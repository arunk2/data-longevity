package edu.iisc.base.emulator;

import java.util.ArrayList;
import java.util.List;

import edu.iisc.base.emulator.exception.ProcessorException;
import edu.iisc.base.emulator.exception.ProcessorExceptionType;

/**
 * AddressSpace - Linear Address Space
 */
public class AddressSpace {
	
	public static final boolean WRITE = true;
	public static final boolean READ = !WRITE;
	
	List<MemoryRegion> regionList;
	Processor processor;

	public AddressSpace() {
		regionList = new ArrayList<MemoryRegion>();
	}

	void addRegion(MemoryRegion r) {
		regionList.add(r);
	}

	MemoryRegion findRegion(int address) throws ProcessorException {
		int i;
		MemoryRegion r;
		
//		address = processor.CP0.translate(address, READ);
		
		if (address > 0x70000000) {
			address -= 0x70000000;
		}

		for (i = 0; i < regionList.size(); i++) {
			r = (MemoryRegion) (regionList.get(i));
			if ((address >= r.from) && (address <= r.to)) {
				return r;
			}
		}
		// no matching region found
		throw new ProcessorException(ProcessorExceptionType.BUS_ERROR_INST,
				"Memory: faulting phys addr: " + Integer.toHexString(address));
	}

	public byte getByte(int address) throws ProcessorException {
		
//		address = processor.CP0.translate(address, READ);
		
		if (address > 0x70000000) {
			address -= 0x70000000;
		}
		
		return findRegion(address).getByte(address);
	}

	public void setByte(int address, byte data) throws ProcessorException {
		
		//If VRAM update it directly else update the RAM
		if (address > 0x70000000) {
			address -= 0x70000000;
		}
		
		if (address >= 0xA0000 && address <= 0xBFFFF) {
			CPU.getInstance().video.writeMode(address, data);
		}
//		address = processor.CP0.translate(address, WRITE);
		else 
			findRegion(address).setByte(address, data);
	}

	public short getHalfWord(int address) throws ProcessorException {
		
//		address = processor.CP0.translate(address, READ);
		
		if (address > 0x70000000) {
			address -= 0x70000000;
		}
		return findRegion(address).getHalfWord(address);
	}

	public void setHalfWord(int address, short data) throws ProcessorException {
		
//		address = processor.CP0.translate(address, WRITE);
		
		if (address > 0x70000000) {
			address -= 0x70000000;
		}
		
		findRegion(address).setHalfWord(address, data);
	}

	public int getWord(int address) throws ProcessorException {
		
//		address = processor.CP0.translate(address, READ);
		if (address > 0x70000000) {
			address -= 0x70000000;
		}
		return findRegion(address).getWord(address);
	}

	public void setWord(int address, int data) throws ProcessorException {
		
//		address = processor.CP0.translate(address, WRITE);
		if (address > 0x70000000) {
			address -= 0x70000000;
		}
		findRegion(address).setWord(address, data);
	}
}
