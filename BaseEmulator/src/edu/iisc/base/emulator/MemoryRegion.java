package edu.iisc.base.emulator;

import edu.iisc.base.emulator.exception.ProcessorException;

public abstract class MemoryRegion {

	public int from, to;
	public byte memory[];

	byte getByte(int address) throws ProcessorException {
		return memory[address - from];
	}

	void setByte(int address, byte data) throws ProcessorException {
		memory[address - from] = data;
	}

	short getHalfWord(int address) throws ProcessorException {
		if (CONSTANTS.BIG_ENDIAN) {
			return (short) (((short) memory[address - from] << 8) 
					| (((short) memory[address - from + 1]) & 0x00ff));

		} else {
			return (short) (((short) memory[address - from + 1] << 8) 
					| (((short) memory[address - from]) & 0x00ff));

		}
	}

	void setHalfWord(int address, short data) throws ProcessorException {
		if (CONSTANTS.BIG_ENDIAN) {
			memory[address - from] = (byte) (data >> 8);
			memory[address - from + 1] = (byte) (data & 0x00ff);
		} else {
			memory[address - from + 1] = (byte) (data >> 8);
			memory[address - from] = (byte) (data & 0x00ff);
		}
	}

	int getWord(int address) throws ProcessorException {
		int i = address - from;

		if (CONSTANTS.BIG_ENDIAN) {
			return (((int) memory[i] << 24)
					| (((int) memory[i + 1] << 16) & 0x00ff0000)
					| (((int) memory[i + 2] << 8) & 0x0000ff00) 
					| (((int) memory[i + 3]) & 0x000000ff));
		} else {
			return (((int) memory[i + 3] << 24)
					| (((int) memory[i + 2] << 16) & 0x00ff0000)
					| (((int) memory[i + 1] << 8) & 0x0000ff00) 
					| (((int) memory[i]) & 0x000000ff));
		}
	}

	void setWord(int address, int data) throws ProcessorException {
		int i = address - from;

		if (CONSTANTS.BIG_ENDIAN) {
			memory[i] = (byte) (data >> 24);
			memory[i + 1] = (byte) ((data & 0x00ff0000) >> 16);
			memory[i + 2] = (byte) ((data & 0x0000ff00) >> 8);
			memory[i + 3] = (byte) (data & 0x000000ff);
		} else {
			memory[i + 3] = (byte) (data >> 24);
			memory[i + 2] = (byte) ((data & 0x00ff0000) >> 16);
			memory[i + 1] = (byte) ((data & 0x0000ff00) >> 8);
			memory[i] = (byte) (data & 0x000000ff);
		}
	}

}
