/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:45 $ $Author: blohman $
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
 * Graphics:             http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/graphreg.htm
 */

/**
 * Graphics registers
 * Controls how the CPU accesses video RAM<BR>
 * Consists of 9 8-bit registers; these are accessed via a pair of registers, <BR>
 *  the Address Register [0x3CE] and the Data Register [0x3CF]
 */
public class GraphicsController
{
    int index;                  // Index used to address registers; set via I/O port 0x3C0
    byte[] latch = new byte[4]; // Latches used to store data for used in Read/Write Modes
                                // Contains byte from corresponding plane read during last CPU read

    
    byte setReset;              // Register 0x00: Set/Reset register (bits 0 - 3)
                                // Used in Write Mode 0 and 3; see documentation for details
    byte enableSetReset;        // Register 0x01: Enable Set/Reset register (bits 0 - 3)
                                // Used in Write Mode 0 to select if data is derived from host or Set/Reset register
    byte colourCompare;         // Register 0x02: Colour compare (bits 0 - 3)
                                // Holds a reference colour used in Read Mode 1
                                // Register 0x03: Data rotate (bits 0 - 4)
    byte dataOperation;         // Used in Write Mode 0 and 2; logical operations defined by bits 4,3 are:
                                // 00b - Result is input from previous stage unmodified.
                                // 01b - Result is input from previous stage logical ANDed with latch register.
                                // 10b - Result is input from previous stage logical ORed with latch register.
                                // 11b - Result is input from previous stage logical XORed with latch register.
    byte dataRotate;            // Bits 0 - 2 indicate the rotate count
    byte readMapSelect;         // Register 0x04: Read map select (bits 0 - 1)
                                // Used in Read Mode 0 to specify the display memory plane
                                // Register 0x05: Graphics Mode (bits 0 - 6)
    byte shift256Reg;           // Bit 6: 256-colour shift mode
                                // 0: Allows bit 5 to control loading of the shift registers
                                // 1: Shift registers loaded supporting 256 colour mode
                                // Bit 5: Shift register interleave mode; used in Modes 4 and 5
                                // 0: Do not format serial data
                                // 1: Format serial data with even-number bits from both maps on even-numbered maps, likewise for odd.
    byte hostOddEvenEnable;     // Bit 4: Host Odd/Even Memory Read Addressing Enable
                                // 0: Do not select odd/even mode
                                // 1: Select odd/even addressing mode used by the IBM Colour/Graphics Monitor Adapter
    byte readMode;              // Bit 3: Read Mode; selects between two read modes (0 and 1). See documentation for more detail
                                // 0: Read mode 0
                                // 1: Read mode 1
                                // Bit 2: Not used
    byte writeMode;             // Bits 1-0: Write mode; selects between four write modes (0-3). See documentation for more detail
                                // 00b - Write mode 0
                                // 01b - Write mode 1    
                                // 10b - Write mode 2    
                                // 11b - Write mode 3
                                // Register 0x06: Miscellaneous Graphics (bits 0 - 3)
    byte memoryMapSelect;       // Bits 3-2: Specifies range of host memory used 
                                // 00: use A0000-BFFFF (128K region)
                                // 01: use A0000-AFFFF (64K region) EGA/VGA graphics modes
                                // 10: use B0000-B7FFF (32K region) Monochrome modes
                                // 11: use B8000-BFFFF (32K region) CGA modes
    byte chainOddEvenEnable;    // Bit 1: Directs system address bit map selector
                                // 0: No map selection enabled
                                // 1: map selection enabled; odd map selected when A0 is 1, even map selected when A0 is 0
    byte alphaNumDisable;       // Bit 0: Controls alphanumeric mode addressing
                                // 0: do not select graphics mode
                                // 1: select graphics mode, disables character generator latches
    byte colourDontCare;        // Register 0x07: Colour Don't Care (bits 0 - 3)
                                // Used in Read Mode 1 to select the planes used in comparisons
    byte bitMask;               // Register 0x08: Bit Mask (bits 0 - 7)
                                // Used in Write Modes 0, 2 and 3 to select data from ALU (1) or latch register (0)
    

    // Helper table used to XOR and AND each latch in Read Mode 1 using colourCompare and colourDontCare as indices, respectively
    protected final static byte colourCompareTable[][] =  { {0x00, 0x00, 0x00, 0x00}, 
                                                          {(byte) 0xFF, 0x00, 0x00, 0x00},
                                                          {0x00, (byte) 0xFF, 0x00, 0x00},
                                                          {(byte) 0xFF, (byte) 0xFF, 0x00, 0x00},
                                                          {0x00, 0x00, (byte) 0xFF, 0x00},
                                                          {(byte) 0xFF, 0x00, (byte) 0xFF, 0x00},
                                                          {0x00, (byte) 0xFF, (byte) 0xFF, 0x00},
                                                          {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00},
                                                          {0x00, 0x00, 0x00, (byte) 0xFF},
                                                          {(byte) 0xFF, 0x00, 0x00, (byte) 0xFF},
                                                          {0x00, (byte) 0xFF, 0x00, (byte) 0xFF},
                                                          {(byte) 0xFF, (byte) 0xFF, 0x00, (byte) 0xFF},
                                                          {0x00, 0x00, (byte) 0xFF, (byte) 0xFF},
                                                          {(byte) 0xFF, 0x00, (byte) 0xFF, (byte) 0xFF},
                                                          {0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                                                          {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}
                                                          };    
    
    /**
     * Return variables to default values
     */
    protected void reset()
    {
        index = 0;
        Arrays.fill(latch, (byte) 0);
        setReset = 0;
        enableSetReset = 0;
        colourCompare = 0;
        dataOperation = 0;
        dataRotate = 0;
        readMapSelect = 0;
        shift256Reg = 0;
        hostOddEvenEnable = 0;
        readMode = 0;
        writeMode = 0;
        memoryMapSelect = 2; // Monochrome mode
        chainOddEvenEnable = 0;
        alphaNumDisable = 0;
        colourDontCare = 0;
        bitMask = 0;
    }
}
