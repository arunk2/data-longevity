package edu.iisc.base.emulator;

public class PageTableEntry {
	
	public Integer entry;
	
	private static final Integer MASK_PFN	=	0xFFFFF000; //Integer.parseInt("1111 1111 1111 1111 1111 0000 0000 0000", 2);
	private static final Integer MASK_N 	= 	0x00000800; //Integer.parseInt("0000 0000 0000 0000 0000 1000 0000 0000", 2);
	private static final Integer MASK_D 	= 	0x00000400; //Integer.parseInt("0000 0000 0000 0000 0000 0100 0000 0000", 2);
	private static final Integer MASK_V 	= 	0x00000200; //Integer.parseInt("0000 0000 0000 0000 0000 0010 0000 0000", 2);
	private static final Integer MASK_G		= 	0x00000100; //Integer.parseInt("0000 0000 0000 0000 0000 0001 0000 0000", 2);
	
	public Integer getPhysicalFrameNo() {
		Integer pfn = entry & MASK_PFN;
		return pfn.intValue();
	}
	
	public boolean isNonCacheable() {
		Integer nc = entry & MASK_N;
		return (nc == 1L);
	}
	
	public boolean isDirty() {
		Integer nc = entry & MASK_D;
		return (nc == 1L);
	}
	
	public boolean isValid() {
		Integer v = entry & MASK_V;
		return (v == 1L);
	}
	
	public boolean isGlobal() {
		Integer g = entry & MASK_G;
		return (g == 1L);
	}
	
}
