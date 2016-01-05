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
 * Attribute Mode Control Register (index 10h)
 * Controls the mode operations of the Attribute Controller.<BR>
 * Consists of 7 bits (bit 4 empty)
 *
 */
public class ModeControlRegister
{
    byte paletteBitsSelect;     // Bit 7 - Palette bits 5-4 select
                                // 0 - P5 and P4 are outputs of the PaletteRegister
                                // 1 - P5 and P4 are bits 1 and 0 of the colorSelect register [0x14] 
    byte colour8Bit;            // Bit 6 - 8 bit color enable
                                // 0 - Set to 0 in all but 256-color mode (0x13)
                                // 1 - Video data sampled so 8 bits are available to set a color (in 256-color mode only)
    byte pixelPanningMode;      // Bit 5 - Allows upper screen half to pan independently of lower half.
                                // 0 - Nothing happens
                                // 1 - Upon successful line compare, bottom displayed as if pixelShiftCount and bytePanning CRTC[0x08] are 0
                                // Bit 4 is not used
    byte blinkIntensity;        // Bit 3 - Blink/background intensity
                                // 0 - MSB attribute sets background intensity, allowing 16 colors for background 
                                // 1 - Blinking enabled (not implemented)
    byte lineGraphicsEnable;    // Bit 2 - Provides continuity in 9-bit char. modes for horiz. line chars in range C0h-DFh
                                // 0 - 9th column replicated from 8th column of character
                                // 1 - 9th column set to background
    byte monoColourEmu;         // Bit 1 - Monochrome/color emulation (according to docs does not work??)
                                // 0 - Color emulation
                                // 1 - Monochrome emulation
    byte graphicsEnable;        // Bit 0 - Enables graphics mode
                                // 0 - non-graphics mode
                                // 1 - graphics mode
    
    /**
     * Return variables to default values
     */
    protected void reset()
    {
        paletteBitsSelect = 0;
        colour8Bit = 0;
        pixelPanningMode = 0;
        blinkIntensity = 0;
        lineGraphicsEnable = 1;
        monoColourEmu = 0;
        graphicsEnable = 0;
    }
}
