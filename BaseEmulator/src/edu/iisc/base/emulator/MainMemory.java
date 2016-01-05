package edu.iisc.base.emulator;

public class MainMemory extends MemoryRegion {
		
	public MainMemory(int base, int size) {
		//initialize memory
		from = base;
		to = base+size-1;
		memory = new byte[to-from+1];
		
	}

	public void copyArrayIntoContents(int address, byte[] buffer, int off, int len) {

		for (int i=off; i<off+len; i++, address++)
            setByte(address, buffer[i]);
		
	}

	public void copyContentsIntoArray(int address, byte[] buffer, int off, int len) {
		
		for (int i = off; i < off + len; i++, address++)
			buffer[i] = getByte(address);
		
	}

	public void loadInitialContents(int address, byte[] buf, int off, int len) {
		copyArrayIntoContents(address, buf, off, len);
	}

}
