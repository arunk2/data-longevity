package edu.iisc.base.emulator.exception;


public final class ProcessorException extends RuntimeException {
	
	//Exception Vector Location
	public static final int ADDRESS_RESET 		= 0xBFC00000;		//RESET Exception
	public static final int ADDRESS_UTLB 		= 0x80000000; 		//UTLB Exception
	public static final int ADDRESS_GENERAL		= 0x80000080; 		//General Exception Vector
	public static final int ADDRESS_UTLB_BEV	= 0xBFC00100; 		//UTLB Exception - when BEV flag in Status register is set
	public static final int ADDRESS_GENERAL_BEV	= 0xBFC00180; 		//General Exception Vector - when BEV flag in Status register is set
	
	private final ProcessorExceptionType type;
	private final int errorCode;

	public ProcessorException(ProcessorExceptionType type, int errorCode) {
		this.type = type;
		this.errorCode = errorCode;
	}

	public ProcessorException(ProcessorExceptionType type) {
		this.type = type;
		this.errorCode = 0;
	}
	
	public ProcessorException(ProcessorExceptionType type, String message) {
		super(message);
		this.type = type;
		this.errorCode = 0;
	}

	public ProcessorExceptionType getType() {
		return type;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String toString() {
		return "Processor Exception: " + type + " [errorcode:0x"
				+ Integer.toHexString(getErrorCode()) + "]";
	}
}

