package edu.iisc.base.emulator;

import java.io.File;

public class CONSTANTS {
	
	public static final int WORD_SIZE = 4;	//4 bytes - 32 bit
	
	//Register numbers & names
	public static final int R_ZERO = 0;
	public static final int R_AT = 1;
	public static final int R_V0 = 2;
	public static final int R_V1 = 3;
	public static final int R_A0 = 4;
	public static final int R_A1 = 5;
	public static final int R_A2 = 6;
	public static final int R_A3 = 7;
	public static final int R_T0 = 8;
	public static final int R_T1 = 9;
	public static final int R_T2 = 10;
	public static final int R_T3 = 11;
	public static final int R_T4 = 12;
	public static final int R_T5 = 13;
	public static final int R_T6 = 14;
	public static final int R_T7 = 15;
	public static final int R_T8 = 24;
	public static final int R_T9 = 25;
	public static final int R_S0 = 16;
	public static final int R_S1 = 17;
	public static final int R_S2 = 18;
	public static final int R_S3 = 19;
	public static final int R_S4 = 20;
	public static final int R_S5 = 21;
	public static final int R_S6 = 22;
	public static final int R_S7 = 23;
	public static final int R_K0 = 26;
	public static final int R_K1 = 27;
	public static final int R_GP = 28;
	public static final int R_SP = 29;
	public static final int R_FP = 30;
	public static final int R_RA = 31;
	
	
	//constants for Interrupt handling
	public static final int INT_HARDWARE_INTERRUPT = 0x1;
	public static final int INT_PROCESSOR_EXCEPTION = 0x2;
	public static final int INT_RESET_REQUEST = 0x4;
	
	public static final boolean BIG_ENDIAN = true;
	
	public static final int BLOCK_SIZE = 4096;	//4 KB
	public static final int PAGE_OFFSET_MASK = 0x00000FFF;	//Integer.parseInt("1111 1111 1111 1111 1111 0000 0000 0000", 2);
	public static final int PAGE_FRAME_MASK = 0xFFFFF000;	//Integer.parseInt("1111 1111 1111 1111 1111 0000 0000 0000", 2);
	
	public static final int RAM_SIZE = 512 * 1024 * 1024; // 512 MB (1/2 GB)
	public final static int RAM_BASE = 0x00000000;
	
	public static final int VIDEO_RAM_SIZE = 0x20000;
	public final static int VIDEO_RAM_BASE = 0xA0000;
	
	   /**
     * the absolute path of Dioscuri's icon
     */
    public static final String EMULATOR_ICON_IMAGE = new File("dioscuri_icon.gif").getAbsolutePath();

    /**
     * the absolute path of the splash screen image
     */
    public static final String EMULATOR_SPLASHSCREEN_IMAGE = new File("dioscuri_splashscreen_2010_v043.gif").getAbsolutePath();

    /**
     * the absolute path of the logging properties file
     */
    public static final String EMULATOR_LOGGING_PROPERTIES = new File("logging.properties").getAbsolutePath();
	
	
}
