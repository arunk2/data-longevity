package edu.iisc.base.emulator.exception;


public enum ProcessorExceptionType {
	UTLBMISSEXCEPTION(-1), EXTERNAL_INTRERRUPT(0), TLB_MODIFICATION(1), TLB_MISS_LOAD(2), TLB_MISS_STORE(3), 
	ADDRESS_ERROR_LOAD(4), ADDRESS_ERROR_STORE(5), BUS_ERROR_INST(6), BUS_ERROR_DATA(7), 
	SYSCALL(8), BREAKPOINT(9), RESERVED_INST(10), COPROCESSOR_UNUSABLE(11), 
	ARITHMETIC_OVERFLOW(12), RESERVED(13);

	private final int vector;

	ProcessorExceptionType(int vector) {
		this.vector = vector;
	}

	public int vector() {
		return vector;
	}
}
