package edu.iisc.base.emulator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class VRAMTest {

	private String elfFileName; // File name

	private AddressSpace addressSpace;
	public int startAddress = 0;

	public VRAMTest(AddressSpace addressSpace, String file) {
		this.addressSpace = addressSpace;
		elfFileName = file;
	}

	public static void main(String[] args) {
		AddressSpace addr = new AddressSpace();
		addr.addRegion(new MainMemory(CONSTANTS.RAM_BASE,
				CONSTANTS.RAM_SIZE / 10));

		VRAMTest reader = new VRAMTest(addr, "");
		reader.loadFile();
		reader.load();

	}

	public void load() {
			
		try {
			// copy all data from our source file into 'file'
			FileInputStream fis = new FileInputStream(elfFileName);
			DataInputStream dis = new DataInputStream(fis);
			
//			Byte line;
			String line;
			
			int addr = 0;
			while ((line = dis.readLine()) != null) {
//			while ((line = dis.readByte()) != null) {

				String[] dtls = line.split(" : ");
				if (dtls[0].equalsIgnoreCase("setIOPortByte")) {
					Integer port = new Integer(dtls[1]);
					byte data = new Byte(dtls[2]);
					//writemem(addr1, data, 1);
					CPU.getInstance().video.setIOPortByte(port, data);
				}
				else {
					Integer addr1 = new Integer(dtls[0]);
					Integer data = new Integer(dtls[1]);
					writemem(addr1, data, 1);
				}
				
//				writemem(addr, line, 1);
				addr++;
			}
			
		} catch (IOException e) {
			System.out.println("Could not write 'file'");
		}
		
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

	// method which controls writing to memory
	public void writemem(int loc, int info, int size) {

		if (size == 1) {
			addressSpace.setByte(loc, ((byte) info));
		} else
			System.out.println("Size error for writemem");

	}

}
