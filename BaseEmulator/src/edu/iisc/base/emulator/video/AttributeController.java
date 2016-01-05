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
 * Attribute Controller: http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/attrreg.htm
 */

/**
 *  Attribute Controller registers
 *  Selects the 16 color and 64 color palettes used for EGA/CGA compatibility.<BR>
 *  Consists of 20 8-bit registers; these are accessed via a pair of registers, <BR>
 *  the Address/Data Register [0x3C0] and the Data Read Register [0x3C1]
 */
public class AttributeController
{
    int index;                      // Index used to address registers; set via I/O port 0x3C0
    int paletteAddressSource;       // Disabled to load colour values in the internal palette
                                    // 0 - Load colour values enabled
                                    // 1 - Normal operation (load disabled)
    boolean dataAddressFlipFlop;    // true - Data write mode
                                    // false - Address mode
    
    
    // Registers
    byte[] paletteRegister = new byte[0x10];    // Registers 0x00 - 0x0F: Internal palette index (bits 0 - 6)
                                                // Dynamic mapping between text attrib/graphic colours and on-screen display
                                                // 0 - Do not select colour
                                                // 1 - Select appropriate colour
    ModeControlRegister modeControlReg = new ModeControlRegister(); // Register 0x10: Mode Control register
    byte overscanColour;                        // Register 0x11: Overscan color
                                                // Select border colour used in 80-column alphanumeric mode and all graphics mode but 0x4, 0x5, 0xD
    byte colourPlaneEnable;                     // Register 0x12: Colour plane enable (bits 0 - 4)
                                                // 0 - disable display memory colour plane
                                                // 1 - enable display memory colour plane
    byte horizPixelPanning;                     // Register 0x13: Horizontal pixel panning (bits 0 - 4)
                                                // Number of pixels the video data is shifted left (alphanum / graphics mode)
    byte colourSelect;                          // Register 0x14: Color select (bits 0 - 3)
                                                // Bits 2-3: MSB of 8-bit colour value to DAC (except mode 0x13)
                                                // Bits 0-1: Can be used in place of P4, P5 of paletteRegister; selected in modeControlReg
    
    /**
     * Return variables to default values
     */
    protected void reset()
    {
        index = 0;
        paletteAddressSource = 1;
        dataAddressFlipFlop = false;
        Arrays.fill(paletteRegister, (byte) 0);
        modeControlReg.reset();
        overscanColour = 0;
        colourPlaneEnable = 0x0f;
        horizPixelPanning = 0;
        colourSelect = 0;
    }
}
