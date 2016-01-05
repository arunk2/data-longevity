package edu.iisc.base.emulator;

import java.util.HashMap;
import java.util.Map;

public class MMU {
	
	Map<Integer, TLBEntry> TLB = new HashMap<Integer, TLBEntry>();
	Map<Integer, PageTableEntry> pageTable;
	
	private TLBEntry getTLBEntry (Integer virtualAddr) {
		return null;
	}
	
	public Integer getPhysicalPage (Integer virtualAddr) {
		
		TLBEntry tlb = getTLBEntry(virtualAddr);
		

		if (tlb != null) {
			//TLB Hit - check for validity of entry and return the physical page no
			return tlb.getPhysicalFrameNo();
		}
		else {
			PageTableEntry page = getPageTableEntry(virtualAddr);
			if (page != null) {
				return page.getPhysicalFrameNo();
			}
			else {
				//Page Fault - raise Interrupt for PF - so that OS handles it
			}
		}
		return null;
	}
	
	private PageTableEntry getPageTableEntry (Integer virtualAddr) {
		
		return null;
	}

}
