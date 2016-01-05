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

/**
 *  Miscellaneous Output Register
 *  8-bit register containing miscellaneous data. This register<BR>
 *  is addressed via its own I/O port: Read [0x3CC], Write [0x3C2]
 *
 */
public class MiscellaneousOutputRegister
{
    byte verticalSyncPol;   // Bit 7: Polarity of the vertical sync pulse (0 = positive retrace)
    byte horizontalSyncPol; // Bit 6: Polarity of the horizontal sync pulse (0 = positive retrace)
                            // Also: Vertical resolution
                            // 00: (EGA) 200 lines
                            // 01: (VGA) 400 lines
                            // 10: (EGA/VGA) 350 lines
                            // 11: (VGA) 480 lines
    byte lowHighPage;       // Bit 5: when in odd/even modes, selects upper/lower 64K page of memory
                            // 0: low page
                            // 1: high page 
                            // Bit 4: not used
    byte clockSelect;       // Bits 3-2: Clock select; controls selection of dot clocks used in display timing. 
                            // 00: 25Mhz   (320/640 pixel wide modes)
                            // 01: 28Mhz   (360/720 pixel wide modes)
                            // 10: undefined, possible external clocks
                            // 11: undefined, possible external clocks
    byte ramEnable;         // Bit 1: controls system access to display buffer
                            // 0: disable address decode for display buffer
                            // 1: enable address decode
    byte ioAddressSelect;   // Bit 0: selects CRT controller address, influencing monochrome/color emulation
                            // 0: monochrome adapter, CRTC base address [0x3Bn]; Input Status Register [0x3BA] 
                            // 1: color/graphics adapter, CRTC base address [0x3Dn]; Input Status Register [0x3DA]
    
    /**
     * Return variables to default values
     */
    protected void reset()
    {
        verticalSyncPol = 1;
        horizontalSyncPol = 1;
        lowHighPage = 0;
        clockSelect = 0;
        ramEnable = 1;
        ioAddressSelect = 1;
    }
}