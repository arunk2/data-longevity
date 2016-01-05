package edu.iisc.base.emulator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.iisc.base.emulator.exception.ProcessorException;
import edu.iisc.base.emulator.exception.ProcessorExceptionType;

/*
 * 20 fields in ELF header
 */
public class ELFReader {
	
	// 'elf' is an integer array which stores the values of the elf header
	private static final int ELF_HEADER_DETAILS	= 20;
	private int[] elf = new int[ELF_HEADER_DETAILS];
	private String elfFileName;	//File name

	private AddressSpace addressSpace;
	public int startAddress = 0;
	
	private static final int MAGIC_I 		= 0;
	private static final int ARCH_32_64_I	= 1;
	private static final int ENDIAN_I		= 2;
	private static final int VERSION_I		= 3;
	private static final int OS_I			= 4;
	private static final int ABI_I			= 5;
	private static final int PADDING_I		= 6;
	private static final int TYPE_I			= 7;
	private static final int MACHINE_I		= 8;
	private static final int VERSION_1_I	= 9;
	private static final int ENTRY_I		= 10;
	private static final int PHOFF_I		= 11;
	private static final int SHOFF_I		= 12;
	private static final int FLAGS_I		= 13;
	private static final int EHSIZE_I		= 14;
	private static final int PHENTSIZE_I	= 15;
	private static final int PHNUM_I		= 16;
	private static final int SHENTSIZE_I	= 17;
	private static final int SHNUM_I		= 18;
	private static final int SHSTRNDX_I		= 19;
	
	private static final int LITTLE_ENDIAN	= 1;
	private static final int BIG_ENDIAN		= 2;
	
	public ELFReader(AddressSpace addressSpace, String file) {
		this.addressSpace = addressSpace;
		elfFileName = file;
	}
		
	public static void main(String[] args) {
		AddressSpace addr = new AddressSpace();
		addr.addRegion(new MainMemory(CONSTANTS.RAM_BASE, CONSTANTS.RAM_SIZE/10));
		
		ELFReader reader = new ELFReader(addr, "");
		reader.loadFile ();
		reader.processELFFile();
		
	}

	public void processELFFile() {
		
		byte data;
		// Find the size of the file, getsize returns 0 if an invalid name was
		// given
		int filesize = getsize(elfFileName);
		// 'file' is a character array which will store the entire elf file
		// 'file' will remain unchanged throughout the program
		byte[] file = new byte[filesize];
		System.out.println("filesize = " + filesize);
		
		// If we have a valid file
		if (filesize > 0) {
			
			try {
				// copy all data from our source file into 'file'
				FileInputStream fis = new FileInputStream(elfFileName);
				DataInputStream dis = new DataInputStream(fis);
				for (int i = 0; i < filesize; i++) {
					data = (byte) dis.readUnsignedByte();
					file[i] = data;
				}
			} catch (IOException e) {
				System.out.println("Could not write 'file'");
			}
			
			decodeELF (file);
			
			// prints ELF header, program headers and section headers
			printfileinfo (elf, file);
			startAddress = elf[ENTRY_I];
			
			//memoryDump ();
			
			System.out.printf ("\n\nEntry point Address : %x", elf[ENTRY_I]);
			///////////////// System.out.println(runprog(ram, elf[3]));
		}
		// If we have an invalid file name, the program automatically ends
		else
			System.out.println("Invalid file name... sorry");
		
	}

	private void loadFile() {
		// Read the name of file from standard input into String s
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("Enter the name of an executable file: ");
		try {
			elfFileName = in.readLine();
		} catch (IOException e) {
			System.out.println("Error reading filename");
		}
		
	}

	private void decodeELF(byte[] file) {

		// Populate 'elf' with values of elf header information
		elf[MAGIC_I] = decconvert(file, 0x0, 4);
		elf[ARCH_32_64_I] = decconvert(file, 0x4, 1);
		elf[ENDIAN_I] = decconvert(file, 0x5, 1);
		elf[VERSION_I] = decconvert(file, 0x6, 1);
		elf[OS_I] = decconvert(file, 0x7, 1);
		elf[ABI_I] = decconvert(file, 0x8, 1);
		elf[PADDING_I] = decconvert(file, 0x9, 7);
		elf[TYPE_I] = decconvert(file, 0x10, 2);
		elf[MACHINE_I] = decconvert(file, 0x12, 2);
		elf[VERSION_1_I] = decconvert(file, 0x14, 4);
		elf[ENTRY_I] = decconvert(file, 0x18, 4);
		elf[PHOFF_I] = decconvert(file, 0x1C, 4);
		elf[SHOFF_I] = decconvert(file, 0x20, 4);
		elf[FLAGS_I] = decconvert(file, 0x24, 4);
		elf[EHSIZE_I] = decconvert(file, 0x28, 2);
		elf[PHENTSIZE_I] = decconvert(file, 0x2A, 2);
		elf[PHNUM_I] = decconvert(file, 0x2C, 2);
		elf[SHENTSIZE_I] = decconvert(file, 0x2E, 2);
		elf[SHNUM_I] = decconvert(file, 0x30, 2);
		elf[SHSTRNDX_I] = decconvert(file, 0x32, 2);
		
//		FileWriter f0;
//		try {
//			f0 = new FileWriter("memory.txt");

			// For each section header, copy contents of its section from 'file'
			// to 'ram'
			for (int i = 0; i < elf[SHNUM_I]; i++) {
				int base = elf[SHOFF_I] + elf[SHENTSIZE_I] * i;
				int addr = decconvert(file, base + 12, 4);
				int off = decconvert(file, base + 16, 4);
				int size = decconvert(file, base + 20, 4);
				// don't look at sections starting at zero
				// there can be one or more of these, and we don 't care about
				// them
				if (addr != 0) {
					

					for (int j = 0; j < size; j++) {
						
						writemem(addr + j, file[off + j], 1);
//						String s = String.format("%x : %x\n", addr + j,
//								file[off + j]);
//						f0.write(s);
					}

				}
			}
//			f0.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	// returns integer value of 1,2 or 4 byte piece of information in 'file'
	private int decconvert(byte[] source, int loc, int size) {
		int result = 0;
		
		if (elf[ENDIAN_I] == BIG_ENDIAN) {
			switch (size) {
			case 1:
				result = (int) source[loc];
				break;
			case 2:
				result = (((int) source[loc]<<8) | (((int)source[loc+1]) & 0x000000FF));
				break;
			case 4:
				result = ( ((int) source[loc]<<24) 
						| (((int)source[loc+1] << 16) & 0x00FF0000)
						| (((int)source[loc+2] << 8) & 0x0000FF00) 
						| (((int)source[loc+3]) & 0x000000FF));
				break;
			default:
				break;
			}
		}
		else {
			switch (size) {
			case 1:
				result = (int) source[loc];
				break;
			case 2:
				result = (((int) source[loc+1]<<8) | (((int)source[loc]) & 0x000000FF));
				break;
			case 4:
				result = ( ((int) source[loc+3]<<24) 
						| (((int)source[loc+2] << 16) & 0x00FF0000)
						| (((int)source[loc+1] << 8) & 0x0000FF00) 
						| (((int)source[loc]) & 0x000000FF));
				break;
			default:
				break;
			}
			
		}

		return result;
	}

	// method which controls writing to memory
	public void writemem(int loc, int info, int size) {
		try {

			if (size == 1) {
				addressSpace.setByte(loc, ((byte) info));
			} else
				System.out.println("Size error for writemem");
			
		}
		catch (ProcessorException exc) {
			//Incase of TLB Miss try loading again
			if(exc.getType() == ProcessorExceptionType.UTLBMISSEXCEPTION) {
				
				addressSpace.processor.exception(Processor.UTLBMISSEXCEPTION);
				
				if (size == 1) {
					addressSpace.setByte(loc, ((byte) info));
				} else
					System.out.println("Size error for writemem");
			}
		}
	}

	// if 'filename' is invalid, getsize returns 0, otherwise it returns the
	// size of the file (in bytes)
	public int getsize(String filename) {
		long sizecounter = 0;
		File file =new File(filename);
		if(file.exists()){
			sizecounter = file.length();
		}
		
		return (int)sizecounter;
	}
	
	// prints out all information in ELF header, 
	// program headers and section headers
	public void printfileinfo(int[] elf, byte[] file) {
				
		// Print Headers
		System.out.println("\nELF HEADER\nMAGIC: "+elf[MAGIC_I]);
		System.out.println("ARCHITECTURE : "+elf[ARCH_32_64_I]);
		System.out.println("ENDIAN : "+elf[ENDIAN_I]);
		System.out.println("Type : "+elf[TYPE_I]);
		System.out.println("Machine : "+elf[MACHINE_I]);
		System.out.printf("Entry Address: 0x%x", elf[ENTRY_I]);
		System.out.println("\nStart of program headers: " + elf[PHOFF_I] + " (bytes)");
		System.out.println("Start of section headers: " + elf[SHOFF_I] + " (bytes)");
		System.out.printf("Flags: 0x%x", elf[FLAGS_I]);
		System.out.println("\nSize of ELF Header: " + elf[EHSIZE_I] + " (bytes)");
		System.out.println("Size of program headers: " + elf[PHENTSIZE_I] + " (bytes)");
		System.out.println("Number of program headers: " + elf[PHNUM_I]);
		System.out.println("Size of section headers: " + elf[SHENTSIZE_I] + " (bytes)");
		System.out.println("Number of section headers: " + elf[SHNUM_I]);
		System.out.println("Section Header String Table Index: " + elf[SHSTRNDX_I]);
		
				
		for (int i = 0; i < elf[PHNUM_I]; i++) {
			int base = elf[PHOFF_I] + elf[PHENTSIZE_I] * i;
			System.out.println("\n" + "Program Header " + (i + 1) + " of "
					+ elf[PHNUM_I]);
			System.out.printf("Type Code: %x\n", decconvert(file, base, 4));
			System.out.printf("Offset: 0x%x\n", decconvert(file, base + 4, 4));
			System.out.printf("Virtual Memory Address: 0x%x\n",
					decconvert(file, base + 8, 4));
			System.out.printf("Physical Address: 0x%x\n",
					decconvert(file, base + 12, 4));
			System.out.printf("Bytes in file image: 0x%x\n",
					decconvert(file, base + 16, 4));
			System.out.printf("Bytes in memory image: 0x%x\n",
					decconvert(file, base + 20, 4));
			System.out.printf("Flags: 0x%x\n", decconvert(file, base + 24, 4));
			System.out.printf("Segment Alignment: 0x%x\n",
					decconvert(file, base + 28, 4));
		}
		
		for (int i = 0; i < elf[SHNUM_I]; i++) {
			int base = elf[SHOFF_I] + elf[SHENTSIZE_I] * i;
			System.out.println("\n" + "Section Header " + (i + 1) + " of "
					+ elf[SHNUM_I]);
			System.out.println("Name Index: " + decconvert(file, base, 4));
			System.out.printf("Type Code: %x\n", decconvert(file, base + 4, 4));
			System.out.printf("Flags: 0x%x\n", decconvert(file, base + 8, 4));
			System.out
					.printf("Address: 0x%x\n", decconvert(file, base + 12, 4));
			System.out.printf("Offset: 0x%x\n", decconvert(file, base + 16, 4));
			System.out.printf("Size: 0x%x\n", decconvert(file, base + 20, 4));
			System.out.printf("Link: 0x%x\n", decconvert(file, base + 24, 4));
			System.out.printf("Info: 0x%x\n", decconvert(file, base + 28, 4));
			System.out.printf("Section Alignment: 0x%x\n",
					decconvert(file, base + 32, 4));
			System.out.printf("Entry Size: 0x%x\n",
					decconvert(file, base + 36, 4));
		}
		System.out.println();
	}


}
