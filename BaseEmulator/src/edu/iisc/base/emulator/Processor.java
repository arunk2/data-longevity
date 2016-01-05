package edu.iisc.base.emulator;

import java.util.ArrayList;
import java.util.List;

import edu.iisc.base.emulator.exception.ProcessorException;
import edu.iisc.base.emulator.exception.ProcessorExceptionType;

/**
 * MIPS R2000 processor - Emulation
 * 
 * @author casl
 *
 */
public class Processor {
	/////////////
//	public File file;
//	public FileWriter writer;
    /////////////
	
	public static final int SIGN_BIT = 0x80000000;
	
	private static final int REGISTERS_COUNT= 32;
	
	// exceptions
	public static final int UTLBMISSEXCEPTION = -1; // not a true exception,
											 // has its own handler
	public final int EXTINTERRUPT = 0;
	public final int TLBMODIFIED = 1;
	public final int TLBLOADMISS = 2;
	public final int TLBSTOREMISS = 3;
	public final int ADDRERRORLOAD = 4;
	public final int ADDRERRORSTORE = 5;
	public final int BUSERRORFETCH = 6;
	public final int BUSERRORDATA = 7;
	public final int SYSCALL = 8;
	public final int BREAKPOINT = 9;
	public final int RESINSTRUCTION = 10;
	public final int CPUNUSABLE = 11;
	public final int OVERFLOW = 12;
	
	public AddressSpace addressSpace;			// Address space
	
	private SystemClock systemClock;
	private ALU alu = new ALU();
	private Register MAR = new Register(); 		// Memory Address Register
	private Register MDR = new Register();		// Memory Data Register

	private Register IR  = new Register();		// Instruction Register
	private Register PC  = new Register(); 		// Program counter

	private Register[] R; // General Registers
	
	private SystemCall syscallHandler = new SystemCall();
	
	private Register HI  = new Register();
	private Register LO  = new Register();

	Instruction currentInstruction;
	//Co-Processor 0 for VM and Exception handling
	public CoPROCESSOR_0 CP0;
	//Co-Processor 1 for Floating point manipulation
	private FPU CP1;
	
	List<MemoryRegion> interruptList;
	
	private int mode;	//0 - user mode  and 1 - kernel mode
	private int ALUTemp;

	/*
	 * Check for User mode
	 */
	public boolean isUserMode() {
		return (mode == 0);
	}
	
	/*
	 * Check for Kernel mode
	 */
	public boolean isKernelMode() {
		return (mode == 1);
	}

	/**
	 * Constructor for new Processor
	 */
	public Processor(AddressSpace addressSpace) {
		
		//////////////
//		try {
//			  file = new File("Hello1.txt");
//		      // creates the file
//		      file.createNewFile();
//		      // creates a FileWriter Object
//		      writer = new FileWriter(file); 
//		      
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
		///////////////
	      
		//get the connected address space
		this.addressSpace = addressSpace;
		this.addressSpace.processor = this; //link processor with addr-space
		
		//initialize system clock and other processor components
		systemClock = new SystemClock();
		systemClock.setProcessor(this);
		CP0 = new CoPROCESSOR_0(this);
		CP1 = new FPU();
		
		R = new Register[REGISTERS_COUNT];
		for (int i = 0; i < REGISTERS_COUNT; i++)
			R[i] = new Register();
		CP1.F = new Register[REGISTERS_COUNT];
		for (int i = 0; i < REGISTERS_COUNT; i++)
			CP1.F[i] = new Register();
		
		//Load the base code for begining
		Loader loader = new Loader(this);
		loader.loadApplication("txt_reader.o");	
	}
	
	/**
	 * Set PC
	 */
	public void setPC(Integer address) {
		PC.setValue(address);
	}
	
	/**
	 * Set SP
	 */
	public void setSP(Integer address) {
		R[CONSTANTS.R_SP].setValue(address);
	}

	/**
	 * Initialize the program counter to start from begining
	 */
	public void resetProgramCounter() {
		PC.setValue(0);
	}
	
	/**
	 * Increment the Program counter by 4, as each instruction is 32 bit size
	 */
	public void incrementProgramCounter() {
		PC.setValue(PC.getValue()+4);
	}

	/**
	 * Execute Instruction pointed by IP register
	 */
	public void executeInstruction() {
		
		try {
			
			fetch();
			decode();
			execute();
//			write();
			
		} catch (ProcessorException exception) {
			
			// Handle exception
			switch (exception.getType()) {
				case EXTERNAL_INTRERRUPT:
					//handle external interrupts
					exception(EXTINTERRUPT);
					break;
				case TLB_MODIFICATION:
					//handle external interrupts
					exception(TLBMODIFIED);
					break;
				case TLB_MISS_LOAD:
					//handle external interrupts
					exception(TLBLOADMISS);
					break;
				case TLB_MISS_STORE:
					//handle external interrupts
					exception(TLBSTOREMISS);
					break;
				case ADDRESS_ERROR_LOAD:
					//handle external interrupts
					exception(ADDRERRORLOAD);
					break;
				case ADDRESS_ERROR_STORE:
					//handle external interrupts
					exception(ADDRERRORSTORE);
					break;
				case BUS_ERROR_INST:
					//handle external interrupts
					exception(BUSERRORFETCH);
					break;
				case BUS_ERROR_DATA:
					//handle external interrupts
					exception(BUSERRORDATA);
					break;
				case SYSCALL:
					//handle external interrupts
					exception(SYSCALL);
					break;
				case BREAKPOINT:
					//handle external interrupts
					exception(BREAKPOINT);
					break;
				case RESERVED_INST:
					//handle external interrupts
					exception(RESINSTRUCTION);
					break;
				case COPROCESSOR_UNUSABLE:
					//handle external interrupts
					exception(CPUNUSABLE);
					break;
				case ARITHMETIC_OVERFLOW:
					//handle external interrupts
					exception(OVERFLOW);
					break;
				case UTLBMISSEXCEPTION:
					//handle external interrupts
					exception(UTLBMISSEXCEPTION);
					break;
				default:
					break;
			}

		}
		incrementProgramCounter();
	}
	
	/**
	 * Fetch(address)
	 * @return
	 */
	private boolean fetch() {
		
		//Load address pointed by PC into MAR
		fetch(PC.getValue());
		IR.setValue(MDR.getValue());
		return true;
	}
	
	private boolean fetch(int address) {
		
		//Load address into MAR
		MAR.setValue(address);
		
		//Decode the address in MAR
		//Copy the content of memory cell with specified address into MDR.
		int value = addressSpace.getWord(address);
		
		System.out.printf("%x : %x\n", address, value);
		
		///////////////
//		String formattedString = String.format("%x : %x\n", address, value);  
//		if(address == 0x401b58)
//			System.out.println("---");
//		
//	    // Writes the content to the file
//	    try {
//			writer.write(formattedString);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	   //////////////
		
		MDR.setValue(value);
		return true;
	}

	private boolean decode() {
		
		currentInstruction = new Instruction(IR.getValue()); 
		currentInstruction.decode();		
		return false;
	}
	
	private boolean execute() {

		executeInst();
		return true;
	}	

	private void executeInst() {
		
		switch (currentInstruction.opcode) {

		case 0: {
			switch (currentInstruction.funct) {
			case 0:
				//nop - no operation
				if (currentInstruction.instruction == 0)
					;//no operation
				else {
				// sll - shift left logical (sll rd, rt, shamt)
				ALUTemp = R[currentInstruction.rt].getValue() << currentInstruction.shamt;
				R[currentInstruction.rd].setValue(ALUTemp);
				}
				break;
			case 1:
				// movf - move conditional on FP false (movf rd, rs, cc)
				if (currentInstruction.rt == 0)
					R[currentInstruction.rd].setValue(R[currentInstruction.rs].getValue());
				// movt
				else if (currentInstruction.rt == 4) //conditional code top 3 bits in RT. To check for its value as 1, we added 2 zeros to end (per instrn)
					R[currentInstruction.rd].setValue(R[currentInstruction.rs].getValue());
				
				break;
			case 2:
				// srl - shift right logical (srl rd, rt, shamt)
				ALUTemp = R[currentInstruction.rt].getValue() >> currentInstruction.shamt;
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 3:
				// sra - shift right arithmetic (sra rd, rt, shamt)
				ALUTemp = R[currentInstruction.rt].getValue() >> currentInstruction.shamt;
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 4:
				// sllv - shift left logical variable (sllv rd, rt, rs)
				ALUTemp = R[currentInstruction.rt].getValue() << R[currentInstruction.rs].getValue();
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 6:
				// srlv - shift right logical variable (srlv rd, rt, rs)
				ALUTemp = R[currentInstruction.rt].getValue() >> R[currentInstruction.rs].getValue();
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 7:
				// srav - shift right arithmetic variable (srav rd, rt, rs)
				ALUTemp = R[currentInstruction.rt].getValue() >> R[currentInstruction.rs].getValue();
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 8:
				// jr - jump register (jr rs)
				
				int target = R[currentInstruction.rs].getValue() - 4;//adjust for 2 extra instn
				
				incrementProgramCounter();
				executeInstruction();
				
				PC.setValue(target);  //////////////
				printProcessorState();
				break;
			case 9:
				// jalr - jump and link register (jalr rs, rd)
				
				int destReg = currentInstruction.rd;
				target = R[currentInstruction.rs].getValue() - 4;
				
				incrementProgramCounter();
				executeInstruction();
				
				R[destReg].setValue(PC.getValue()+4);
				PC.setValue(target);
				printProcessorState();
				break;
			case 10:
				// movz - move conditional zero (movn rd, rs, rt)
				if (R[currentInstruction.rt].getValue() == 0)
					R[currentInstruction.rd].setValue(R[currentInstruction.rs].getValue());
				break;
			case 11:
				// movn - move conditional not zero (movn rd, rs, rt)
				if (R[currentInstruction.rt].getValue() != 0)
					R[currentInstruction.rd].setValue(R[currentInstruction.rs].getValue());
				break;
			case 12:
				// syscall - system call (syscall)
				throw new ProcessorException(ProcessorExceptionType.SYSCALL);
				
			case 13:
				// break - break (break code)
				throw new ProcessorException(ProcessorExceptionType.BREAKPOINT);
				
			case 15:
				// sync
				break;
			case 16:
				// mfhi - move from hi (mfhi rd)
				R[currentInstruction.rd].setValue(HI.getValue());
				break;
			case 17:
				// mthi - move to hi (mfhi rs)
				HI.setValue(R[currentInstruction.rs].getValue());
				break;
			case 18:
				// mflo - move from lo (mflo rd)
				R[currentInstruction.rd].setValue(LO.getValue());
				break;
			case 19:
				// mtlo - move to lo (mfhi rs)
				HI.setValue(R[currentInstruction.rs].getValue());
				break;
			case 24:
				// mult - multiply (mul rs, rt)
				alu.mul(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				LO.setValue(alu.resLo);
				HI.setValue(alu.resHi);
				break;
			case 25:
				// multu - unsigned multiply (mul rs, rt)
				alu.mul(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				LO.setValue(alu.resLo);
				HI.setValue(alu.resHi);
				break;
			case 26:
				// div - divide with overflow (div rs, rt)
				alu.div(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				LO.setValue(alu.resLo);
				HI.setValue(alu.resHi);
				break;
			case 27:
				// divu - divide without overflow (divu rs, rt)
				alu.div(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				LO.setValue(alu.resLo);
				HI.setValue(alu.resHi);
				break;
			case 32:
				// add - addition with overflow (add rd, rs, rt)
				alu.add(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				R[currentInstruction.rd].setValue(alu.res);
				break;
			case 33:
				// addu - addition without overflow (add rd, rs, rt)
				alu.add(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				R[currentInstruction.rd].setValue(alu.res);
				break;
			case 34:
				// sub - subtraction with overflow (sub rd, rs, rt)
				alu.sub(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				R[currentInstruction.rd].setValue(alu.res);
				break;
			case 35:
				// subu - subtraction without overflow (sub rd, rs, rt)
				alu.sub(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				R[currentInstruction.rd].setValue(alu.res);
				break;
			case 36:
				// and - logical and (and rd, rs, rt)
				ALUTemp = R[currentInstruction.rs].getValue() & R[currentInstruction.rt].getValue();
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 37:
				// or - logical OR (or rd, rs, rt)
				ALUTemp = R[currentInstruction.rs].getValue() | R[currentInstruction.rt].getValue();
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 38:
				// xor - logical XOR (xor rd, rs, rt)
				ALUTemp = R[currentInstruction.rs].getValue() ^ R[currentInstruction.rt].getValue();
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 39:
				// nor - logical NOR (nor rd, rs, rt)
				ALUTemp = ~ (R[currentInstruction.rs].getValue()
							| R[currentInstruction.rt].getValue());
				R[currentInstruction.rd].setValue(ALUTemp);
				break;
			case 42:
				// slt - set less than (slt rd, rs, rt)
				if (R[currentInstruction.rs].getValue() < R[currentInstruction.rt].getValue())
					R[currentInstruction.rd].setValue(1);
				else
					R[currentInstruction.rd].setValue(0);
				break;
			case 43:
				// sltu - set less than unsigned (sltu rd, rs, rt)
				if (R[currentInstruction.rs].getValue() < R[currentInstruction.rt].getValue())
					R[currentInstruction.rd].setValue(1);
				else
					R[currentInstruction.rd].setValue(0);
				break;
			case 48:
				// tge - Trap if greater equal (tge rs, rt)
				if (R[currentInstruction.rs].getValue() >= R[currentInstruction.rt].getValue())
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 49:
				// tgeu - Unsigned Trap if greater equal (tgeu rs, rt)
				if (R[currentInstruction.rs].getValue() >= R[currentInstruction.rt].getValue())
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 50:
				// tlt - Trap if less than (tlt rs, rt)
				if (R[currentInstruction.rs].getValue() < R[currentInstruction.rt].getValue())
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 51:
				// tltu - Unsigned Trap if less than (tltu rs, rt)
				if (R[currentInstruction.rs].getValue() < R[currentInstruction.rt].getValue())
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 52:
				// teq - Trap if equal (teq rs, rt)
				if (R[currentInstruction.rs].getValue().equals(R[currentInstruction.rt].getValue()))
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 54:
				// tne - Trap if not equal (tne rs, rt)
				if (!R[currentInstruction.rs].getValue().equals(R[currentInstruction.rt].getValue()))
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			}
			break;
		}
		case 1: {
			switch(currentInstruction.rt) {
			case 0:
				//bltz - branch on less than zero and link (bltz rs, label)
				if (R[currentInstruction.rs].getValue() < 0) {
					PC.setValue(PC.getValue()+ (currentInstruction.immediat << 2)); //shift immediate value by 2 (mul by 4) for word size
				}
				break;
			case 1:
				//bgez - branch on greater than equal zero (bgez rs, label)
				if (R[currentInstruction.rs].getValue() >= 0)
					PC.setValue(PC.getValue()+ (currentInstruction.immediat << 2)); //shift immediate value by 2 (mul by 4) for word size
				break;
			case 2:
				//bltzl
				break;
			case 3:
				//bgezl
				break;
			case 8:
				//tgei - trap if greater equal immediate (tgei rs, imm)
				if (R[currentInstruction.rs].getValue() >= currentInstruction.immediat)
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 9:
				//tgeiu - unsigned trap if greater equal immediate (tgeiu rs, imm)
				if (R[currentInstruction.rs].getValue() >= currentInstruction.immediat)
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 10:
				//tlti - trap if less than immediate (tlti rs, imm)
				if (R[currentInstruction.rs].getValue() < currentInstruction.immediat)
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 11:
				//tltiu - unsigned trap if less than immediate (tlti rs, imm)
				if (R[currentInstruction.rs].getValue() < currentInstruction.immediat)
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 12:
				//teqi - trap if equal immediate (teqi rs, imm)
				if (R[currentInstruction.rs].getValue().equals(currentInstruction.immediat))
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 14:
				//tnei - trap if not equal immediate (tnei rs, imm)
				if (R[currentInstruction.rs].getValue().equals(currentInstruction.immediat))
					throw new ProcessorException(ProcessorExceptionType.EXTERNAL_INTRERRUPT);
				break;
			case 16:
				//bltzal - branch on less than zero and link (bltzal rs, label)
				R[CONSTANTS.R_RA].setValue(PC.getValue()); //save address of next instrn for return value
				if (R[currentInstruction.rs].getValue() < 0) {
					PC.setValue(PC.getValue()+ (currentInstruction.immediat << 2)); //shift immediate value by 2 (mul by 4) for word size
				}
				break;
			case 17:
				//bgezal - branch on greater than equal zero and link (bgezal rs, label)
				
				//backup required details and Execute the next instrn
				int sourceRegister = currentInstruction.rs;
				int target = PC.getValue()+ (currentInstruction.immediat << 2);
				
				incrementProgramCounter();
				executeInstruction();
				
				R[CONSTANTS.R_RA].setValue(PC.getValue()); //save address of next instrn for return value
				
				if (R[sourceRegister].getValue() >= 0) {
					PC.setValue(target); //shift immediate value by 2 (mul by 4) for word size
				}
				else { //reset the PC so that the instruction is executed again
					PC.setValue(PC.getValue()-4);
				}
				printProcessorState();
				break;
			case 18:
				//bltzall
				break;
			case 19:
				//bgczall
				break;

			}
			break;
		}
		case 2: {
			// j - jump (j target)
			/*
			 * Short version: In a 32 bit instruction you can not include a 32-bit jump 
			 * destination. The opcoded uses 6 bits, which leaves 26 bits for the 
			 * instruction. The target address is constructed, by taking the first 4 bits 
			 * of the instruction following the j instruction, then 26 bits from the 
			 * jump instruction operand, and finally 2 zero bits are appended (as the 
			 * instructions are 32 bits, alginment is usefull and allows the omitting 
			 * of the last two 0s).
			 */
			
			int target = currentInstruction.target;
			target = (PC.getValue() & 0xF0000000) | target;
			target <<= 2;
			target -= 4;
			
			incrementProgramCounter();
			executeInstruction();
			
			PC.setValue(target);
			printProcessorState();
			break;
		}
		case 3: {
			// jal - jump and link (jal target) 
			
			int target = currentInstruction.target;
			target = (PC.getValue() & 0xF0000000) | target;
			target <<= 2;
			target -= 4;

			incrementProgramCounter();
			executeInstruction();
			
			R[CONSTANTS.R_RA].setValue(PC.getValue()); //save address of next instrn for return value
			
			PC.setValue(target);
			printProcessorState();
			break;
		}
		case 4: {
			// beq - branch on equal (beq rs, rt, label)
			
			//backup required details and Execute the next instrn
			int sourceRegister = currentInstruction.rs;
			int targetRegister = currentInstruction.rt;
			int target = PC.getValue()+ (currentInstruction.immediat << 2);
			
			boolean cond = false;
			if(R[sourceRegister].getValue().equals(R[targetRegister].getValue()))
				cond = true;
			else
				cond = false;
			
			incrementProgramCounter();
			executeInstruction();
			
			if (cond)
				PC.setValue(target); //shift immediate value by 2 (mul by 4) for word size
			else { //reset the PC so that the instruction is executed again
				PC.setValue(PC.getValue()-4);
			}
			
			printProcessorState();
			break;
		}
		case 5: {
			// bne - branch on not equal (bne rs, rt, label)
			
			//backup required details and Execute the next instrn
			int sourceRegister = currentInstruction.rs;
			int targetRegister = currentInstruction.rt;
			int target = PC.getValue() + (currentInstruction.immediat << 2);
			
			boolean cond = false;
			if(!R[sourceRegister].getValue().equals(R[targetRegister].getValue()))
				cond = true;
			else
				cond = false;
			
			incrementProgramCounter();
			executeInstruction();
			
			if (cond)
				PC.setValue(target); //shift immediate value by 2 (mul by 4) for word size
			else { //reset the PC so that the instruction is executed again
				PC.setValue(PC.getValue()-4);
			}
			
			printProcessorState();
			break;
		}
		case 6: {
			// blez - branch on less than zero (blez rs, label)
			
			//backup required details and Execute the next instrn
			int sourceRegister = currentInstruction.rs;
			int target = PC.getValue()+ (currentInstruction.immediat << 2);

			boolean cond = false;
			if(R[sourceRegister].getValue() <= 0)
				cond = true;
			else
				cond = false;
			
			incrementProgramCounter();
			executeInstruction();
			
			if (cond)
				PC.setValue(target); //shift immediate value by 2 (mul by 4) for word size
			else { //reset the PC so that the instruction is executed again
				PC.setValue(PC.getValue()-4);
			}
			
			printProcessorState();
			break;
			
		}
		case 7: {
			// bgtz - branch on greater than zero (bgtz rs, label)
			
			//backup required details and Execute the next instrn
			int sourceRegister = currentInstruction.rs;
			int target = PC.getValue()+ (currentInstruction.immediat << 2);

			boolean cond = false;
			if(R[sourceRegister].getValue() > 0)
				cond = true;
			else
				cond = false;
			
			incrementProgramCounter();
			executeInstruction();
			
			if (cond)
				PC.setValue(target); //shift immediate value by 2 (mul by 4) for word size
			else { //reset the PC so that the instruction is executed again
				PC.setValue(PC.getValue()-4);
			}

			printProcessorState();
			break;
		}
		case 8: {
			// addi - addition with overflow (addi rt, rs, imm)
			alu.add(R[currentInstruction.rs].getValue(), (int) currentInstruction.immediat);
			R[currentInstruction.rt].setValue(alu.res);
			break;
		}
		case 9: {
			// addiu - addition without overflow (addiu rt, rs, imm)
			alu.add(R[currentInstruction.rs].getValue(), (int) currentInstruction.immediat);
			R[currentInstruction.rt].setValue(alu.res);
			break;
		}
		case 10: {
			// slti - set less than immediate (slti rt, rs, imm)
			if (R[currentInstruction.rs].getValue() < currentInstruction.immediat)
				R[currentInstruction.rt].setValue(1);
			else
				R[currentInstruction.rt].setValue(0);
			break;
		}
		case 11: {
			// sltiu - set less than unsigned immediate (sltiu rt, rs, imm)
			if (R[currentInstruction.rs].getValue() < currentInstruction.immediat)
				R[currentInstruction.rt].setValue(1);
			else
				R[currentInstruction.rt].setValue(0);
			break;
		}
		case 12: {
			// andi - logical and (andi rs, rt, imm)
			ALUTemp = R[currentInstruction.rs].getValue() & currentInstruction.immediat;
			R[currentInstruction.rt].setValue(ALUTemp);
			break;
		}
		case 13: {
			// ori - logical OR (or rt, rs, imm)
			ALUTemp = R[currentInstruction.rs].getValue() | currentInstruction.immediat;
			R[currentInstruction.rt].setValue(ALUTemp);
			break;
		}
		case 14: {
			// xori - logical XOR (xor rt, rs, imm)
			ALUTemp = R[currentInstruction.rs].getValue() ^ currentInstruction.immediat;
			R[currentInstruction.rd].setValue(ALUTemp);
			break;
		}
		case 15: {
			// lui - load upper immediate (lui rt, imm)
			R[currentInstruction.rt].setValue(currentInstruction.immediat << 16);
			break;
		}
		case 17:
		case 18:
		case 16: {
			
			switch (currentInstruction.rs) {
			case 0:
				//mfcz - move from coprocessor 0 (mfcz rt, rd)
				if (currentInstruction.opcode == 16)
					R[currentInstruction.rt].setValue(CP0.R[currentInstruction.rd].getValue());
				else if (currentInstruction.opcode == 17)
					R[currentInstruction.rt].setValue(CP1.F[currentInstruction.rd].getValue());
				break;
			case 2:
				//cfcz
				break;
			case 4:
				//mtcz - move to coprocessor 0 (mtcz rd, rt)
				if (currentInstruction.opcode == 16)
					CP0.R[currentInstruction.rd].setValue(R[currentInstruction.rt].getValue());
				else if (currentInstruction.opcode == 17)
					CP1.F[currentInstruction.rd].setValue(R[currentInstruction.rt].getValue());
				break;
			case 6:
				//ctcz
				break;
			case 8:
				if (currentInstruction.rt == 0) 
					//bczf
					;
				else if (currentInstruction.rt == 0) 
					//bczt
					;
				else if (currentInstruction.rt == 0) 
					//bczfl
				;
				else if (currentInstruction.rt == 0) 
					//bcztl
				;
				break;
				
			case 16:
				if (!CP0.isZero()) {
					switch (currentInstruction.funct) {
					case 1:
						//tlbr
						break;
					case 2:
						//tlbwi
						break;
					case 6:
						//tlbwr
						break;
					case 8:
						//tlbp
						break;
					case 24:
						//eret - exception return (eret)
						if (currentInstruction.opcode == 16) {
							CP0.resetStatusEXL();
							PC.setValue(CP0.R[CoPROCESSOR_0.REG_EPC].getValue());
						}
						break;
					case 31:
						//dret
						break;
					}
				}
				else {
					switch (currentInstruction.funct) {
					case 0:
						//add.f
						break;
					case 1:
						//sub.f
						break;
					case 2:
						//mul.f
						break;
					case 3:
						//div.f
						break;
					case 4:
						//sqrt.f
						break;
					case 5:
						//abs.f
						break;
					case 7:
						//neg.f
						break;
					case 12:
						//round.w.f
						break;
					case 13:
						//trunc.w.f
						break;
					case 14:
						//cell.w.f
						break;
					case 15:
						//floor.f
						break;
					case 17:
						if (currentInstruction.rt == 0)
							//movf.f
							;
						else if (currentInstruction.rt == 1)
							//movt.f
							;
						break;
						
					}
				}
				break;
			case 17:
				if (CP0.isZero()) {
					switch (currentInstruction.funct) {

					case 0:
						//add.f
						break;
					case 1:
						//sub.f
						break;
					case 2:
						//mul.f
						break;
					case 3:
						//div.f
						break;
					case 4:
						//sqrt.f
						break;
					case 5:
						//abs.f
						break;
					case 7:
						//neg.f
						break;
					case 12:
						//round.w.f
						break;
					case 13:
						//trunc.w.f
						break;
					case 14:
						//cell.w.f
						break;
					case 15:
						//floor.f
						break;
					case 17:
						if (currentInstruction.rt == 0)
							//movf.f
							;
						else if (currentInstruction.rt == 1)
							//movt.f
							;
						break;
					
					}
				}
				break;
				
			}

			break;
		}
		case 20: {
			// beql
			break;
		}
		case 21: {
			// bnel
			break;
		}
		case 22: {
			// blezl
			break;
		}
		case 23: {
			// bgtzl
			break;
		}
		case 28: {
			switch (currentInstruction.funct) {
			case 0:
				//madd - multiply and add to LO & HI (mul rs, rt)
				alu.mul(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				try {
					alu.add(LO.getValue(), alu.resLo);
					LO.setValue(alu.res);
					alu.add(HI.getValue(), alu.resHi);
					HI.setValue(alu.res);
				}
				catch (Exception e) {
					LO.setValue(LO.getValue()+alu.resLo);
					HI.setValue(HI.getValue()+alu.resHi+1);
				}
				break;
			case 1:
				//maddu - multiply and add to LO & HI (mul rs, rt)
				alu.mul(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				try {
					alu.add(LO.getValue(), alu.resLo);
					LO.setValue(alu.res);
					alu.add(HI.getValue(), alu.resHi);
					HI.setValue(alu.res);
				}
				catch (Exception e) {
					LO.setValue(LO.getValue()+alu.resLo);
					HI.setValue(HI.getValue()+alu.resHi+1);
				}
				break;
			case 2:
				//mul - multiply without overflow (mul rd, rs, rt)
				alu.mul(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				R[currentInstruction.rd].setValue(alu.resLo);
				break;
			case 4:
				//msub - multiply and subtract to LO & HI (mul rs, rt)
				alu.mul(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				try {
					alu.sub(LO.getValue(), alu.resLo);
					LO.setValue(alu.res);
					alu.sub(HI.getValue(), alu.resHi);
					HI.setValue(alu.res);
				}
				catch (Exception e) {
					LO.setValue(LO.getValue()-alu.resLo);
					HI.setValue(HI.getValue()-alu.resHi-1);
				}
				break;
			case 5:
				//msubu - multiply and subtract to LO & HI (mul rs, rt)
				alu.mul(R[currentInstruction.rs].getValue(), R[currentInstruction.rt].getValue());
				try {
					alu.sub(LO.getValue(), alu.resLo);
					LO.setValue(alu.res);
					alu.sub(HI.getValue(), alu.resHi);
					HI.setValue(alu.res);
				}
				catch (Exception e) {
					LO.setValue(LO.getValue()-alu.resLo);
					HI.setValue(HI.getValue()-alu.resHi-1);
				}
				break;
			case 32:
				//clz - count leading zeros (cloz rd, rs)
				alu.countZeros(R[currentInstruction.rs].getValue());
				R[currentInstruction.rd].setValue(alu.res);
				break;
			case 33:
				//clo - count leading ones (clo rd, rs)
				alu.countOnes(R[currentInstruction.rs].getValue());
				R[currentInstruction.rd].setValue(alu.res);
				break;
				
			}
			break;
		}
		case 32: {
			// lb - load byte (lb rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat_unsigned_byte;
			R[currentInstruction.rt].setValue(addressSpace.getByte(address));
			break;
		}
		case 33: {
			// lh - load halfword (lh rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			R[currentInstruction.rt].setValue(addressSpace.getHalfWord(address));
			break;
		}
		case 34: {
			// lwl
			break;
		}
		case 35: {
			// lw - load word (lw rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			R[currentInstruction.rt].setValue(addressSpace.getWord(address));
			break;
		}
		case 36: {
			// lbu - load unsigned byte (lbu rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			R[currentInstruction.rt].setValue(addressSpace.getByte(address));
			break;
		}
		case 37: {
			// lhu - load unsigned halfword (lhu rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			R[currentInstruction.rt].setValue(addressSpace.getHalfWord(address));
			break;
		}
		case 38: {
			// lwr
			break;
		}
		case 40: {
			// sb - store byte (sb rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat_unsigned_byte;
			addressSpace.setByte(address, R[currentInstruction.rt].getByte());
			break;
		}
		case 41: {
			// sh - store halfword (sh rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			addressSpace.setHalfWord(address, R[currentInstruction.rt].getHalfWord());
			break;
		}
		case 42: {
			// swl
			break;
		}
		case 43: {
			// sw - store word (sh rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			addressSpace.setWord(address, R[currentInstruction.rt].getValue());
			break;
		}
		case 46: {
			// swr
			break;
		}
		case 47: {
			// cache
			break;
		}
		case 48: {
			// ll - load linked (ll rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			R[currentInstruction.rt].setValue(addressSpace.getWord(address));
			//A store conditional instr is needed. since we have a single processor we can ignore it
			break;
		}
		case 49: {
			// lwc1  - load word coprocessor 1 (lwcl ft, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			CP1.F[currentInstruction.rt].setValue(addressSpace.getWord(address));
			break;
		}
		case 50: {
			// lwc2
			break;
		}
		case 51: {
			// pref
			break;
		}
		case 53: {
			// ldc1
			break;
		}
		case 54: {
			// ldc2
			break;
		}
		case 56: {
			// sc - store conditional (sc rt, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			addressSpace.setWord(address, R[currentInstruction.rt].getValue());
			R[currentInstruction.rt].setValue(1); //since we have 1 processor atomicity is guranteed
			break;
		}
		case 57: {
			// swc1 - store word coprocessor 1 (swc1 ft, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			addressSpace.setWord(address, CP1.F[currentInstruction.rt].getValue());
			break;
		}
		case 58: {
			// swc2
			break;
		}
		case 61: {
			// sdc1 - store double coprocessor 1 (sdc1 ft, address)
			Integer address = R[currentInstruction.rs].getValue() + currentInstruction.immediat;
			addressSpace.setWord(address, CP1.F[currentInstruction.rt].getValue());
			addressSpace.setWord(address+4, CP1.F[currentInstruction.rt].getValue());
			break;
		}
		case 62: {
			// sdc2
			break;
		}

		}
		
	}

	private boolean write() {
		
		if (currentInstruction.type == INSTRUCTION_TYPE.R_TYPE) {
			store(currentInstruction.immediat, ALUTemp);
		}
		else if (currentInstruction.type == INSTRUCTION_TYPE.R_TYPE) {
			store(R[currentInstruction.rd].getValue(), ALUTemp);
		}
		else {
			//Do nothing
		}
		return true;
	}
	
	/**
	 * Store(address, value)
	 * @return
	 */
	private boolean store(int address, int value) {
		
		//Load the address into MAR.
		MAR.setValue(address);
		
		//Load the value into MDR.
		////MDR.setValue(value);
		
		//Decode the address in MAR
		//Copy the content of MDR into memory cell with the specified address.
		addressSpace.setWord(address, value);
		
		return true;
	}

	public void startProcess() {

		//Init VRAM operations
		VRAMTest vramtest = new VRAMTest(addressSpace, "setport-setup.txt");
		vramtest.load();
		
		//reset vram loc
		for (int loc = 0xA0000; loc <0xC0000; loc++) {
			if (loc % 2 == 0)
				addressSpace.setByte(loc, ((byte) 32));
			else
				addressSpace.setByte(loc, ((byte) 7));
		}
		
		//setup display
		Display display = Display.getInstance();
		display.setAddressSpace(addressSpace);
		
		systemClock.startProcess();
	}
	
	// external interrupts: exception 0, ip_no save oldPc for storing in EPC
//	public void interrupt(int no) {
//		boolean branchDelay = isBranchDelaySlot();
//		int oldPC = PC.getValue();
////		int oldPC = pc;
//		
//		System.out.println("hardware interrupt " + no + "\n");
//
//		PC.setValue(ProcessorException.ADDRESS_GENERAL);
////		pc = ProcessorException.ADDRESS_GENERAL;
//		CP0.interrupt(0, no, oldPC, branchDelay);
//	}
	
	// other exceptions
	public void exception(int exception) {

		// Handle all interrrupts with simpler hardcoded routines
		switch (exception) {
		case EXTINTERRUPT:
			break;
		case TLBMODIFIED:
			break;
		case TLBLOADMISS:
			break;
		case TLBSTOREMISS:
			break;
		case ADDRERRORLOAD:
			break;
		case ADDRERRORSTORE:
			break;
		case BUSERRORFETCH:
			break;
		case BUSERRORDATA:
			break;
		case SYSCALL:
			handleSYSCALL();
			break;
		case BREAKPOINT:
			break;
		case RESINSTRUCTION:
			break;
		case CPUNUSABLE:
			break;
		case OVERFLOW:
			break;
		case UTLBMISSEXCEPTION:
			break;
		}

	}
	
	private void handleSYSCALL() {

		//Get the syscall number from V0 register
		int syscallNo = R[CONSTANTS.R_V0].getValue();
		syscallNo = syscallNo - 4000;
		//Get no.of arguments
		int argsCount = syscallHandler.mips_syscall_args[syscallNo];
		
		//Depending upon no.of arguments fetch details from a0-a3 registers
		//fetch arguments, pack along with syscall no and call routines
		List<Integer> arguments = new ArrayList<Integer>();
		arguments.add(syscallNo);
		switch(argsCount) {
		case 0:	//No arguments
			break;
		case 1:
			arguments.add(R[CONSTANTS.R_A0].getValue());
			break;
		case 2:
			arguments.add(R[CONSTANTS.R_A0].getValue());
			arguments.add(R[CONSTANTS.R_A1].getValue());
			break;
		case 3:
			arguments.add(R[CONSTANTS.R_A0].getValue());
			arguments.add(R[CONSTANTS.R_A1].getValue());
			arguments.add(R[CONSTANTS.R_A2].getValue());
			break;
		case 4:
			arguments.add(R[CONSTANTS.R_A0].getValue());
			arguments.add(R[CONSTANTS.R_A1].getValue());
			arguments.add(R[CONSTANTS.R_A2].getValue());
			arguments.add(R[CONSTANTS.R_A3].getValue());
			break;
		}
		
		int ret = syscallHandler.doSyscall(arguments);
		R[CONSTANTS.R_V0].setValue(ret);
		//if exit syscall is called 
		if (syscallNo == 1)
			systemClock.stopRunning();
	}
	
	void checkInterrupts() {
		
//		for (int i = 0; i < interruptList.size(); i++) {
//			if (CP0.interruptEnabled(i)) {
//				((MemoryRegion) interruptList.get(i))
//						.checkInterrupt(this);
//			}
//		}
		
	}
	
	public void printProcessorState() {
		System.out.println("Registers:");
		System.out.printf("[Ze] : %x  : ",R[CONSTANTS.R_ZERO].getValue());
		System.out.printf("[AT] : %x  : ",R[CONSTANTS.R_AT].getValue());
		System.out.printf("[V0] : %x  : ",R[CONSTANTS.R_V0].getValue());
		System.out.printf("[V1] : %x  : ",R[CONSTANTS.R_V1].getValue());
		System.out.printf("\n[A0] : %x  : ",R[CONSTANTS.R_A0].getValue());
		System.out.printf("[A1] : %x  : ",R[CONSTANTS.R_A1].getValue());
		System.out.printf("[A2] : %x  : ",R[CONSTANTS.R_A2].getValue());
		System.out.printf("[A3] : %x  : ",R[CONSTANTS.R_A3].getValue());
		System.out.printf("\n[T0] : %x  : ",R[CONSTANTS.R_T0].getValue());
		System.out.printf("[T1] : %x  : ",R[CONSTANTS.R_T1].getValue());
		System.out.printf("[T2] : %x  : ",R[CONSTANTS.R_T2].getValue());
		System.out.printf("[T3] : %x  : ",R[CONSTANTS.R_T3].getValue());
		System.out.printf("\n[T4] : %x  : ",R[CONSTANTS.R_T4].getValue());
		System.out.printf("[T5] : %x  : ",R[CONSTANTS.R_T5].getValue());
		System.out.printf("[T6] : %x  : ",R[CONSTANTS.R_T6].getValue());
		System.out.printf("[T7] : %x  : ",R[CONSTANTS.R_T7].getValue());

		System.out.printf("\n[S0] : %x  : ",R[CONSTANTS.R_S0].getValue());
		System.out.printf("[S1] : %x  : ",R[CONSTANTS.R_S1].getValue());
		System.out.printf("[S2] : %x  : ",R[CONSTANTS.R_S2].getValue());
		System.out.printf("[S3] : %x  : ",R[CONSTANTS.R_S3].getValue());
		System.out.printf("\n[S4] : %x  : ",R[CONSTANTS.R_S4].getValue());
		System.out.printf("[S5] : %x  : ",R[CONSTANTS.R_S5].getValue());
		System.out.printf("[S6] : %x  : ",R[CONSTANTS.R_S6].getValue());
		System.out.printf("[S7] : %x  : ",R[CONSTANTS.R_S7].getValue());
		System.out.printf("\n[T8] : %x  : ",R[CONSTANTS.R_T8].getValue());
		System.out.printf("[T9] : %x  : ",R[CONSTANTS.R_T9].getValue());
		System.out.printf("[K0] : %x  : ",R[CONSTANTS.R_K0].getValue());
		System.out.printf("[K1] : %x  : ",R[CONSTANTS.R_K1].getValue());
		System.out.printf("\n[GP] : %x  : ",R[CONSTANTS.R_GP].getValue());
		System.out.printf("[SP] : %x  : ",R[CONSTANTS.R_SP].getValue());
		System.out.printf("[FP] : %x  : ",R[CONSTANTS.R_FP].getValue());
		System.out.printf("[RA] : %x  : ",R[CONSTANTS.R_RA].getValue());
		System.out.printf("\n[CO Processor - Status] : %x :",CP0.R[CoPROCESSOR_0.REG_STATUS].getValue());
		System.out.printf("[CO Processor - Cause ] : %x \n\n",CP0.R[CoPROCESSOR_0.REG_CAUSE].getValue());
		
	}

}
