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

import java.util.Arrays;

/*
 * Complete descriptions of the following VGA registers can be found at:
 * External:             http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/extreg.htm
 * Sequencer:            http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/seqreg.htm
 * Graphics:             http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/graphreg.htm
 * CRT Controller:       http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/crtcreg.htm
 * Attribute Controller: http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/attrreg.htm
 * Colour:               http://www.stanford.edu/class/cs140/projects/pintos/specs/freevga/vga/colorreg.htm
 * 
 * Other register information can be found at http://mudlist.eorbit.net/~adam/pickey/ports.html
 */

/**
 * Implementation of a VGA videocard
 * Based on Bochs code (http://bochs.sourceforge.net/)
 * Implements all VGA registers (Sequencer, CRTC, Graphics, Attribute, Colour and Miscellaneous)
 * and more... 
 * 
 */
public class VideoCard
{

    // Define maximum resolutions for this videocard
    private final static int XRES_MAX = 800;
    private final static int YRES_MAX = 600;
    
    // The display is divided into tiles of size 16x24, which can be updated individually
    protected final static int X_TILESIZE = 16;
    protected final static int Y_TILESIZE = 24;
    private final static int X_NUM_TILES = (XRES_MAX / X_TILESIZE);
    private final static int Y_NUM_TILES = (YRES_MAX / Y_TILESIZE);
    byte[] tile = new byte[X_TILESIZE * Y_TILESIZE * 4];    // Array of 8-bit values representing a block of pixels with
                                                            //  dimension equal to the 'tilewidth' & 'tileheight' parameters to
                                                            //  Each value specifies an index into the array of colours allocated
    boolean vgaTileUpdated[][] = new boolean[X_NUM_TILES][Y_NUM_TILES];     // Indicates if a tile needs to be updated/redrawn
   

    // Create instances of all required registers
    MiscellaneousOutputRegister miscOutputRegister = new MiscellaneousOutputRegister();
    SequencerRegister sequencer = new SequencerRegister();
    GraphicsController graphicsController = new GraphicsController();
    CRTControllerRegister crtControllerRegister = new CRTControllerRegister();
    AttributeController attributeController = new AttributeController();
    ColourRegister colourRegister = new ColourRegister();
    
    // Create a 256 array of RGB pixels
    Pixel[] pixels = new Pixel[256];
    
    // Other registers
    boolean vgaEnabled;     // (RW)[0x3C3] Video subsystem enable
                            // Bit 7-4: reserved (0)
                            // Bit 3  : select video subsystem [0x46E8h]
                            // Bit 2-1: reserved 
                            // Bit 0  : select video subsystem [0x3C3]
                            // Set currently as boolean as is only used to check if enabled/disabled
    
    // Input Status #1 - consist of two separate registers, one each of mono and colour emulation
    // Strictly speaking th
                            // (R)[0x3BA] (mono) 
    byte vertRetrace;       // Bit 3: Vertical Retrace
                            //        0: Display interval; 1: Display is in vertical retrace interval
    byte displayDisabled;   // Bit 0: Display Disabled
                            //        0: Display mode; 1: Horizontal or vertical retrace period is active
    
                            // (R)[0x3DA] (colour)
    byte horizRetrace;      // Bit 0: Horizontal Retrace
                            //        0: Do not use memory; 1: Memory access enabled

    // Video memory variables
    public byte[] vgaMemory = new byte[256 * 1024];     // VGA memory contents 2 * [A0000 - BFFFF] (??)
    boolean vgaMemReqUpdate;                            // Indicates if the vga memory has been updated, and a redraw is necessary
    public byte[] textSnapshot = new byte[128 * 1024];  // Current text snapshot, is used to compare current screen characters with newly arrived characters.
    													// If no differences are found, do not draw anything again. Else, only redraw changed character.
    
    // Helper variables
    int lineOffset;                 // Specifies address difference between consecutive scanlines/character lines
    int lineCompare;                // Indicates scan line where horiz. division can occur. No division when set to 0x3FF
    int verticalDisplayEnd;         // Vertical scanline counter: scanline value immediately after last scanline of active display (i.e. roughly the screenHeight)
                                    // Holds same value as CRTC[0x07]&0x42 + CRTC[0x12]

    // Constructor
    // Initialises Pixel array
    public VideoCard()
    {
        Arrays.fill(pixels, new Pixel());
    }
    
    // Methods
    /**
     * If within tile bounds, sets the vgaTileUpdate to value
     * 
     * @param xtile Location of x tile
     * @param ytile Location of y tile
     * @param value True or false reflecting whether this tile has been updated
     */
    final protected void setTileUpdate(int xtile, int ytile, boolean value)
    {
        if (((xtile) < X_NUM_TILES) && ((ytile) < Y_NUM_TILES))
            vgaTileUpdated[(xtile)][(ytile)] = value;
    }

    /**
     * If within tile bounds, retrieves the status of the vgaTileUpdate value
     * 
     * @param xtile Location of x tile
     * @param ytile Location of y tile
     * @return True or false reflecting whether this tile has been updated
     */
    final protected boolean getTileUpdate(int xtile, int ytile)
    {
        return ((((xtile) < X_NUM_TILES) && ((ytile) < Y_NUM_TILES)) ? vgaTileUpdated[xtile][ytile] : false);
    }
    
    /**
     * Return variables to default values
     */
    protected void reset()
    {
        miscOutputRegister.reset();
        sequencer.reset();
        graphicsController.reset();
        crtControllerRegister.reset();
        attributeController.reset();
        colourRegister.reset();
        
        vgaEnabled = true;
        vgaMemReqUpdate = false;
        lineOffset = 80;
        lineCompare = 1023;
        verticalDisplayEnd = 399;

        for (int i = 0; i < 256; i++)
        {
            pixels[i].red = 0;
            pixels[i].green = 0;
            pixels[i].blue = 0;
        }
        Arrays.fill(vgaMemory, (byte) 0);
    }    

}
