package edu.iisc.base.emulator;

public class Register {
	private byte[] value = new byte[CONSTANTS.WORD_SIZE];
	
	public Register() {
		setValue(0);
	}
	
	public void setValue(int data) {
		
		if (CONSTANTS.BIG_ENDIAN) {
			value[0] = (byte) (data >> 24);
			value[1] = (byte) ((data & 0x00ff0000) >> 16);
			value[2] = (byte) ((data & 0x0000ff00) >> 8);
			value[3] = (byte) (data & 0x000000ff);
		} else {
			value[3] = (byte) (data >> 24);
			value[2] = (byte) ((data & 0x00ff0000) >> 16);
			value[1] = (byte) ((data & 0x0000ff00) >> 8);
			value[0] = (byte) (data & 0x000000ff);
		}
		
	}
	
	public byte getByte() {
		return value[0];
	}
	
	private short getHalfWordInBytes(int offset) {
		
		if (CONSTANTS.BIG_ENDIAN) {	
			return (short) (((short) value[offset] << 8) 
					| (((short) value[offset + 1]) & 0x00ff));
		}
		else {
			return (short) (((short) value[offset + 1] << 8) 
					| (((short) value[offset]) & 0x00ff));
		}
		
	}
	
	public short getHalfWord() {
		return getHalfWordInBytes(0);
	}
	
	public Integer getValue() {
        
		if (CONSTANTS.BIG_ENDIAN) {
			return (((int) value[0] << 24)
					| (((int) value[1] << 16) & 0x00ff0000)
					| (((int) value[2] << 8) & 0x0000ff00) 
					| (((int) value[3]) & 0x000000ff));
		} else {
			return (((int) value[3] << 24)
					| (((int) value[2] << 16) & 0x00ff0000)
					| (((int) value[1] << 8) & 0x0000ff00) 
					| (((int) value[0]) & 0x000000ff));
		}
        
	}
}
