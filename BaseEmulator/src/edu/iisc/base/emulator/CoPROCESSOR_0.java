package edu.iisc.base.emulator;

import java.util.Random;

import edu.iisc.base.emulator.exception.ProcessorException;
import edu.iisc.base.emulator.exception.ProcessorExceptionType;

public class CoPROCESSOR_0 {

	//EntryHi & EntryLo
	private static final int ENTRYHI_VPN 	=	0xFFFFF000;
	private static final int ENTRYLO_PID 	= 	0x00000FC0;
	private static final int ENTRYLO_PFN 	= 	0xFFFFF000;
	private static final int ENTRYLO_NONCACHE=	0x00000800;
	private static final int ENTRYLO_DIRTY 	=	0x00000400;
	private static final int ENTRYLO_VALID 	=	0x00000200;
	private static final int ENTRYLO_GLOBAL	=	0x00000100;
	
	//Index Regiter masks
	private static final int INDEX_P	=	0x80000000; 		//Probe Failure
	private static final int INDEX_INDEX= 	0x00003F00; 		//Arithmetic overflow exception
	
	//Random Regiter masks
	private static final int RANDOM		= 	0x00003F00; 			//Random
	
	//Status Register masks
	private static final int STATUS_CU 		= 0xF0000000;		//CoProcessor Usability
	private static final int STATUS_BEV 	= 0x00400000;		//Bootstrap Exception Vector
	private static final int STATUS_TS 		= 0x00200000;		//TLB Shutdown
	private static final int STATUS_PE 		= 0x00100000;		//Parity Error
	private static final int STATUS_CM 		= 0x00080000;		//Cache Miss
	private static final int STATUS_PZ 		= 0x00040000;		//Parity Zero
	private static final int STATUS_SWC 	= 0x00020000; 		//Swap Caches
	private static final int STATUS_ISC 	= 0x00010000; 		//Isolate Cache
	private static final int STATUS_INT_MASK= 0x0000FF00; 		//Interrupt Mask
	private static final int STATUS_KUO 	= 0x00000020; 		//Kernel/User mode (old). 0 - kernel, 1 - user
	private static final int STATUS_IEO 	= 0x00000010; 		//Interrupt Enable (old). 1 - enable, 0 - disable
	private static final int STATUS_KUP 	= 0x00000008; 		//Kernel/User mode (previous). 0 - kernel, 1 - user
	private static final int STATUS_IEP 	= 0x00000004; 		//Interrupt Enable (previous). 1 - enable, 0 - disable
	private static final int STATUS_KER_USER_CUR 	= 0x00000002; 		//Kernel/User mode (current). 0 - kernel, 1 - user
	private static final int STATUS_INT_ENABLE_CUR 	= 0x00000001; 		//Interrupt Enable (current). 1 - enable, 0 - disable

	//Cause Register masks
	private static final int CAUSE_BD 		= 0x80000000;		//Branch Delay. set to 1 if last was taken while executing in a branch delay slot
	private static final int CAUSE_CE 		= 0x30000000; 		//CoProcessor Error
	private static final int CAUSE_IP 		= 0x0000FC00; 		//Interrupts Pending
	private static final int CAUSE_SW 		= 0x00000300; 		//Software Interrupts
	private static final int CAUSE_EXC_CODE = 0x0000003C; 		//Exception Code field
	
	//CONTEXT Register masks
	private static final int CONTEXT_PTEBASE= 0xFFE00000;		//Holds - base for the Page Table Entry
	private static final int CONTEXT_BADVPN	= 0x001FFFFC; 		//Holds - failing Virtual Page Number
	
	//PRID Register masks
	private static final int PRID_IMP 		= 0x0000FF00;		//Implementation Identifier
	private static final int PRID_REV 		= 0x000000FF; 		//Revision Identifier
	
	//PAGE MASKS
	public final static int PAGE_FRAME_MASK = CONSTANTS.PAGE_FRAME_MASK;
	public final static int PAGE_OFFSET_MASK = CONSTANTS.PAGE_OFFSET_MASK;

	// register numbers
	//co-processor 0 - VM sub-system
	public final static int REG_TLBHI = 10;
	public final static int REG_TLBLO = 2;
	public final static int REG_INDEX = 0;
	public final static int REG_RANDOM = 1;
	//co-processor 0 - ExceptionHandling
	public final static int REG_STATUS = 12;	
	public final static int REG_CAUSE = 13;
	public final static int REG_EPC = 14;
	public final static int REG_CONTEXT = 4;
	public final static int REG_BADVA = 8;
	public final static int REG_PRID = 15;
	
	public final static int EXCMASK = 0xffffff00;
	public final static int CEMASK = 0xcfffffff;
	public final static int CP0 = 0x10000000;
	public final static int CP1 = 0x20000000;
	public final static int CP2 = 0x40000000;
	public final static int CP3 = 0x80000000;
	public final static int BOOTMODE = 0x00400000;
	public final static int TLBSHUTDOWN = 0x00200000;
	
	private static final int TLB_SIZE 		= 64;	//64 entries
	private static final int REGISTERS_COUNT= 32;	//32 registers
	public Register[] R = new Register[REGISTERS_COUNT];
	
	//Translation Look-aside Buffer for quick address translation
	private TLBEntry[] TLB = new TLBEntry[TLB_SIZE];
	private final static int RESERVEDENTRIES = 8;
	
	private Processor processor;					//Processor connencted with
	private Random random = new Random(29);
	private int tlbHint = 0;
	
	public CoPROCESSOR_0(Processor processor) {
		this.processor = processor;
		for (int i = 0; i < REGISTERS_COUNT; i++) {
			R[i] = new Register();
		}

		// init PRID, init in kernel mode, interrupts disabled
		R[REG_PRID].setValue(0x00000230); // proc id, MIPS R3000A compatible
		R[REG_STATUS].setValue(CP0 & BOOTMODE & TLBSHUTDOWN);
		
		for (int i = 0; i < TLB_SIZE; i++) {
			TLB[i] = new TLBEntry();
		}
	}

	public boolean isZero() {
		if ((R[REG_STATUS].getValue() & STATUS_PZ) > 0)
			return true;
		else
			return false;
	}

	public void setStatusEXL() {
		int temp = R[REG_STATUS].getValue() | STATUS_BEV;
		R[REG_STATUS].setValue(temp);
	}

	public void resetStatusEXL() {
		int temp = R[REG_STATUS].getValue() & (~STATUS_BEV);
		R[REG_STATUS].setValue(temp);
	}
	
	public void interrupt(int exception, int no, int pc, boolean braDelay) {
		
		// badVMaddr is set when exception was detected, this
		// happens while translating an address in translate(), physical()

		// set ip bits in status register, set kernel mode
		int tmp = R[REG_STATUS].getValue() & 0x0000000f; // mask prev and current state
		R[REG_STATUS].setValue(R[REG_STATUS].getValue() & 0xffffffc0);
		R[REG_STATUS].setValue(R[REG_STATUS].getValue() | (tmp << 2));
		
		// KUcurr=0 (kmode), IEcurr=0 (int disabl.)
		// set epc
		R[REG_EPC].setValue(pc);
		if (braDelay)
			R[REG_EPC].setValue(pc - 4); // point to the previous instr

		// setup exception cause and/or hardware interrupt number
		if (exception >= 0) { // filter out utlb misses (exc code = -1)
			R[REG_CAUSE].setValue(R[REG_CAUSE].getValue() & EXCMASK);
			R[REG_CAUSE].setValue(R[REG_CAUSE].getValue() | ((exception * 4) & ~EXCMASK));
			if (exception == 0) {
				R[REG_CAUSE].setValue(R[REG_CAUSE].getValue() | ((1 << no) << 10));
			}
		}
		
		// old ip's remain in cause
		if (braDelay)
			R[REG_CAUSE].setValue(R[REG_CAUSE].getValue() | 0x80000000);
		else
			R[REG_CAUSE].setValue(R[REG_CAUSE].getValue() & 0x7fffffff);

		// if it was an UTLBmiss we must provide tlbhi and part of context
		// this is to minimize OS handling effort (OS needs only to set TLBLO)
		if (exception == processor.UTLBMISSEXCEPTION) {  //TODO - check this value
			R[REG_TLBHI].setValue(R[REG_TLBHI].getValue() & PAGE_OFFSET_MASK);
			R[REG_TLBHI].setValue(R[REG_TLBHI].getValue() | (R[REG_BADVA].getValue() & PAGE_FRAME_MASK));
			R[REG_CONTEXT].setValue( R[REG_CONTEXT].getValue() & ~CONTEXT_BADVPN);
			R[REG_CONTEXT].setValue(R[REG_CONTEXT].getValue() | (CONTEXT_BADVPN & (R[REG_TLBHI].getValue() >> 10)));
		}
	}

	public boolean interruptEnabled(int i) {
		int mask;

		if ((R[REG_STATUS].getValue() & STATUS_INT_ENABLE_CUR) == 0)
			return false;

		if ((i >= 0) && (i <= 5))
			mask = 0x00000400 << i;
		else if ((i >= 6) && (i <= 7))
			mask = 0x00000100 << (i - 6);
		else
			return false;
		return ((R[REG_STATUS].getValue() & mask) != 0);
	}

	public void setCPError(int cpNo) {
		R[REG_CAUSE].setValue(R[REG_CAUSE].getValue() & CEMASK);
		R[REG_CAUSE].setValue(R[REG_CAUSE].getValue() & (cpNo << 28));
	}
		
	public int getRegister(int no) {
		switch (no) {
		case REG_STATUS:
		case REG_EPC:
		case REG_CAUSE:
		case REG_TLBHI:
		case REG_TLBLO:
		case REG_INDEX:
		case REG_CONTEXT:
		case REG_BADVA:
		case REG_PRID:
			return R[no].getValue();
		case REG_RANDOM:
			return (randomIndex() << 8);
		default:
//			throw new CPRegUnavailableException("r" + no);
			throw new ProcessorException(ProcessorExceptionType.COPROCESSOR_UNUSABLE);
		}
	}

	public void putRegister(int no, int data) {
		switch (no) {
		case REG_TLBLO:
		case REG_TLBHI:
		case REG_INDEX:
		case REG_STATUS:
		case REG_CAUSE:
		case REG_CONTEXT:
			R[no].setValue(data);
			break;
		default:
//			throw new CPRegUnavailableException("r" + no);
			throw new ProcessorException(ProcessorExceptionType.COPROCESSOR_UNUSABLE);
		}
	}

	public void rfeInstruction() {
		// System.out.print("sr before:" +
		// Integer.toHexString(register[STATUS]));
		// restore status register, enter user mode
		int tmp = (R[REG_STATUS].getValue() & 0x0000003f) >>> 2; // move prev to current
		R[REG_STATUS].setValue(R[REG_STATUS].getValue() & 0xffffffc0);
		R[REG_STATUS].setValue(R[REG_STATUS].getValue() | tmp);
		// System.out.print("  sr after:" +
		// Integer.toHexString(register[STATUS]));
	}

	public void handleTLBInstruction(Instruction i) {
		int j;
		switch (i.opcode) {
		case Instruction.OP_TLBR:
			j = (R[REG_INDEX].getValue() & 0x00003f00) >> 8;
		
			R[REG_TLBLO].setValue(TLB[j].getLo());
			R[REG_TLBHI].setValue(TLB[j].getHi());
			break;
		case Instruction.OP_TLBWI:
			j = (R[REG_INDEX].getValue() & 0x00003f00) >> 8;
			TLB[j].setLo(R[REG_TLBLO].getValue());
			TLB[j].setHi(R[REG_TLBHI].getValue());
			break;
		case Instruction.OP_TLBWR:
			j = randomIndex();
			TLB[j].setLo(R[REG_TLBLO].getValue());
			TLB[j].setHi(R[REG_TLBHI].getValue());
			break;
		case Instruction.OP_TLBP:
			for (j = 0; j < TLB_SIZE; j++) {
				if ((TLB[j].getLo() == R[REG_TLBLO].getValue())
						&& (TLB[j].getHi() == R[REG_TLBHI].getValue())) {
					R[REG_INDEX].setValue(j << 8);
					return;
				}
			}
			R[REG_INDEX].setValue(R[REG_INDEX].getValue() | 0x80000000);
			// set probe bit 31
			break;
		default:
		}
	}

	// reflects static part of address map of IDT R3052 extended architecture
	public int translate(int virt, boolean write) throws ProcessorException {
		
//		//Video memory
//		System.out.printf("Accessing in Virtual Addr --- %x\n", virt);
//		if (virt > 0xA0000 && virt < 0xBFFFF) {
//			System.out.printf("Accessing in Video RAM --- %x\n", virt);
//			return virt;
//		}
			
		if (virt >= 0 && virt <= 0x7fffffff) { // kernel/user mapped
			return tlbLookup(virt, false, write);
		}
		// check for kernel mode first
		if ((R[REG_STATUS].getValue() & STATUS_KER_USER_CUR) != 0) {
			if (write)
				throw new ProcessorException(ProcessorExceptionType.ADDRESS_ERROR_STORE, "access violation at "
						+ Integer.toHexString(virt));
			else
				throw new ProcessorException(ProcessorExceptionType.ADDRESS_ERROR_LOAD, "access violation at "
						+ Integer.toHexString(virt));
		} else if (virt > 0xc0000000 && virt <= 0xffffffff) { // kernel mapped
			return tlbLookup(virt, true, write);
		} else if (virt >= 0x80000000 && virt < 0xa0000000) { // kernel cached
			return virt - 0x80000000;
		} else if (virt >= 0xa0000000 && virt < 0xc0000000) { // kernel uncached
			return virt - 0xa0000000;
		}
		return 0;
	}

	final int tlbLookup(int virt, boolean kSeg2, boolean writeAccess)
			throws ProcessorException {
		// use last hit from tlbHint
		if (isTLBHit(virt, TLB[tlbHint])) {
			return physical(virt, TLB[tlbHint], writeAccess);
		}
		for (int i = 0; i < TLB_SIZE; i++) {
			if (isTLBHit(virt, TLB[i])) {
				tlbHint = i;
				return (physical(virt, TLB[i], writeAccess));
			}
		}
		// generate tlb miss exception
		if (!kSeg2) {
			R[REG_BADVA].setValue(virt);
			throw new ProcessorException(ProcessorExceptionType.UTLBMISSEXCEPTION);
		}
		// kernel segment
		else {
			R[REG_BADVA].setValue(virt);
			throw new ProcessorException(ProcessorExceptionType.TLB_MISS_LOAD, "VM:" + Integer.toHexString(virt));
		}
	}

	final boolean isTLBHit(int virt, TLBEntry e) {
		return (e.getHi() & PAGE_FRAME_MASK) == (virt & PAGE_FRAME_MASK);
	}

	final int physical(int virt, TLBEntry e, boolean writeAccess)
			throws ProcessorException {
		// check valid bit, then dirty (read-only bit)
		// may generate TLB L/S miss
		if ((e.getLo() & ENTRYLO_VALID) == 0) {
			R[REG_BADVA].setValue(virt);
			if (writeAccess) {
				throw new ProcessorException(ProcessorExceptionType.TLB_MISS_STORE, "VM:" + Integer.toHexString(virt));
			} else {
				throw new ProcessorException(ProcessorExceptionType.TLB_MISS_LOAD, "VM:" + Integer.toHexString(virt));
			}
		}
		if (((e.getLo() & ENTRYLO_DIRTY) == 0) && writeAccess) {
			R[REG_BADVA].setValue(virt);
			throw new ProcessorException(ProcessorExceptionType.TLB_MODIFICATION, "VM:" + Integer.toHexString(virt));
		}
		return ((TLB[tlbHint].getLo() & PAGE_FRAME_MASK) | (virt & PAGE_OFFSET_MASK));
	}	

	final int randomIndex() {
		return ((random.nextInt() % (TLB_SIZE - RESERVEDENTRIES)) + RESERVEDENTRIES);
	}

}
