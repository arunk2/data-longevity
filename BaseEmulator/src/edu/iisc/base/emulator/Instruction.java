package edu.iisc.base.emulator;

enum INSTRUCTION_TYPE {
	I_TYPE,
	J_TYPE,
	R_TYPE
};

public class Instruction {
	
	private static final int OPCODE_MASK = 	0xFC000000;	//Integer.parseInt("1111 1100 0000 0000 0000 0000 0000 0000", 2);
	private static final int RS_MASK = 		0x03E00000;	//Integer.parseInt("0000 0011 1110 0000 0000 0000 0000 0000", 2);
	private static final int RT_MASK = 		0x001F0000;	//Integer.parseInt("0000 0000 0001 1111 0000 0000 0000 0000", 2);
	private static final int RD_MASK = 		0x0000F800;	//Integer.parseInt("0000 0000 0000 0000 1111 1000 0000 0000", 2);
	private static final int SHAMT_MASK =	0x000007C0;	//Integer.parseInt("0000 0000 0000 0000 0000 0111 1100 0000", 2);
	private static final int FUNCT_MASK = 	0x0000003F;	//Integer.parseInt("0000 0000 0000 0000 0000 0000 0011 1111", 2);
	
	private static final int IMMEDIATE_MASK=0x0000FFFF;	//Integer.parseInt("0000 0000 0000 0000 1111 1111 1111 1111", 2);
	private static final int TARGET_MASK = 	0x03FFFFFF;	//Integer.parseInt("0000 0011 1111 1111 1111 1111 1111 1111", 2);
	
	public static final int OP_ADD = 1;
	public static final int OP_ADDI = 2;
	public static final int OP_ADDIU = 3;
	public static final int OP_ADDU = 4;
	public static final int OP_AND	= 5;
	public static final int OP_ANDI = 6;
	public static final int OP_BEQ = 7;
	public static final int OP_BGEZ = 8;
	public static final int OP_BGEZAL = 9;
	public static final int OP_BGTZ = 10;
	public static final int OP_BLEZ = 11;
	public static final int OP_BLTZ = 12;
	public static final int OP_BLTZAL = 13;
	public static final int OP_BNE = 14;

	public static final int OP_DIV = 16;
	public static final int OP_DIVU = 17;
	public static final int OP_J = 18;
	public static final int OP_JAL = 19;
	public static final int OP_JALR = 20;
	public static final int OP_JR = 21;
	public static final int OP_LB = 22;
	public static final int OP_LBU = 23;
	public static final int OP_LH = 24;
	public static final int OP_LHU = 25;
	public static final int OP_LUI = 26;
	public static final int OP_LW = 27;
	public static final int OP_LWL = 28;
	public static final int OP_LWR = 29;

	public static final int OP_MFHI = 31;
	public static final int OP_MFLO = 32;

	public static final int OP_MTHI = 34;
	public static final int OP_MTLO = 35;
	public static final int OP_MULT = 36;
	public static final int OP_MULTU = 37;
	public static final int OP_NOR = 38;
	public static final int OP_OR = 39;
	public static final int OP_ORI = 40;
	public static final int OP_RFE = 41;
	public static final int OP_SB = 42;
	public static final int OP_SH = 43;
	public static final int OP_SLL = 44;
	public static final int OP_SLLV = 45;
	public static final int OP_SLT = 46;
	public static final int OP_SLTI = 47;
	public static final int OP_SLTIU = 48;
	public static final int OP_SLTU = 49;
	public static final int OP_SRA = 50;
	public static final int OP_SRAV = 51;
	public static final int OP_SRL = 52;
	public static final int OP_SRLV = 53;
	public static final int OP_SUB = 54;
	public static final int OP_SUBU = 55;
	public static final int OP_SW = 56;
	public static final int OP_SWL = 57;
	public static final int OP_SWR = 58;
	public static final int OP_XOR = 59;
	public static final int OP_XORI = 60;
	public static final int OP_SYSCALL = 61;
	public static final int OP_UNIMP = 62;
	public static final int OP_RES = 63;

	public static final int OP_MFCP0 = 65;
	public static final int OP_MTCP0 = 66;
	public static final int OP_CFCP0 = 67;
	public static final int OP_CTCP0 = 68;
	public static final int OP_BCCP0 = 69;
	public static final int OP_TLBR = 70;
	public static final int OP_TLBWI = 71;
	public static final int OP_TLBWR = 72;
	public static final int OP_TLBP = 73;
	
	public static final int OP_COP1 = 75;
	public static final int OP_COP2 = 76;
	public static final int OP_COP3 = 77;
	
	public static final int OP_BREAK = 78;
	
	/*
	 * The table below is used to translate bits 31:26 of the instruction
	 * into a value suitable for the "opCode" field of a MemWord structure,
	 * or into a special value for further decoding.
	 */
	public static final int SPECIAL  = 100;
	public static final int BCOND = 101;
	public static final int COP0 = 102; // system, mmu
	public static final int COP1 = 103; // fpu
	public static final int COP2 = 104;
	public static final int COP3 = 105;

	public static final int IFMT = 1;
	public static final int JFMT = 2;
	public static final int RFMT = 3;
	
	public int instruction;
	public INSTRUCTION_TYPE type;
	
	public int opcode;
	public int rs;
	public int rt;
	public int rd;
	public int shamt;
	public int funct;
	public short immediat;
	public int immediat_unsigned;
	public int immediat_unsigned_byte;
	public int target;
	
	public Instruction(int instruction) {
		this.instruction = instruction;
	}
	
	public void decode() {
//		opcode 	= instruction & OPCODE_MASK;
		opcode	= (instruction >> 26) & 0x3f;
		rs = (instruction >> 21) & 0x1f;
		rt = (instruction >> 16) & 0x1f;
		rd = (instruction >> 11) & 0x1f;
//		rs		= instruction & RS_MASK;
//		rs		= rs >> 21;
//		rt		= instruction & RT_MASK;
//		rt		= rt >> 16;
//		rd		= instruction & RD_MASK;
//		rd		= rd >> 11;
		shamt	= instruction & SHAMT_MASK;
		shamt	= shamt >> 6;
		funct	= instruction & FUNCT_MASK;
		immediat= (short) (instruction & IMMEDIATE_MASK);
		immediat_unsigned = instruction & IMMEDIATE_MASK;
		immediat_unsigned_byte = instruction & 0x000000FF;
		target = instruction & TARGET_MASK;
		
		/*
		Mnemonic	Meaning					TypeOpcode	Funct
		add			Add						R	0x00	0x20
		addu		Add Unsigned			R	0x00	0x21
		and			Bitwise AND				R	0x00	0x24
		div			Divide					R	0x00	0x1A
		divu		Unsigned Divide			R	0x00	0x1B
		jr			Jump to Address in Reg	R	0x00	0x08
		mfhi		Move from HI Register	R	0x00	0x10
		mflo		Move from LO Register	R	0x00	0x12
		mfc0		Move from Coprocessor 0	R	0x10	NA
		mult		Multiply				R	0x00	0x18
		multu		Unsigned Multiply		R	0x00	0x19
		nor			Bitwise NOR (NOT-OR)	R	0x00	0x27
		xor			Bitwise XOR (Ex-OR)		R	0x00	0x26
		or			Bitwise OR				R	0x00	0x25
		slt			Set to 1 if Less Than	R	0x00	0x2A
		sltu		Set to 1 if < Unsigned	R	0x00	0x2B
		sll			Logical Shift Left		R	0x00	0x00
		srl			Shift Right (0-extended)R	0x00	0x02
		sra			Shift Right(sign-extend)R	0x00	0x03
		sub			Subtract				R	0x00	0x22
		subu		Unsigned Subtract		R	0x00	0x23
		 */
		if (opcode == 0x00 ||opcode == 0x10)
			type = INSTRUCTION_TYPE.R_TYPE;
		/*
		 * 	Mnemonic	Meaning			Type	Opcode	Funct
		 * 	j			Jump to Address	J		0x02	NA
		 *	jal			Jump and Link	J		0x03	NA
		 */
		else if (opcode == 0x02 || opcode == 0x03)
			type = INSTRUCTION_TYPE.J_TYPE;
		/*
		 * 
			Mnemonic	Meaning					Type	Opcode	Funct
			addi		Add Immediate			I		0x08	NA
			addiu		Add Unsigned Immediate	I		0x09	NA
			andi		Bitwise AND Immediate	I		0x0C	NA
			beq			Branch if Equal			I		0x04	NA
			bne			Branch if Not Equal		I		0x05	NA
			lbu			Load Byte Unsigned		I		0x24	NA
			lhu			Load Halfword Unsigned	I		0x25	NA
			lui			Load Upper Immediate	I		0x0F	NA
			lw			Load Word				I		0x23	NA
			ori			Bitwise OR Immediate	I		0x0D	NA
			sb			Store Byte				I		0x28	NA
			sh			Store Halfword			I		0x29	NA
			slti		Set to 1 if Less Than Immediate	I	0x0A	NA
			sltiu		Set to 1 if Less Than UnsignedI	I	0x0B	NA
		 */
		else 
			type = INSTRUCTION_TYPE.I_TYPE;
		
	}

	final static int CPNOMASK = 0x18000000;
	int cpNo() {
		return instruction & CPNOMASK;
	}
	
//	void decode() {  
//		int[] opEntry;
//		
//		rs = (value >> 21) & 0x1f;
//		rt = (value >> 16) & 0x1f;
//		rd = (value >> 11) & 0x1f;
//		opEntry = opTable[(value >> 26) & 0x3f];
//		opCode = opEntry[OPCODE];
//		if (opEntry[FORMAT] == IFMT) { // Immediate Type
//			extra = value & 0xffff;
//			if ((extra & 0x8000) == 0x8000) {
//			   extra |= 0xffff0000;
//			}
//		} 
//		else if (opEntry[FORMAT] == RFMT) { // Register Type
//			extra = (value >> 6) & 0x1f;
//		} 
//		else { // Jump Type
//			extra = value & 0x03ffffff;
//		}
//		if (opCode == SPECIAL) {
//			opCode = specialTable[value & 0x3f];
//		} 
//		else if (opCode == BCOND) {
//			int i = value & 0x1f0000;
//			if (i == 0) {	opCode = OP_BLTZ; } 
//			else if (i == 0x10000) {	opCode = OP_BGEZ; } 
//			else if (i == 0x100000) {	opCode = OP_BLTZAL; } 
//			else if (i == 0x110000) {	opCode = OP_BGEZAL; } 
//			else {	opCode = OP_UNIMP; }
//		}
//		else if (opCode == COP0) {
//			int i = value & 0x0000001f; // function field
//			if (i == 0) {
//				if (rs == 0) {	opCode = OP_MFCP0; }
//				else if (rs == 0x4) {	opCode = OP_MTCP0; }
//			}
//			else if (i == 0x1) {	opCode = OP_TLBR; }
//			else if (i == 0x2) {	opCode = OP_TLBWI; }
//			else if (i == 0x6) {	opCode = OP_TLBWR; }
//			else if (i == 0x8) {	opCode = OP_TLBP; }
//			else if (i == 0x10) {	opCode = OP_RFE; }
//			else { opCode = OP_UNIMP; }
//		}
//		else if (opCode == COP1) {
//			opCode = OP_COP1;
//		}
//		else if (opCode == COP2) {
//			opCode = OP_COP2;
//		}
//		else if (opCode == COP3) {
//			opCode = OP_COP3;
//		}
//	}
	
}
