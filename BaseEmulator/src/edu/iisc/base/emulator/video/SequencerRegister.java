/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:46 $ $Author: blohman $
 * 
 * Copyright (C) 2007  National Library of the Netherlands, Nationaal Archief of the Netherlands
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 * jrvanderhoeven at users.sourceforge.net
 * blohman at users.sourceforge.net
 * 
 * Developed by:
 * Nationaal Archief               <www.nationaalarchief.nl>
 * Koninklijke Bibliotheek         <www.kb.nl>
 * Tessella Support Services plc   <www.tessella.com>
 *
 * Project Title: DIOSCURI
 *
 */
package edu.iisc.base.emulator.video;

import java.util.Arrays;

/*
 * Complete descriptions of the following VGA registers can be found at:
 * Sequencer:            http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/seqreg.htm
 */

/**
 * Sequencer Register
 * Controls how video data is sent to the DAC.<BR>
 * Consists of 5 8-bit registers; these are accessed via a pair of registers, <BR>
 * the Address Register [0x3C4] and the Data Register [0x3C5]
 */
public class SequencerRegister
{
    byte index;                         // Index used to address registers; set via I/O port 0x3C4

    byte aSynchReset;                   // Register 0x00: Reset register (bits 0 - 1)
    byte synchReset;                    // Bit 0: Asynchronous Reset: 0 commands sequencer to asynch. clear and halt.
                                        // Bit 1: Synchromous Reset: 0 commands sequencer to synch. clear and halt.
                                        // Both bits must be set to 1 for the sequencer to operate    
    byte clockingMode;                  // Register 0x01: Clocking mode register (bits 0 - 5)
                                        // Bit 5: Screen disable
                                        // Bit 4: Shift four enable
    byte dotClockRate;                  // Bit 3: Dot clock rate
                                        // Bit 2: Shilft/Load rate
                                        // Bit 1: not used
                                        // Bit 0: 9/8 dot mode 
    byte mapMask;                       // Register 0x02: Map Mask register (bits 0 - 3)                     
    byte[] mapMaskArray = new byte[4];  // Each bit corresponds to a plane of video display memory. Write enabled is set per plane here.
                                        // 0: Write disabled
                                        // 1: Write enabled
    byte characterMapSelect;            // Register 0x03: Character Map Select (bits 0 - 5)
                                        // Text mode font selector when bit 3 of character attribute is 1. These fields are not contiguous
                                        // to provide EGA compatibility (Set A: bits 2,3,5; Set B: bits 0,1,4)
                                        // 000b - Font residing at 0000h - 1FFFh
                                        // 001b - Font residing at 4000h - 5FFFh
                                        // 010b - Font residing at 8000h - 9FFFh
                                        // 011b - Font residing at C000h - DFFFh
                                        // 100b - Font residing at 2000h - 3FFFh
                                        // 101b - Font residing at 6000h - 7FFFh
                                        // 110b - Font residing at A000h - BFFFh
                                        // 111b - Font residing at E000h - FFFFh
    final static short charMapOffset[] = new short[] {(short) 0x0000,   // Array holding above offset values 
                                                      (short) 0x4000,   // Note: the values given here exceed the range of a signed short, but should
                                                      (short) 0x8000,   // return the correct hex values, e.g. (short)0xE000 = -8192 signed = E000h 
                                                      (short) 0xC000, 
                                                      (short) 0x2000, 
                                                      (short) 0x6000, 
                                                      (short) 0xA000, 
                                                      (short) 0xE000};
    short charMapAddress;                                               // Holds the character map address
                                        // Register 0x04: Memory Mode Register (bits 1 - 3)
    byte chainFourEnable;               // Bit 3: Chain 4 enable - controls map selected during system read operations
                                        // 0: System addresses sequentially access data within bit map using Map Mask register
                                        // 1: Map selection based on lower bits (oddEvenDisable,extendedMemory):
                                        // 00 - map 0
                                        // 01 - map 1
                                        // 10 - map 2
                                        // 11 - map 3
    byte oddEvenDisable;                // Bit 2: Odd/Even Host Memory Write Addressing Disabled
                                        // 0: Even system addresses access maps 0 and 2; odd system addresses access maps 1 and 3
                                        // 1: System addresses sequentially access data, maps are accessed according to MapMask register
    byte extendedMemory;                // Bit 1: Extended Memory
                                        // 0: disable extended memory access
                                        // 1: enable video memory from 64KB to 256KB
    
    /**
     * Return variables to default values
     */
    protected void reset()
    {
        index = 0;
        aSynchReset = 1;
        synchReset = 1;
        clockingMode = 0;
        dotClockRate = 0;
        mapMask = 0;
        Arrays.fill(mapMaskArray, (byte) 0);
        characterMapSelect = 0;
        charMapAddress = 0;
        chainFourEnable = 0;            // Use mapMask register, read map select
        oddEvenDisable = 1;             // Sequential addressing mode
        extendedMemory = 1;             // Enable memory > 64K
    }    

}
