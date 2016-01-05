/*
 * $Revision: 1.2 $ $Date: 2007-08-23 15:39:51 $ $Author: jrvanderhoeven $
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
 * Text mode information
 * Collection of text mode variables required for a screen update
 * 
 */
public class TextModeAttributes
{
    // Attributes and features of text mode
    public short fullStartAddress;  // Display memory address of upper left pixel / character (CRTC Start high + low)
    public byte cursorStartLine;    // Scanline where the cursor begins
    public byte cursorEndLine;      // Scanline where the cursor ends; if cs_end < cs_start, no cursor is drawn
    public short lineOffset;        // Number of bytes (char + attribute) per display line
    public short lineCompare;       // Scan line where a horiz. division can occur; 0x3FF indicates no division
    public byte horizPanning;       // 'Pixel shift count': specifies number of pixels the video data is shifted left
    public byte vertPanning;        // 'Preset Row Scan': specifies how many scan lines to scroll display upward (0 - Maximum Scan Line)
    public byte lineGraphics;       // 9 bit wide chars - 0: 9th column is replicated from 8th column; 1: 9th column is set to background
    public byte splitHorizPanning;  // 'Pixel panning mode': allows upper screen half to pan independently of lower half.
    
    
    // Methods
    
    public short[] getAttributes()
    {
        return new short[] {fullStartAddress, cursorStartLine, cursorEndLine, lineOffset, lineCompare, horizPanning, vertPanning, lineGraphics, splitHorizPanning};
    }
    
}
