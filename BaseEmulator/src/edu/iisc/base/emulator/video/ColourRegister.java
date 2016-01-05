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
 * Colour Register
 * Selects the 256 colour palette from the maximum possible colours.<BR>
 * Consists of 5 registers, which are addressed via I/O ports [0x3C7] - [0x3C9] 
 *
 */
public class ColourRegister
{

    byte pixelMask;                 // (RW)[0x3C8] Pixel Mask register (default FFh)
                                    // Used as AND mask for colour-register address
    
    byte dacState;                  // (R)[0x3C7] - DAC State register (bits 0 - 1)
                                    // Returns whether the DAC is prepared to accept reads or writes
                                    // NOTE: documentation differs on values below, some indicating 0 is read and 3 is write...
                                    // 00 - prepared to accept writes from the DAC data register
                                    // 11 - prepared to accept reads from the DAC data register
    
    byte dacReadAddress;            // (W)[0x3C7] - DAC Address Read Mode register
                                    // Contains the value of the first DAC data entry to be read
    int dacReadCounter;             // dacReadAddress counter indicating which colour (0, 1, 2) is to be read 
    
    byte dacWriteAddress;           // (RW)[0x3C8] - DAC Address Write Mode register
                                    // Contains the value of the first DAC data entry to be written
    int dacWriteCounter;            // dacWriteAddress counter indicating which colour (0, 1, 2) is to be written
    
                                    // (RW)[0x3C9] - DAC Data register
                                    // Reads/writes a colour value from the Pixel array at index specified by dac[Read|Write]Address,
                                    // and colour specified by the corresponding Read/Write counter.
                                    // Handled directly in the VGA class
    

    /**
     * Return variables to default values
     */
    protected void reset()
    {
        pixelMask = (byte) 0xFF;
        dacState = 0x01;
        
        dacReadAddress = 0;
        dacReadCounter = 0;
        
        dacWriteAddress = 0;
        dacWriteCounter = 0;
    }
}
