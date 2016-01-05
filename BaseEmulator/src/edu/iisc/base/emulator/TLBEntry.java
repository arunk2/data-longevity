package edu.iisc.base.emulator;

public class TLBEntry {
	
	public Long entry = 0L;
	
	private static final Long MASK_VPN 	=	0xFFFFF00000000000L; //Long.parseLong("1111 1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000", 2);
	private static final Long MASK_PID 	= 	0x00000FC000000000L; //Long.parseLong("0000 0000 0000 0000 0000 1111 1100 0000 0000 0000 0000 0000 0000 0000 0000 0000", 2);
	private static final Long MASK_PFN 	= 	0x00000000FFFFF000L; //Long.parseLong("0000 0000 0000 0000 0000 0000 0000 0000 1111 1111 1111 1111 1111 0000 0000 0000", 2);
	private static final Long MASK_N 	=	0x0000000000000800L; //Long.parseLong("0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 1000 0000 0000", 2);
	private static final Long MASK_D 	=	0x0000000000000400L; //Long.parseLong("0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0100 0000 0000", 2);
	private static final Long MASK_V 	=	0x0000000000000200L; //Long.parseLong("0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0010 0000 0000", 2);
	private static final Long MASK_G	=	0x0000000000000100L; //Long.parseLong("0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0001 0000 0000", 2);
	
	public Integer getLo() {
		Long tmp = entry & 0x00000000FFFFFFFFL;
		return tmp.intValue();
	}
	
	public void setLo(Integer lo) {
		entry = lo & 0x00000000FFFFFFFFL;
	}
	
	public Integer getHi() {
		Long tmp = entry & 0xFFFFFFFF00000000L;
		tmp >>= 16;
		return tmp.intValue();
	}
	
	public void setHi(Integer hi) {
		Long tmp = new Long(hi);
		tmp <<= 16;
		entry |= tmp;
	}
	
	public Integer getVirtualPageNo() {
		Long vpn = entry & MASK_VPN;
		return vpn.intValue();
	}
	
	public Integer getPhysicalFrameNo() {
		Long pfn = entry & MASK_PFN;
		return pfn.intValue();
	}
	
	public Integer getPID() {
		Long pid = entry & MASK_PID;
		return pid.intValue();
	}
	
	public boolean isNonCacheable() {
		Long nc = entry & MASK_N;
		return (nc == 1L);
	}
	
	public boolean isDirty() {
		Long nc = entry & MASK_D;
		return (nc == 1L);
	}
	
	public boolean isValid() {
		Long v = entry & MASK_V;
		return (v == 1L);
	}
	
	public boolean isGlobal() {
		Long g = entry & MASK_G;
		return (g == 1L);
	}
	
}
