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
 * Complete descriptions of the following VGA register can be found at:
 * CRT Controller:       http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/crtcreg.htm
 */

/**
 * CRT Controller Registers, Address (RW: 3x4h) and Data (RW: 3x5h),
 * where x can be B or D, set in colorEmulation
 *
 */
public class CRTControllerRegister
{
    byte index;                     // Index into CRTC register for data write; set via Address register
    boolean protectEnable;          // Protect video timing registers from values unsuitable for VGA timings; legacy programs attempted this
                                    // Writing to registers 00h-07h is disabled, except Line Compare of Overflow register (0x07).
    byte[] regArray = new byte[0x19];// 0x00 - Horizontal Total Register
                                    // 0x01 - End Horizontal Display Register
                                    // 0x02 - Start Horizontal Blanking Register
                                    // 0x03 - End Horizontal Blanking Register
                                    // 0x04 - Start Horizontal Retrace Register
                                    // 0x05 - End Horizontal Retrace Register
                                    // 0x06 - Vertical Total Register
                                    // 0x07 - Overflow Register
                                    // 0x08 - Preset Row Scan Register
                                    // 0x09 - Maximum Scan Line Register
    byte scanDoubling;              //        Bit 7 of register 0x09
                                    // 0x0A - Cursor Start Register
                                    // 0x0B - Cursor End Register
                                    // 0x0C - Start Address High Register
                                    // 0x0D - Start Address Low Register
                                    // 0x0E - Cursor Location High Register
                                    // 0x0F - Cursor Location Low Register
                                    // 0x10 - Vertical Retrace Start Register
                                    // 0x11 - Vertical Retrace End Register
                                    // 0x12 - Vertical Display End Register
                                    // 0x13 - Offset Register
                                    // 0x14 - Underline Location Register
                                    // 0x15 - Start Vertical Blanking Register
                                    // 0x16 - End Vertical Blanking
                                    // 0x17 - CRTC Mode Control Register
                                    // 0x18 - Line Compare Register
    /**
     * Return variables to default values
     */
    protected void reset()
    {
        index = 0;
        protectEnable = false;
        Arrays.fill(regArray, (byte) 0);
        scanDoubling = 0;
    }
}
