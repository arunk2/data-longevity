package edu.iisc.base.emulator.video;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.iisc.base.emulator.MemoryRegion;
import edu.iisc.base.emulator.Processor;
import edu.iisc.base.emulator.screen.Screen;

/**
 * An implementation of a video (VGA) module.
 * 
 * @see Module 
 * 
 * Metadata module 
 * ******************************************** 
 * general.type               : video 
 * general.name               : General VGA display adapter 
 * general.architecture       : Von Neumann 
 * general.description        : Models a simple VGA adapter 
 * general.creator            : Tessella Support Services, Koninklijke Bibliotheek, Nationaal Archief of the Netherlands 
 * general.version            : 1.0 
 * general.keywords           : VGA, Video Graphics Array, video, graphics, 640, 480 
 * general.relations          : Motherboard
 * general.yearOfIntroduction : 
 * general.yearOfEnding       : 
 * general.ancestor           : 
 * general.successor          :
 * 
 * Notes:
 * - This code is based on Bochs code which has been ported to Java.
 * 
 */

public class Video extends MemoryRegion 
{
    // Attributes
	
    private VideoCard videocard;
    private TextModeAttributes textModeAttribs;
    private TextTranslation textTranslation;

    // Toggles
    private boolean isObserved;
    private boolean debugMode;

    // Timing
    private int updateInterval;
    
    // Buffer variables
    private int initialScreenWidth = 640;
    private int initialScreenHeight = 480;
    int oldScreenWidth = 0;
    int oldScreenHeight = 0;
    int oldMaxScanLine = 0;
	public Screen screen;
    
    // Logging
    private static Logger logger = Logger.getLogger("nl.kbna.dioscuri.module.video");

    
    // Constants
    
    // Module specifics
    public final static int MODULE_ID = 1;
    public final static String MODULE_TYPE = "video";
    public final static String MODULE_NAME = "Video Graphics Array (VGA) adapter";

    private final static int MAX_TEXT_LINES = 100;

    // Constructor
    /**
     * Class constructor
     */
    public Video(int videoRamBase, int videoRamSize) {
    	//for output
    	screen = new Screen(this);
    	
    	//initialize memory
		from = videoRamBase;
		to = videoRamBase+videoRamSize-1;
		memory = new byte[to-from+1];
    	
        // Create new videocard
        videocard = new VideoCard();

        // Initialise variables
        videocard.vgaMemReqUpdate = false;

        // Create textModeAttributes collection for use in text mode
        textModeAttribs = new TextModeAttributes();

        // Create text translation for converting textmode data into modern characters
        textTranslation = new TextTranslation();
	}


	/**
     * Returns the ID of the module
     * 
     * @return string containing the ID of module
     * @see Module
     */
    public int getID()
    {
        return MODULE_ID;
    }

    /**
     * Returns the type of the module
     * 
     * @return string containing the type of module
     * @see Module
     */
    public String getType()
    {
        return MODULE_TYPE;
    }

    /**
     * Returns the name of the module
     * 
     * @return string containing the name of module
     * @see Module
     */
    public String getName()
    {
        return MODULE_NAME;
    }

    /**
     * Reset all parameters of module
     * 
     * @return boolean true if module has been reset successfully, false otherwise
     */
    public boolean reset()
    {

    	// Resetting the videocard will also reset all submodules
        videocard.reset();

        // Set all tiles to not need updating
        for (int y = 0; y < (initialScreenHeight / VideoCard.Y_TILESIZE); y++)
            for (int x = 0; x < (initialScreenWidth / VideoCard.X_TILESIZE); x++)
                videocard.setTileUpdate(x, y, false);

        logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Module has been reset");

        return true;
    }

    /**
     * Returns the status of observed toggle
     * 
     * @return state of observed toggle
     * @see Module
     */
    public boolean isObserved()
    {
        return isObserved;
    }

    /**
     * Sets the observed toggle
     * 
     * @param status
     * @see Module
     */
    public void setObserved(boolean status)
    {
        isObserved = status;
    }

    /**
     * Returns the status of the debug mode toggle
     * 
     * @return state of debug mode toggle
     * @see Module
     */
    public boolean getDebugMode()
    {
        return debugMode;
    }

    /**
     * Sets the debug mode toggle
     * 
     * @param status
     * @see Module
     */
    public void setDebugMode(boolean status)
    {
        debugMode = status;
    }

    /**
     * Returns a dump of this module
     * 
     * @return string
     * @see Module
     */
    public String getDump()
    {
        String dump = "Video status:\n";

        dump += "Read mode: " + videocard.graphicsController.readMode + "\n";
        dump += "Write mode: " + videocard.graphicsController.writeMode + "\n";
        
        // dump += "Graphics mode: " + ...
        // dump += "Text mode: " + ...
        
        return dump;
    }

    // ******************************************************************************
    // ModuleDevice Methods

    /**
     * Retrieve the interval between subsequent updates
     * 
     * @return int interval in microseconds
     */
    public int getUpdateInterval()
    {
        return updateInterval;
    }

    /**
     * Defines the interval between subsequent updates
     * 
     * @param int interval in microseconds
     */
    public void setUpdateInterval(int interval)
    {
        // Check if interval is > 0
        if (interval > 0)
        {
            updateInterval = interval;
        }
        else
        {
            updateInterval = 1000; // default is 1 ms
        }
//        motherboard.resetTimer(this, updateInterval);
    }


    /**
     * Update device
     * Refreshes the framebuffer and send redraw to screen
     */
    public void update()
    {
        int screenHeight;
        int screenWidth;
        
        // Only update if the video memory has been updated
//        if (!videocard.vgaMemReqUpdate)
//            return;

        // Determine if in graphic/text mode; this is set in graphics register 0x06
        if (videocard.graphicsController.alphaNumDisable != 0)
        {}

        // alphaNumDisable == 0; Text mode
        else
        {
            // Cursor attributes
            int cursorXPos;         // Cursor column position
            int cursorYPos;         // Cursor row position
            int cursorWidth = 8; // 8 or 9 pixel cursor width
            int fullCursorAddress = 162; // Cursor location (byte number in vga memory) 

            // Screen attributes 
            int maxScanLine = 15;    // Character height - 1
            int numColumns = 80;        // (Char. clocks - 1) + 1 , i.e. number of columns
            int numRows = 25;                                                                // Number of text rows in screen 
            screenWidth = cursorWidth * numColumns;                                     // Screen width in pixels
            videocard.verticalDisplayEnd = 399;
            screenHeight = videocard.verticalDisplayEnd + 1;                            // Screen height in pixels
            
            int fullStartAddress = 0; // Upper left character of screen

            logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " update() in progress; text mode");
            
            // Collect text features to be passed to screen update
            textModeAttribs.fullStartAddress = (short) 0;  
            textModeAttribs.cursorStartLine = (byte) 14;
            textModeAttribs.cursorEndLine = (byte) 15;
            textModeAttribs.lineOffset = (short) 160;
            textModeAttribs.lineCompare = (short) 1023;
            textModeAttribs.horizPanning = (byte) 0;
            textModeAttribs.vertPanning = (byte) 0;
            textModeAttribs.lineGraphics = 1;
            textModeAttribs.splitHorizPanning = 0;
                        
            // Check if the GUI window needs to be resized
            if ((screenWidth != oldScreenWidth) || (screenHeight != oldScreenHeight) || (maxScanLine != oldMaxScanLine))
            {
                screen.updateScreenSize(screenWidth, screenHeight, cursorWidth, maxScanLine + 1);
                oldScreenWidth = screenWidth;
                oldScreenHeight = screenHeight;
                oldMaxScanLine = maxScanLine;
            }

            // Determine cursor position in terms of rows and columns
            if (fullCursorAddress < fullStartAddress)   // Cursor is not on screen, so set unreachable values
            {
                cursorXPos = 0xFFFF;
                cursorYPos = 0xFFFF;
            }
            else    // Cursor is on screen, calculate position
            {
                cursorXPos = ((fullCursorAddress - fullStartAddress) / 2) % (screenWidth / cursorWidth);
                cursorYPos = ((fullCursorAddress - fullStartAddress) / 2) / (screenWidth / cursorWidth);
            }
            
            // Call screen update for text mode
            screen.updateText(0, fullStartAddress, cursorXPos, cursorYPos, textModeAttribs.getAttributes(), numRows);
            
            // Screen has been updated, copy new contents into 'snapshot'
            System.arraycopy(videocard.vgaMemory, fullStartAddress, videocard.textSnapshot, 0, 2 * numColumns * numRows);
            
            videocard.vgaMemReqUpdate = false;
        }
    }

    /**
     * Prepares an area of the screen starting at xOrigin, yOrigin and size width, height<BR>
     * for an update; in graphics mode this sets the corresponding tiles for update, in <BR>
     * text mode this invalidates the whole text snapshot
     * 
     * @param xOrigin   The x coordinate of the upper left value of the region that is updated, in pixels 
     * @param yOrigin   The y coordinate of the upper left value of the region that is updated, in pixels
     * @param width The width of the region to be updated, in pixels
     * @param height    The height of the region to be updated, in pixels
     */
    void setAreaForUpdate(int xOrigin, int yOrigin, int width, int height)
    {
        
        // TODO: xOrigin and yOrigin are always called as 0,0; possibility for optimisation?

        // Helper variables
        int xTileOrigin, yTileOrigin;   // x, y values in terms of tiles 
        int xTileMax, yTileMax;         // maximum x, y values in terms of tiles


        // Check if an update is necessary at all
        if ((width == 0) || (height == 0))
        {
            return;
        }

        videocard.vgaMemReqUpdate = true;

        // Check which mode the adapter is in
        if (videocard.graphicsController.alphaNumDisable != 0)
        {
            // Graphics mode; calculate and set the tiles which need an update

            // Determine x, y values as a function of tiles
            xTileOrigin = xOrigin / VideoCard.X_TILESIZE;
            yTileOrigin = yOrigin / VideoCard.Y_TILESIZE;

            // Check if value is within current screen limits
            if (xOrigin < oldScreenWidth)
            {
                xTileMax = (xOrigin + width - 1) / VideoCard.X_TILESIZE;
            }
            else
            {
                xTileMax = (oldScreenWidth - 1) / VideoCard.X_TILESIZE;
            }
            
            // Check if value is within current screen limits
            if (yOrigin < oldScreenHeight)
            {
                yTileMax = (yOrigin + height - 1) / VideoCard.Y_TILESIZE;
            }
            else
            {
                yTileMax = (oldScreenHeight - 1) / VideoCard.Y_TILESIZE;
            }
            
            // Set tiles for updating; note that the upper limits ([x,y]tileMax) will be taken care of in setTileUpdate
            for (int yCounter = yTileOrigin; yCounter <= yTileMax; yCounter++)
            {
                for (int xCounter = xTileOrigin; xCounter <= xTileMax; xCounter++)
                {
                   videocard.setTileUpdate(xCounter, yCounter, true);
                }
            }

        }
        else
        {
            // Text mode; simply invalidate the whole text snapshot
            Arrays.fill(videocard.textSnapshot, (byte) 0);
        }
    }

    /**
     * IN instruction to video adapter<BR>
     * 
     * @param portAddress the target port; can be any of 0x3B4, 0x3B5, 0x3BA, 0x3C0-0x3CF, 0x3D4, 0x3D4, 0x3DA
     * @return byte from target port
     */
    public byte getIOPortByte(int portAddress)
    {
        byte returnValue = 0;   // Data returned from requested port

        // Ensure the correct ports are used for colour/monochrome mode
        if ((portAddress >= 0x3B0) && (portAddress <= 0x3BF) && (videocard.miscOutputRegister.ioAddressSelect != 0))
        {
            // Adapter is in colour mode, but addressing monochrome mode ports, so return default value
            return (byte) (0xFF);
        }
        if ((portAddress >= 0x3D0) && (portAddress <= 0x3DF) && (videocard.miscOutputRegister.ioAddressSelect == 0))
        {
            // Adapter is in monochrome mode, but addressing colour mode ports, so return default value
            return (byte) (0xFF);
        }

        switch (portAddress)
        {
            case 0x3BA: // Input Status 1 (monochrome)
            case 0x3CA: // Feature Control
                // TODO: According to specs, all bits of the FC are reserved, just return???
            case 0x3DA: // Input Status 1 (colour)
                
                long microSeconds;
                short vertResolution;
                
                // Reset flip-flop to address mode when reading these ports
                videocard.attributeController.dataAddressFlipFlop = false;

                
                // Reset displayDisabled, only done here
                videocard.displayDisabled = 0;

                // Determine the current internal 'time', in microseconds.
                // This can be inferred from total instructions executed divided by the instructions executed per second (approx.) 
//                microSeconds = (long) (( ((double)cpu.getCurrentInstructionNumber()/cpu.getIPS())) * 1000000);

                // Determine vertical display size from verticalSyncPol and horizontalSyncPol
                // This is needed to calculate the vertical retrace period
                switch ((videocard.miscOutputRegister.verticalSyncPol << 1) | videocard.miscOutputRegister.horizontalSyncPol)
                {
                    case 0:
                        vertResolution = 200;
                        break;
                    case 1:
                        vertResolution = 400;
                        break;
                    case 2:
                        vertResolution = 350;
                        break;
                    default:
                        vertResolution = 480;
                        break;
                }
                
                // Check if a horizontal (colour) or vertical (mono) retrace is in progress; if so, also set displayDisabled
                // Similar to Bochs, use a 72 Hz vertical frequency. This means a retrace happens
//                if ((microSeconds % 13888) < 70)
//                {
//                    videocard.vertRetrace = 1;
//                    videocard.displayDisabled = 1;
//                }
//                else
                {
                    videocard.vertRetrace = 0;
                }
//                if ((microSeconds % (13888 / vertResolution)) == 0)
//                {
//                    videocard.horizRetrace = 1;
//                    videocard.displayDisabled = 1;
//                }
//                else
                {
                    videocard.horizRetrace = 0;
                }

                return (byte) (videocard.vertRetrace << 3 | videocard.displayDisabled);

            case 0x3C0: // Attribute Controller Attribute Address Register (only read when the flipflop is in address mode)
                // Check flipflop state
                if (!videocard.attributeController.dataAddressFlipFlop)
                {
                    return (byte) ((videocard.attributeController.paletteAddressSource << 5) | videocard.attributeController.index);
                }
                else
                {
                    logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " Port [0x3C0] read, but flipflop not set to address mode");
                    return 0;
                }

            case 0x3C1: // Attribute Controller Data Read Register, uses index to determine register to read
                switch (videocard.attributeController.index)
                {
                    // Palette registers
                    case 0x00:
                    case 0x01:
                    case 0x02:
                    case 0x03:
                    case 0x04:
                    case 0x05:
                    case 0x06:
                    case 0x07:
                    case 0x08:
                    case 0x09:
                    case 0x0A:
                    case 0x0B:
                    case 0x0C:
                    case 0x0D:
                    case 0x0E:
                    case 0x0F:
                        returnValue = videocard.attributeController.paletteRegister[videocard.attributeController.index];
                        return (returnValue);

                    case 0x10:  // Mode control register
                        return ((byte) ((videocard.attributeController.modeControlReg.graphicsEnable << 0) | 
                                        (videocard.attributeController.modeControlReg.monoColourEmu << 1)  | 
                                        (videocard.attributeController.modeControlReg.lineGraphicsEnable << 2) | 
                                        (videocard.attributeController.modeControlReg.blinkIntensity << 3) |
                                        (videocard.attributeController.modeControlReg.pixelPanningMode << 5) | 
                                        (videocard.attributeController.modeControlReg.colour8Bit << 6) | 
                                        (videocard.attributeController.modeControlReg.paletteBitsSelect << 7)));

                    case 0x11:  // Overscan colour
                        return (videocard.attributeController.overscanColour);

                    case 0x12:  // Colour plane enable
                        return (videocard.attributeController.colourPlaneEnable);

                    case 0x13:  // Horizontal pixel panning
                        return (videocard.attributeController.horizPixelPanning);

                    case 0x14:  // Colour select
                        return (videocard.attributeController.colourSelect);

                    default:
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " Port [0x3C1] reads unknown register 0x" + Integer.toHexString(videocard.attributeController.index).toUpperCase());
                        return 0;
                }

            case 0x3C2: // Input Status 0
                logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Port [0x3C1] reads Input Status #0; ignored");
                return 0;

            case 0x3C3: // VGA Enable Register
                return (videocard.vgaEnabled) ? (byte) 1 : 0;

            case 0x3C4: // Sequencer Index
                return (videocard.sequencer.index);

            case 0x3C5: // Sequencer Registers 0-4, based on index
                switch (videocard.sequencer.index)
                {
                    case 0: // Asynch and synch reset
                        logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Port [0x3C5] reads sequencer reset");
                        return (byte) (videocard.sequencer.aSynchReset | (videocard.sequencer.synchReset << 1));

                    case 1: // Clocking mode
                        logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Port [0x3C5] reads sequencer clocking mode");
                        return (videocard.sequencer.clockingMode);

                    case 2: // Map mask register
                        return (videocard.sequencer.mapMask);

                    case 3: // Character map select register
                        return (videocard.sequencer.characterMapSelect);

                    case 4: // Memory mode register */
                        return ((byte) ((videocard.sequencer.extendedMemory << 1) | 
                                        (videocard.sequencer.oddEvenDisable << 2) | 
                                        (videocard.sequencer.chainFourEnable << 3)));

                    default:
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " Port [0x3C5] reads unknown register 0x" + Integer.toHexString(videocard.sequencer.index).toUpperCase());
                        return 0;
                }

            case 0x3C6: // Pixel mask register
                return (videocard.colourRegister.pixelMask);

            case 0x3C7: // DAC state register
                return (videocard.colourRegister.dacState);

            case 0x3C8: // DAC write index
                return (videocard.colourRegister.dacWriteAddress);

            case 0x3C9: // DAC Data Register; read colour values in sets of three. Automatically increment counter and address
                // Ensure DAC read state is enabled
                if (videocard.colourRegister.dacState == 0x03)
                {
                    switch (videocard.colourRegister.dacReadCounter)
                    {
                        // Cast dacReadAddress to integer as it will be used as index
                        case 0:
                            returnValue = videocard.pixels[((int) videocard.colourRegister.dacReadAddress) & 0xFF].red;
                            break;
                        case 1:
                            returnValue = videocard.pixels[((int) videocard.colourRegister.dacReadAddress) & 0xFF].green;
                            break;
                        case 2:
                            returnValue = videocard.pixels[((int) videocard.colourRegister.dacReadAddress) & 0xFF].blue;
                            break;
                        default:
                            // Shouldn't get to here...
                            returnValue = 0;
                    }
                    // Automatically increment counter
                    videocard.colourRegister.dacReadCounter++;
                    
                    // Set of 3 read; reset counter and increment address
                    if (videocard.colourRegister.dacReadCounter >= 3)
                    {
                        videocard.colourRegister.dacReadCounter = 0;
                        videocard.colourRegister.dacReadAddress++;
                    }
                }
                else
                {   // DAC read state not enabled, return ones
                    returnValue = 0x3F;
                }
                return (returnValue);

            case 0x3CC: // Miscellaneous Output
                return ((byte) (((videocard.miscOutputRegister.ioAddressSelect & 0x01) << 0) | 
                                ((videocard.miscOutputRegister.ramEnable & 0x01) << 1) | 
                                ((videocard.miscOutputRegister.clockSelect & 0x03) << 2) | 
                                ((videocard.miscOutputRegister.lowHighPage & 0x01) << 5) | 
                                ((videocard.miscOutputRegister.horizontalSyncPol & 0x01) << 6) | 
                                ((videocard.miscOutputRegister.verticalSyncPol & 0x01) << 7)));

            case 0x3CD: // GDC segment select ???
                logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Port [0x3CD] read; unknown register, returned 0x00");
                return 0x00;

            case 0x3CE: // Graphics Controller index
                return (byte) (videocard.graphicsController.index);

            case 0x3CF: // Graphics Controller Registers 0-8, based on index
                switch (videocard.graphicsController.index)
                {
                    case 0: // Set/Reset
                        return (videocard.graphicsController.setReset);

                    case 1: // Enable Set/Reset
                        return (videocard.graphicsController.enableSetReset);

                    case 2: // Colour Compare
                        return (videocard.graphicsController.colourCompare);

                    case 3: // Data Rotate
                        return ((byte) (((videocard.graphicsController.dataOperation & 0x03) << 3) |
                                        ((videocard.graphicsController.dataRotate & 0x07) << 0)));

                    case 4: // Read Map Select
                        return (videocard.graphicsController.readMapSelect);

                    case 5: // Graphics Mode
                        returnValue = (byte) (((videocard.graphicsController.shift256Reg & 0x03) << 5) |
                                              ((videocard.graphicsController.hostOddEvenEnable & 0x01) << 4) |
                                              ((videocard.graphicsController.readMode & 0x01) << 3) |
                                              ((videocard.graphicsController.writeMode & 0x03) << 0));

                        if (videocard.graphicsController.hostOddEvenEnable != 0 || videocard.graphicsController.shift256Reg != 0)
                            logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " io read 0x3cf: reg 05 = " + returnValue);
                        return (returnValue);

                    case 6: // Miscellaneous Graphics
                        return ((byte) (((videocard.graphicsController.memoryMapSelect & 0x03) << 2) |
                                        ((videocard.graphicsController.hostOddEvenEnable & 0x01) << 1) |
                                        ((videocard.graphicsController.alphaNumDisable & 0x01) << 0)));
                        
                    case 7: // Colour Don't Care
                        return (videocard.graphicsController.colourDontCare);

                    case 8: // Bit Mask
                        return (videocard.graphicsController.bitMask);

                    default:
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " Port [0x3CF] reads unknown register 0x" + Integer.toHexString(videocard.graphicsController.index).toUpperCase());
                        return (0);
                }

            case 0x3D4: // CRTC Index Register
                return (videocard.crtControllerRegister.index);

            case 0x3B5: // CRTC Registers (monochrome emulation modes)
            case 0x3D5: // CRTC Registers (colour emulation modes)
                if (videocard.crtControllerRegister.index > 0x18)
                {
                    logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Port [0x" + Integer.toHexString(portAddress).toUpperCase() +"] reads unknown register 0x" + Integer.toHexString(videocard.crtControllerRegister.index).toUpperCase());
                    return (0);
                }
                return videocard.crtControllerRegister.regArray[videocard.crtControllerRegister.index];

            case 0x3B4: // CRTC Index Register (monochrome emulation modes)
                // TODO: return crt index register here, same as 0x3D4???
            case 0x3CB: // GDC segment select register 2 ???
            default:
                logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Port [0x" + Integer.toHexString(portAddress).toUpperCase() +"] read; unknown register, returned 0x00");
                return 0;
        }

    }

    /**
     * OUT instruction to video adapter<BR>
     * 
     * @param portAddress the target port<BR>
     * @param data  Value written to the selected port
     */
    public void setIOPortByte(int portAddress, byte data)
    {
//    	System.out.println("setIOPortByte : "+ portAddress +" : "+ data);
//    	System.out.printf("setIOPortByte : %x : %x\n", portAddress, data);
        boolean needUpdate = false; // Determine if a screen refresh is needed 

        // Check if correct ports are addressed while in monochrome / colour mode; if not, ignore OUT
        if ((videocard.miscOutputRegister.ioAddressSelect != 0) && (portAddress >= 0x3B0) && (portAddress <= 0x03BF))
            return;
        if ((videocard.miscOutputRegister.ioAddressSelect == 0) && (portAddress >= 0x03D0) && (portAddress <= 0x03DF))
            return;

        switch (portAddress)
        {
            case 0x3BA:    // Ext. reg: Feature Control Register (Monochrome)
                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + " I/O write port 0x3BA (Feature Control Register, monochrome): reserved");
                break;

            case 0x3C0:    // Attribute controller: Address register
                // Determine whether in address/data mode
                if (videocard.attributeController.dataAddressFlipFlop)
                {
                    // Data mode
                    switch (videocard.attributeController.index)
                    {
                        case 0x00:  // Internal Palette Index
                        case 0x01:
                        case 0x02:
                        case 0x03:
                        case 0x04:
                        case 0x05:
                        case 0x06:
                        case 0x07:
                        case 0x08:
                        case 0x09:
                        case 0x0A:
                        case 0x0B:
                        case 0x0C:
                        case 0x0D:
                        case 0x0E:
                        case 0x0F:
                            if (data != videocard.attributeController.paletteRegister[videocard.attributeController.index])
                            {
                                videocard.attributeController.paletteRegister[videocard.attributeController.index] = data;
                                needUpdate = true;
                            }
                            break;
                            
                        case 0x10:  // Mode control register
                            // Store previous values for check
                            byte oldLineGraphics = videocard.attributeController.modeControlReg.lineGraphicsEnable;
                            byte oldPaletteBitsSelect = videocard.attributeController.modeControlReg.paletteBitsSelect;
                            
                            videocard.attributeController.modeControlReg.graphicsEnable = (byte) ((data >> 0) & 0x01);
                            videocard.attributeController.modeControlReg.monoColourEmu = (byte) ((data >> 1) & 0x01);
                            videocard.attributeController.modeControlReg.lineGraphicsEnable = (byte) ((data >> 2) & 0x01);
                            videocard.attributeController.modeControlReg.blinkIntensity = (byte) ((data >> 3) & 0x01);
                            videocard.attributeController.modeControlReg.pixelPanningMode = (byte) ((data >> 5) & 0x01);
                            videocard.attributeController.modeControlReg.colour8Bit = (byte) ((data >> 6) & 0x01);
                            videocard.attributeController.modeControlReg.paletteBitsSelect = (byte) ((data >> 7) & 0x01);
                            
                            // Check if updates are necessary
                            if (videocard.attributeController.modeControlReg.lineGraphicsEnable != oldLineGraphics)
                            {
                                screen.updateCodePage(0x20000 + videocard.sequencer.charMapAddress);
                                videocard.vgaMemReqUpdate = true;
                            }
                            if (videocard.attributeController.modeControlReg.paletteBitsSelect != oldPaletteBitsSelect)
                            {
                                needUpdate = true;
                            }
                            logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C0: Mode control: " + data);
                            break;
                            
                        case 0x11:  // Overscan Colour Register
                            videocard.attributeController.overscanColour = (byte) (data & 0x3f);
                            logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C0: Overscan colour = " + data);
                            break;
                            
                        case 0x12:  // Colour Plane Enable Register
                            videocard.attributeController.colourPlaneEnable = (byte) (data & 0x0f);
                            needUpdate = true;
                            logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C0: Colour plane enable = " + data);
                            break;
                            
                        case 0x13:  // Horizontal Pixel Panning Register
                            videocard.attributeController.horizPixelPanning = (byte) (data & 0x0f);
                            needUpdate = true;
                            logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C0: Horiz. pixel panning = " + data);
                            break;
                            
                        case 0x14:  // Colour Select Register
                            videocard.attributeController.colourSelect = (byte) (data & 0x0f);
                            needUpdate = true;
                            logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C0: Colour select = " + videocard.attributeController.colourSelect);
                            break;
                            
                        default:
                            logger.log(Level.WARNING,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C0: Data mode (unknown register) " + videocard.attributeController.index);
                    } 
                    
                 
                }
                else
                {
                    // Address mode
                    int oldPaletteAddressSource = videocard.attributeController.paletteAddressSource;

                    videocard.attributeController.paletteAddressSource = (byte) ((data >> 5) & 0x01);
                    logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C0: address mode = " + videocard.attributeController.paletteAddressSource);

                    if (videocard.attributeController.paletteAddressSource == 0)
                        screen.clearScreen();
                    else if (!(oldPaletteAddressSource != 0))
                    {
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "found enable transition");
                        needUpdate = true;
                    }
                    
                    data &= 0x1F;   // Attribute Address bits
                    
                    videocard.attributeController.index = data;
                    switch (data)
                    {
                        case 0x00:
                        case 0x01:
                        case 0x02:
                        case 0x03:
                        case 0x04:
                        case 0x05:
                        case 0x06:
                        case 0x07:
                        case 0x08:
                        case 0x09:
                        case 0x0A:
                        case 0x0B:
                        case 0x0C:
                        case 0x0D:
                        case 0x0E:
                        case 0x0F:
                            break;

                        default:
                            logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C0: Address mode reg = " + data);
                    }
                }
                
                // Flip the flip-flop
                videocard.attributeController.dataAddressFlipFlop = !videocard.attributeController.dataAddressFlipFlop;
                break;

            case 0x3C2: // Miscellaneous Output Register
                videocard.miscOutputRegister.ioAddressSelect = (byte) ((data >> 0) & 0x01);
                videocard.miscOutputRegister.ramEnable = (byte) ((data >> 1) & 0x01);
                videocard.miscOutputRegister.clockSelect = (byte) ((data >> 2) & 0x03);
                videocard.miscOutputRegister.lowHighPage = (byte) ((data >> 5) & 0x01);
                videocard.miscOutputRegister.horizontalSyncPol = (byte) ((data >> 6) & 0x01);
                videocard.miscOutputRegister.verticalSyncPol = (byte) ((data >> 7) & 0x01);

                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + " I/O write port 0x3C2:");
                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  I/O Address select  = " + videocard.miscOutputRegister.ioAddressSelect);
                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Ram Enable          = " + videocard.miscOutputRegister.ramEnable);
                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Clock Select        = " + videocard.miscOutputRegister.clockSelect);
                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Low/High Page       = " + videocard.miscOutputRegister.lowHighPage);
                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Horiz Sync Polarity = " + videocard.miscOutputRegister.horizontalSyncPol);
                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Vert Sync Polarity  = " + videocard.miscOutputRegister.verticalSyncPol);
                break;

            case 0x3C3: // Video Subsystem Enable; currently only uses bit 0 to check if enabled/disabled
                videocard.vgaEnabled = (data & 0x01) == 1 ? true : false;
                logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " set I/O port 0x3C3: VGA Enabled = " + videocard.vgaEnabled);
                break;

            case 0x3C4: // Sequencer Index Register
                if (data > 4)
                {
                    logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " I/O write port 0x3C4: index > 4");
                }
                videocard.sequencer.index = data;
                break;

            case 0x3C5: // Sequencer Data Registers
                // Determine register to write to
                switch (videocard.sequencer.index)
                {
                    case 0: // Reset register
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + " I/O write 0x3C5: Sequencer reset:  " + data);
                        if ((videocard.sequencer.aSynchReset != 0) && ((data & 0x01) == 0))
                        {
                            videocard.sequencer.characterMapSelect = 0;
                            videocard.sequencer.charMapAddress = 0;
                            screen.updateCodePage(0x20000 + videocard.sequencer.charMapAddress);
                            videocard.vgaMemReqUpdate = true;
                        }
                        videocard.sequencer.aSynchReset = (byte) ((data >> 0) & 0x01);
                        videocard.sequencer.synchReset = (byte) ((data >> 1) & 0x01);
                        break;
                    
                    case 1: // Clocking mode register
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C5 (clocking mode): " + data);
                        videocard.sequencer.clockingMode = (byte) (data & 0x3D);
                        videocard.sequencer.dotClockRate = ((data & 0x08) > 0) ? (byte) 1 : 0;
                        break;
                    
                    case 2: // Map Mask register
                        videocard.sequencer.mapMask = (byte) (data & 0x0F);
                        for (int i = 0; i < 4; i++)
                            videocard.sequencer.mapMaskArray[i] = (byte) ((data >> i) & 0x01);
                        break;
                        
                    case 3: // Character Map select register
                        videocard.sequencer.characterMapSelect = (byte) (data & 0x3F);
                        
                        byte charSetA = (byte) (data & 0x13);   // Text mode font used when attribute byte bit 3 == 1
                        if (charSetA > 3)
                            charSetA = (byte) ((charSetA & 3) + 4);
                        
                        byte charSetB = (byte) ((data & 0x2C) >> 2);    // Text mode font used when attribute byte bit 3 == 0
                        if (charSetB > 3)
                            charSetB = (byte) ((charSetB & 3) + 4);
                        
                        // Select font from font table
                        // FIXME: Ensure this check is correct
                        if (videocard.crtControllerRegister.regArray[0x09] != 0)
                        {
                            videocard.sequencer.charMapAddress = SequencerRegister.charMapOffset[charSetA];
                            screen.updateCodePage(0x20000 + videocard.sequencer.charMapAddress);
                            videocard.vgaMemReqUpdate = true;
                        }
                        
                        // Different fonts not supported at this time
                        if (charSetB != charSetA)
                            logger.log(Level.WARNING,  "[" + MODULE_TYPE + "]" + "Character map select: map #2 in block " + charSetB + " unused");
                        break;
                    
                    case 4: // Memory Mode register
                        videocard.sequencer.extendedMemory = (byte) ((data >> 1) & 0x01);
                        videocard.sequencer.oddEvenDisable = (byte) ((data >> 2) & 0x01);
                        videocard.sequencer.chainFourEnable = (byte) ((data >> 3) & 0x01);

                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + " I/O write port 0x3C5 (memory mode):");
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Extended Memory  = " + videocard.sequencer.extendedMemory);
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Odd/Even disable = " + videocard.sequencer.oddEvenDisable);
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Chain 4 enable   = " + videocard.sequencer.chainFourEnable);
                        break;
                    
                    default:
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3C5: index " + videocard.sequencer.index + " unhandled");
                }
                break;

            case 0x3C6: // Pixel mask
                videocard.colourRegister.pixelMask = data;
                if (videocard.colourRegister.pixelMask != (byte) 0xFF)
                    logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " I/O write port 0x3C6: Pixel mask= " + data + " != 0xFF");
                break;

            case 0x3C7: // DAC Address Read Mode register
                videocard.colourRegister.dacReadAddress = data;
                videocard.colourRegister.dacReadCounter = 0;
                videocard.colourRegister.dacState = 0x03;
                break;

            case 0x3C8: // DAC Address Write Mode register
                videocard.colourRegister.dacWriteAddress = data;
                videocard.colourRegister.dacWriteCounter = 0;
                videocard.colourRegister.dacState = 0x00;
                break;

            case 0x3C9: // DAC Data Register
                // Determine sub-colour to be written 
                switch (videocard.colourRegister.dacWriteCounter)
                {
                    case 0:
                        videocard.pixels[(((int) videocard.colourRegister.dacWriteAddress) & 0xFF)].red = data;
                        break;
                    case 1:
                        videocard.pixels[(((int) videocard.colourRegister.dacWriteAddress) & 0xFF)].green = data;
                        break;
                    case 2:
                        videocard.pixels[(((int) videocard.colourRegister.dacWriteAddress) & 0xFF)] .blue = data;

                        needUpdate |= screen.setPaletteColour(videocard.colourRegister.dacWriteAddress, (videocard.pixels[(((int) videocard.colourRegister.dacWriteAddress) & 0xFF)].red) << 2,
                                (videocard.pixels[(((int) videocard.colourRegister.dacWriteAddress) & 0xFF)].green) << 2,
                                (videocard.pixels[(((int) videocard.colourRegister.dacWriteAddress) & 0xFF)].blue) << 2);
                        break;
                }

                videocard.colourRegister.dacWriteCounter++;

                // Reset counter when 3 values are written and automatically update the address
                if (videocard.colourRegister.dacWriteCounter >= 3)
                {
                    videocard.colourRegister.dacWriteCounter = 0;
                    videocard.colourRegister.dacWriteAddress++;
                }
                break;

            case 0x3CA: // Feature Control Register
                // Read only (write at 0x3BA mono, 0x3DA colour)
                break;

            case 0x3CC: // Miscellaneous Output Register
                // Read only (write at 0x3C2
                break;

            case 0x3CD: // Unknown
                logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " I/O write to unknown port 0x3CD = " + data);
                break;

            case 0x3CE: // Graphics Controller Address Register
                // Only 9 register accessible
                if (data > 0x08)
                    logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " /O write port 0x3CE: index > 8");
                videocard.graphicsController.index = data;
                break;

            case 0x3CF: // Graphics Controller Data Register
                switch (videocard.graphicsController.index)
                {
                    case 0: // Set/Reset
                        videocard.graphicsController.setReset = (byte) (data & 0x0F);
                        break;
                        
                    case 1: // Enable Set/Reset
                        videocard.graphicsController.enableSetReset = (byte) (data & 0x0F);
                        break;
                        
                    case 2: // Colour Compare
                        videocard.graphicsController.colourCompare = (byte) (data & 0x0F);
                        break;
                        
                    case 3: // Data Rotate
                        videocard.graphicsController.dataRotate = (byte) (data & 0x07);
                        videocard.graphicsController.dataOperation = (byte) ((data >> 3) & 0x03);
                        break;
                        
                    case 4: // Read Map Select
                        videocard.graphicsController.readMapSelect = (byte) (data & 0x03);
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3CF (Read Map Select): " + data);
                        break;
                        
                    case 5: // Graphics Mode
                        videocard.graphicsController.writeMode = (byte) (data & 0x03);
                        videocard.graphicsController.readMode = (byte) ((data >> 3) & 0x01);
                        videocard.graphicsController.hostOddEvenEnable = (byte) ((data >> 4) & 0x01);
                        videocard.graphicsController.shift256Reg = (byte) ((data >> 5) & 0x03);

                        if (videocard.graphicsController.hostOddEvenEnable != 0)
                            logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3CF (graphics mode): value = " + data);
                        if (videocard.graphicsController.shift256Reg != 0)
                            logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "I/O write port 0x3CF (graphics mode): value = " + data);
                        break;
                        
                    case 6: // Miscellaneous
                        // Store old values for check later
                        byte oldAlphaNumDisable = videocard.graphicsController.alphaNumDisable;
                        byte oldMemoryMapSelect = videocard.graphicsController.memoryMapSelect;

                        videocard.graphicsController.alphaNumDisable = (byte) (data & 0x01);
                        videocard.graphicsController.chainOddEvenEnable = (byte) ((data >> 1) & 0x01);
                        videocard.graphicsController.memoryMapSelect = (byte) ((data >> 2) & 0x03);

                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + " I/O write port 0x3CF (Miscellaneous): " + data);
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Alpha Num Disable: " + videocard.graphicsController.alphaNumDisable);
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Memory map select: " + videocard.graphicsController.memoryMapSelect);
                        logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + "  Odd/Even enable  : " + videocard.graphicsController.hostOddEvenEnable);
                        

                        if (oldMemoryMapSelect != videocard.graphicsController.memoryMapSelect)
                            needUpdate = true;
                        if (oldAlphaNumDisable != videocard.graphicsController.alphaNumDisable)
                        {
                            needUpdate = true;
                            oldScreenHeight = 0;
                        }
                        break;
                        
                    case 7: // Colour Don't Care
                        videocard.graphicsController.colourDontCare = (byte) (data & 0x0F);
                        break;
                        
                    case 8: // Bit Mask
                        videocard.graphicsController.bitMask = data;
                        break;
                        
                    default:
                        // Unknown index addressed
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " I/O write port 0x3CF: index " + videocard.graphicsController.index + " unhandled");
                }
                break;

            case 0x3B4: // CRT Controller Address Register (monochrome)
            case 0x3D4: // CRT Controller Address Register (colour)
                // Set index to be accessed in CRTC Data Register cycle
                videocard.crtControllerRegister.index = (byte) (data & 0x7F);
                if (videocard.crtControllerRegister.index > 0x18)
                    logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " I/O write port 0x3(B|D)4: invalid CRTC register " + videocard.crtControllerRegister.index + " selected");
                break;

            case 0x3B5: // CRTC Data Register (monochrome)
            case 0x3D5: // CRTC Data Register (colour)
                if (videocard.crtControllerRegister.index > 0x18)
                {
                    logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + "  I/O write port 0x3(B|D)5: invalid CRTC Register (" + videocard.crtControllerRegister.index + "); ignored");
                    return;
                }
                // Check if writing is allowed for registers 0x00 - 0x07
                if ((videocard.crtControllerRegister.protectEnable) && (videocard.crtControllerRegister.index < 0x08))
                {
                    // Only write exception in protectEnable is lineCompare of Overflow register (0x07)
                    if (videocard.crtControllerRegister.index == 0x07)
                    {
                        // Reset variables before ORing
                        videocard.crtControllerRegister.regArray[videocard.crtControllerRegister.index] &= ~0x10;
                        videocard.lineCompare &= 0x2ff;
                        
                        // Bit 4 specifies lineCompare bit 8 
                        videocard.crtControllerRegister.regArray[videocard.crtControllerRegister.index] |= (data & 0x10);
                        if ((videocard.crtControllerRegister.regArray[0x07] & 0x10) != 0)
                            videocard.lineCompare |= 0x100;
                        needUpdate = true;
                        break;
                    }
                    else
                    {
                        return;
                    }
                }
                if (data != videocard.crtControllerRegister.regArray[videocard.crtControllerRegister.index])
                {
                    videocard.crtControllerRegister.regArray[videocard.crtControllerRegister.index] = data;
                    switch (videocard.crtControllerRegister.index)
                    {
                        case 0x07:
                            // Overflow register; specifies bit 8, 9 for several fields
                            
                            // Reset variables before ORing
                            videocard.verticalDisplayEnd &= 0xFF;
                            videocard.lineCompare &= 0x2FF;

                            // Bit 1 specifies verticalDisplayEnd bit 8  
                            if ((videocard.crtControllerRegister.regArray[0x07] & 0x02) != 0)
                                videocard.verticalDisplayEnd |= 0x100;
                            // Bit 6 specifies verticalDisplayEnd bit 9
                            if ((videocard.crtControllerRegister.regArray[0x07] & 0x40) != 0)
                                videocard.verticalDisplayEnd |= 0x200;
                            // Bit 4 specifies lineCompare bit 8
                            if ((videocard.crtControllerRegister.regArray[0x07] & 0x10) != 0)
                                videocard.lineCompare |= 0x100;
                            needUpdate = true;
                            break;
                            
                        case 0x08:
                            // Preset row scan; bits 5-6 allow 15/31/35 pixel shift without change in start address, 
                            // bits 0-4 specify number of scanlines to scroll up (more precise than start address)
                            needUpdate = true;
                            break;
                            
                        case 0x09:
                            // Maximum scan line; for text mode, value should be char. height - 1, 
                            // for graphic mode a non-zero value causes repeat of scanline by MSL+1 

                            // Bit 7 sets scan doubling:
                            // FIXME: Why is this ANDed with 0x9F if bit 7 is required?
                            videocard.crtControllerRegister.scanDoubling = ((data & 0x9F) > 0) ? (byte) 1 : 0;

                            // Reset variables before ORing
                            videocard.lineCompare &= 0x1FF;

                            // Bit 6 specifies bit 9 of line_compare
                            if ((videocard.crtControllerRegister.regArray[0x09] & 0x40) != 0)
                                videocard.lineCompare |= 0x200;
                            needUpdate = true;
                            break;
                            
                        case 0x0A:
                        case 0x0B:
                        case 0x0E:
                        case 0x0F:
                            // Cursor start & end / cursor location; specifies the scanlines 
                            // at which the cursor should start and end, and the location of the cursor
                            videocard.vgaMemReqUpdate = true;
                            break;
                            
                        case 0x0C:
                        case 0x0D:
                            // Start address; specifies the display memory address of the upper left pixel/character
                            if (videocard.graphicsController.alphaNumDisable != 0)
                            {
                                needUpdate = true;
                            }
                            else
                            {
                                videocard.vgaMemReqUpdate = true;
                            }
                            break;
                            
                        case 0x11:
                            // Change vertical retrace end
                            videocard.crtControllerRegister.protectEnable = ((videocard.crtControllerRegister.regArray[0x11] & 0x80) > 0) ? true : false;
                            break;
                            
                        case 0x12:
                            // Change vertical display end
                            videocard.verticalDisplayEnd &= 0x300;
                            videocard.verticalDisplayEnd |= (((int) videocard.crtControllerRegister.regArray[0x12]) & 0xFF);
                            break;
                            
                        case 0x13:
                        case 0x14:
                        case 0x17:
                            // Line offset; specifies address difference between consecutive scanlines/character lines 
                            videocard.lineOffset = videocard.crtControllerRegister.regArray[0x13] << 1;
                            if ((videocard.crtControllerRegister.regArray[0x14] & 0x40) != 0)
                            {
                                videocard.lineOffset <<= 2;
                            }
                            else if ((videocard.crtControllerRegister.regArray[0x17] & 0x40) == 0)
                            {
                                videocard.lineOffset <<= 1;
                            }
                            needUpdate = true;
                            break;

                        case 0x18:
                            // Line compare; indicates scan line where horiz. division can occur. No division when set to 0x3FF
                            videocard.lineCompare &= 0x300;
                            videocard.lineCompare |= (((short) videocard.crtControllerRegister.regArray[0x18]) & 0xFF); // Cast from byte to short
                            needUpdate = true;
                            break;
                    }

                }
                break;

            case 0x3Da: // Feature Control (colour)
                logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "]" + " I/O write port 0x3DA (Feature Control Register, colour): reserved");
                break;

            case 0x03C1: // Attribute Data Read Register
                // Read only
                break;
                
            default:
                logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " unsupported I/O write to port " + portAddress + ", data =" + data);

        }

        if (needUpdate)
        {
            // Mark all video as updated so the changes will go through
            setAreaForUpdate(0, 0, oldScreenWidth, oldScreenHeight);
        }
        return;
    }

    public byte[] getIOPortWord(int portAddress)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setIOPortWord(int portAddress, byte[] dataWord)
    {
        // Support IO words by redirecting to byte handler
        setIOPortByte(portAddress, (byte) (dataWord[1] & 0xff));
        setIOPortByte(portAddress + 1, (byte) (dataWord[0] & 0xff));
        return;
    }

    public byte[] getIOPortDoubleWord(int portAddress)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setIOPortDoubleWord(int portAddress, byte[] dataDoubleWord)
    {
        // TODO Auto-generated method stub
        return;
    }


    // ******************************************************************************
    // ModuleVideo methods

    /**
     * Returns a pointer to the whole video buffer
     * 
     * @return byte[] containing the video buffer
     */
    public byte[] getVideoBuffer()
    {
        return this.videocard.vgaMemory;
    }

    /**
     * Returns a byte from video buffer at position index
     * 
     * @param int index
     * 
     * @return byte from video buffer
     */
    public byte getVideoBufferByte(int index)
    {
        return this.videocard.vgaMemory[index];
    }

    /**
     * Stores a byte in video buffer at position index
     * 
     * @param int index
     * @param byte data
     */
    public void setVideoBufferByte(int index, byte data)
    {
        this.videocard.vgaMemory[index] = data;
    }

    /**
     * Returns all characters (as Unicode) that are currently in buffer
     * 
     * @return String containing all characters in the buffer or null when no characters exist
     */
	public String getVideoBufferCharacters()
	{
		int maxRows, maxCols, index;
		StringBuffer text;
		
		// Retrieve screen dimension in text mode
		maxRows = screen.getScreenRows();
		maxCols = screen.getScreenColumns();
		
		// Create initial stringbuffer
		text = new StringBuffer(maxRows * maxCols);
		
		// Convert each character in textSnapshot into Unicode character
		for (int row = 0; row < maxRows; row++)
		{
			for (int col = 0; col < maxCols; col++)
			{
				index = videocard.textSnapshot[(row * maxCols * 2) + (col * 2)] & 0xFF;
				if (index < 255)
				{
					text.append(textTranslation.asciiToUnicode[index]);
				}
			}
			
			// Add a newline at the end of row
			text.append("\n");
		}
		
		return text.toString();
	}

    /**
     * Returns a byte from text snapshot at position index
     * 
     * @param int index
     * 
     * @return byte from textsnapshot
     */
	public byte getTextSnapshot(int index)
    {
        return this.videocard.textSnapshot[index];
    }

    /**
     * Stores a byte in text snapshot at position index
     * 
     * @param int index
     * @param byte data
     */
    public void setTextSnapshot(int index, byte data)
    {
        this.videocard.textSnapshot[index] = data;
    }
    
    /**
     *  Translate the text attribute/graphic colour input value into the CRT display colour
     */
    public byte getAttributePaletteRegister(int index)
    {
      return videocard.attributeController.paletteRegister[index];
    }

    
    // ******************************************************************************
    // Custom methods

    /**
     * Determine the screen size in pixels
     * 
     * @return integer array containing [height, width] of screen in pixels
     */
    public int[] determineScreenSize()
    {
        int heightInPixels, widthInPixels;
        int horizontal, vertical;

        // Determine initial values from CRTC registers (ensure dimensions are positive by casting byte to int)
        horizontal = ((((int) videocard.crtControllerRegister.regArray[1]) & 0xFF) + 1) * 8;
        vertical = ((((int) videocard.crtControllerRegister.regArray[18]) & 0xFF) | 
                (((((int) videocard.crtControllerRegister.regArray[7]) & 0xFF) & 0x02) << 7) | (((((int) videocard.crtControllerRegister.regArray[7]) & 0xFF) & 0x40) << 3)) + 1;

        if (videocard.graphicsController.shift256Reg == 0)
        {
            widthInPixels = 640;
            heightInPixels = 480;

            if (videocard.crtControllerRegister.regArray[0x06] == (byte) 0xBF)
            {
                if (videocard.crtControllerRegister.regArray[0x17] == (byte) 0xA3 && videocard.crtControllerRegister.regArray[0x14] == (byte) 0x40 && videocard.crtControllerRegister.regArray[0x09] == (byte) 0x41)
                {
                    widthInPixels = 320;
                    heightInPixels = 240;
                }
                else
                {
                    if (videocard.sequencer.dotClockRate != 0)
                    {
                        horizontal <<= 1;
                    }
                    widthInPixels = horizontal;
                    heightInPixels = vertical;
                }
            }
            else if ((horizontal >= 640) && (vertical >= 480))
            {
                widthInPixels = horizontal;
                heightInPixels = vertical;
            }
        }
        else if (videocard.graphicsController.shift256Reg == 2)
        {

            if (videocard.sequencer.chainFourEnable != 0)
            {
                widthInPixels = horizontal;
                heightInPixels = vertical;
            }
            else
            {
                widthInPixels = horizontal;
                heightInPixels = vertical;
            }
        }
        else
        {
            if (videocard.sequencer.dotClockRate != 0)
                horizontal <<= 1;
            widthInPixels = horizontal;
            heightInPixels = vertical;
        }
        return new int[] {heightInPixels, widthInPixels};
    }


    /**
     * VGA memory Read Modes 0 and 1 functionality
     */
    public byte readMode(int address)
    {
        int i;  // Counter
        int offset; // Offset in the memory bank
        int[] plane = new int[4];   // Memory address of plane 

        // Determien range of host memory used, and any offset 
        switch (videocard.graphicsController.memoryMapSelect)
        {
            case 1: // 0xA0000 .. 0xAFFFF
                if (address > 0xAFFFF)
                {
                    return (byte) 0xFF;
                }
                offset = address - 0xA0000;
                break;
                
            case 2: // 0xB0000 .. 0xB7FFF
                if ((address < 0xB0000) || (address > 0xB7FFF))
                {
                    return (byte) 0xFF;
                }
                return videocard.vgaMemory[address - 0xB0000];
                
            case 3: // 0xB8000 .. 0xBFFFF
                if (address < 0xB8000)
                {
                    return (byte) 0xFF;
                }
                return videocard.vgaMemory[address - 0xB8000];
                
            default: // 0xA0000 .. 0xBFFFF
                return videocard.vgaMemory[address - 0xA0000];
        }
        
        // Address is between 0xA0000 and 0xAFFFF
        if (videocard.sequencer.chainFourEnable != 0)
        {

            // Mode 13h: 320 x 200 256 colour mode: chained pixel representation
            return videocard.vgaMemory[(offset & ~0x03) + (offset % 4) * 65536];
        }

        for (i = 0; i < 4; i++)
        {
            plane[i] = i << 16;
        }

        // Address is between 0xA0000 and 0xAFFFF
        switch (videocard.graphicsController.readMode)
        {
            case 0: // Read mode 0 - read value of one plane in vga memory
                // All four latches are read with plane data, even though only one plane/latch is returned
                for (i = 0; i < 4; i++)
                {
                    videocard.graphicsController.latch[i] = videocard.vgaMemory[plane[i] + offset];
                }
                return (videocard.graphicsController.latch[videocard.graphicsController.readMapSelect]);

            case 1: // Read mode 1 ('colour compare mode') - determine pixel colour of all four planes
            {
                byte colourCompare, colourDontCare;
                byte[] latch = new byte[4];
                byte returnValue;

                colourCompare = (byte) (videocard.graphicsController.colourCompare & 0x0F);
                colourDontCare = (byte) (videocard.graphicsController.colourDontCare & 0x0F);
                
                for (i = 0; i < 4; i++)
                {
                    latch[i] = videocard.graphicsController.latch[i] = videocard.vgaMemory[plane[i] + offset];
                    latch[i] ^= GraphicsController.colourCompareTable[colourCompare][i];
                    latch[i] &= GraphicsController.colourCompareTable[colourDontCare][i];
                }

                returnValue = (byte) ~(latch[0] | latch[1] | latch[2] | latch[3]);

                return returnValue;
            }
            
            default:
                return 0;
        }
    }

    /**
     * VGA memory Write Modes 0, 1, 2 and 3 functionality
     */
    public void writeMode(int address, byte value)
    {
        int offset;
        byte newValue[] = new byte[4];
        int startAddress;
        int plane0, plane1, plane2, plane3;

        switch (videocard.graphicsController.memoryMapSelect)
        {
            case 1: // 0xA0000 .. 0xAFFFF
                if (address > 0xAFFFF)
                    return;
                offset = address - 0xA0000;
                break;
                
            case 2: // 0xB0000 .. 0xB7FFF
                if ((address < 0xB0000) || (address > 0xB7FFF))
                    return;
                offset = address - 0xB0000;
                break;
                
            case 3: // 0xB8000 .. 0xBFFFF
                if (address < 0xB8000)
                    return;
                offset = address - 0xB8000;
                break;
                
            default: // 0xA0000 .. 0xBFFFF
                offset = address - 0xA0000;
        }

        startAddress = (videocard.crtControllerRegister.regArray[0x0C] << 8) | videocard.crtControllerRegister.regArray[0x0D];

        if (videocard.graphicsController.alphaNumDisable != 0)
        {
            if (videocard.graphicsController.memoryMapSelect == 3)
            { 
                // 0xB8000 .. 0xBFFFF
                int x_tileno, x_tileno2, y_tileno;
                /* CGA 320x200x4 / 640x200x2 start */
                videocard.vgaMemory[offset] = value;
                offset -= startAddress;
                if (offset >= 0x2000)
                {
                    y_tileno = offset - 0x2000;
                    y_tileno /= (320 / 4);
                    y_tileno <<= 1; // 2 * y_tileno;
                    y_tileno++;
                    x_tileno = (offset - 0x2000) % (320 / 4);
                    x_tileno <<= 2; // *= 4;
                }
                else
                {
                    y_tileno = offset / (320 / 4);
                    y_tileno <<= 1; // 2 * y_tileno;
                    x_tileno = offset % (320 / 4);
                    x_tileno <<= 2; // *=4;
                }
                x_tileno2 = x_tileno;
                if (videocard.graphicsController.shift256Reg == 0)
                {
                    x_tileno *= 2;
                    x_tileno2 += 7;
                }
                else
                {
                    x_tileno2 += 3;
                }
                if (videocard.sequencer.dotClockRate != 0)
                {
                    x_tileno /= (VideoCard.X_TILESIZE / 2);
                    x_tileno2 /= (VideoCard.X_TILESIZE / 2);
                }
                else
                {
                    x_tileno /= VideoCard.X_TILESIZE;
                    x_tileno2 /= VideoCard.X_TILESIZE;
                }
                if (videocard.crtControllerRegister.scanDoubling != 0)
                {
                    y_tileno /= (VideoCard.Y_TILESIZE / 2);
                }
                else
                {
                    y_tileno /= VideoCard.Y_TILESIZE;
                }
                videocard.vgaMemReqUpdate = true;
               videocard.setTileUpdate(x_tileno, y_tileno, true);
                if (x_tileno2 != x_tileno)
                {
                   videocard.setTileUpdate(x_tileno2, y_tileno, true);
                }
                return;
                /* CGA 320x200x4 / 640x200x2 end */
            }
            else if (videocard.graphicsController.memoryMapSelect != 1)
            {

                logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " mem_write: graphics: mapping = " + videocard.graphicsController.memoryMapSelect);
                return;
            }

            if (videocard.sequencer.chainFourEnable != 0)
            {
                int x_tileno, y_tileno;

                // 320 x 200 256 colour mode: chained pixel representation
                videocard.vgaMemory[(offset & ~0x03) + (offset % 4) * 65536] = value;
                if (videocard.lineOffset > 0)
                {
                    offset -= startAddress;
                    x_tileno = (offset % videocard.lineOffset) / (VideoCard.X_TILESIZE / 2);
                    if (videocard.crtControllerRegister.scanDoubling != 0)
                    {
                        y_tileno = (offset / videocard.lineOffset) / (VideoCard.Y_TILESIZE / 2);
                    }
                    else
                    {
                        y_tileno = (offset / videocard.lineOffset) / VideoCard.Y_TILESIZE;
                    }
                    videocard.vgaMemReqUpdate = true;
                   videocard.setTileUpdate(x_tileno, y_tileno, true);
                }
                return;
            }
        }

        /* addr between 0xA0000 and 0xAFFFF */

        // Set offsets in vga.vgaMemory array for planes
        plane0 = 0 << 16;
        plane1 = 1 << 16;
        plane2 = 2 << 16;
        plane3 = 3 << 16;

        int i;
        switch (videocard.graphicsController.writeMode)
        {
            case 0: /* write mode 0 */
            {
                final byte bitmask = videocard.graphicsController.bitMask;
                final byte set_reset = videocard.graphicsController.setReset;
                final byte enable_set_reset = videocard.graphicsController.enableSetReset;
                /* perform rotate on CPU data in case its needed */
                if (videocard.graphicsController.dataRotate != 0)
                {
                    value = (byte) ((value >> videocard.graphicsController.dataRotate) | (value << (8 - videocard.graphicsController.dataRotate)));
                }
                newValue[0] = (byte) (videocard.graphicsController.latch[0] & ~bitmask);
                newValue[1] = (byte) (videocard.graphicsController.latch[1] & ~bitmask);
                newValue[2] = (byte) (videocard.graphicsController.latch[2] & ~bitmask);
                newValue[3] = (byte) (videocard.graphicsController.latch[3] & ~bitmask);
                switch (videocard.graphicsController.dataOperation)
                {
                    case 0: // replace
                        newValue[0] |= (((enable_set_reset & 1) != 0) ? (((set_reset & 1) != 0) ? bitmask : 0) : (value & bitmask));
                        newValue[1] |= (((enable_set_reset & 2) != 0) ? (((set_reset & 2) != 0) ? bitmask : 0) : (value & bitmask));
                        newValue[2] |= (((enable_set_reset & 4) != 0) ? (((set_reset & 4) != 0) ? bitmask : 0) : (value & bitmask));
                        newValue[3] |= (((enable_set_reset & 8) != 0) ? (((set_reset & 8) != 0) ? bitmask : 0) : (value & bitmask));
                        break;
                    case 1: // AND
                        newValue[0] |= (((enable_set_reset & 1) != 0) ? (((set_reset & 1) != 0) ? (videocard.graphicsController.latch[0] & bitmask) : 0)
                                : (value & videocard.graphicsController.latch[0]) & bitmask);
                        newValue[1] |= (((enable_set_reset & 2) != 0) ? (((set_reset & 2) != 0) ? (videocard.graphicsController.latch[1] & bitmask) : 0)
                                : (value & videocard.graphicsController.latch[1]) & bitmask);
                        newValue[2] |= (((enable_set_reset & 4) != 0) ? (((set_reset & 4) != 0) ? (videocard.graphicsController.latch[2] & bitmask) : 0)
                                : (value & videocard.graphicsController.latch[2]) & bitmask);
                        newValue[3] |= (((enable_set_reset & 8) != 0) ? (((set_reset & 8) != 0) ? (videocard.graphicsController.latch[3] & bitmask) : 0)
                                : (value & videocard.graphicsController.latch[3]) & bitmask);
                        break;
                    case 2: // OR
                        newValue[0] |= (((enable_set_reset & 1) != 0) ? (((set_reset & 1) != 0) ? bitmask : (videocard.graphicsController.latch[0] & bitmask))
                                : ((value | videocard.graphicsController.latch[0]) & bitmask));
                        newValue[1] |= (((enable_set_reset & 2) != 0) ? (((set_reset & 2) != 0) ? bitmask : (videocard.graphicsController.latch[1] & bitmask))
                                : ((value | videocard.graphicsController.latch[1]) & bitmask));
                        newValue[2] |= (((enable_set_reset & 4) != 0) ? (((set_reset & 4) != 0) ? bitmask : (videocard.graphicsController.latch[2] & bitmask))
                                : ((value | videocard.graphicsController.latch[2]) & bitmask));
                        newValue[3] |= (((enable_set_reset & 8) != 0) ? (((set_reset & 8) != 0) ? bitmask : (videocard.graphicsController.latch[3] & bitmask))
                                : ((value | videocard.graphicsController.latch[3]) & bitmask));
                        break;
                    case 3: // XOR
                        newValue[0] |= (((enable_set_reset & 1) != 0) ? (((set_reset & 1) != 0) ? (~videocard.graphicsController.latch[0] & bitmask)
                                : (videocard.graphicsController.latch[0] & bitmask)) : (value ^ videocard.graphicsController.latch[0]) & bitmask);
                        newValue[1] |= (((enable_set_reset & 2) != 0) ? (((set_reset & 2) != 0) ? (~videocard.graphicsController.latch[1] & bitmask)
                                : (videocard.graphicsController.latch[1] & bitmask)) : (value ^ videocard.graphicsController.latch[1]) & bitmask);
                        newValue[2] |= (((enable_set_reset & 4) != 0) ? (((set_reset & 4) != 0) ? (~videocard.graphicsController.latch[2] & bitmask)
                                : (videocard.graphicsController.latch[2] & bitmask)) : (value ^ videocard.graphicsController.latch[2]) & bitmask);
                        newValue[3] |= (((enable_set_reset & 8) != 0) ? (((set_reset & 8) != 0) ? (~videocard.graphicsController.latch[3] & bitmask)
                                : (videocard.graphicsController.latch[3] & bitmask)) : (value ^ videocard.graphicsController.latch[3]) & bitmask);
                        break;
                    default:
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " vga_mem_write: write mode 0: op = " + videocard.graphicsController.dataOperation);
                }
            }
                break;

            case 1: /* write mode 1 */
                for (i = 0; i < 4; i++)
                {
                    newValue[i] = videocard.graphicsController.latch[i];
                }
                break;

            case 2: /* write mode 2 */
            {
                final byte bitmask = videocard.graphicsController.bitMask;

                newValue[0] = (byte) (videocard.graphicsController.latch[0] & ~bitmask);
                newValue[1] = (byte) (videocard.graphicsController.latch[1] & ~bitmask);
                newValue[2] = (byte) (videocard.graphicsController.latch[2] & ~bitmask);
                newValue[3] = (byte) (videocard.graphicsController.latch[3] & ~bitmask);
                switch (videocard.graphicsController.dataOperation)
                {
                    case 0: // write
                        newValue[0] |= ((value & 1) != 0) ? bitmask : 0;
                        newValue[1] |= ((value & 2) != 0) ? bitmask : 0;
                        newValue[2] |= ((value & 4) != 0) ? bitmask : 0;
                        newValue[3] |= ((value & 8) != 0) ? bitmask : 0;
                        break;
                    case 1: // AND
                        newValue[0] |= ((value & 1) != 0) ? (videocard.graphicsController.latch[0] & bitmask) : 0;
                        newValue[1] |= ((value & 2) != 0) ? (videocard.graphicsController.latch[1] & bitmask) : 0;
                        newValue[2] |= ((value & 4) != 0) ? (videocard.graphicsController.latch[2] & bitmask) : 0;
                        newValue[3] |= ((value & 8) != 0) ? (videocard.graphicsController.latch[3] & bitmask) : 0;
                        break;
                    case 2: // OR
                        newValue[0] |= ((value & 1) != 0) ? bitmask : (videocard.graphicsController.latch[0] & bitmask);
                        newValue[1] |= ((value & 2) != 0) ? bitmask : (videocard.graphicsController.latch[1] & bitmask);
                        newValue[2] |= ((value & 4) != 0) ? bitmask : (videocard.graphicsController.latch[2] & bitmask);
                        newValue[3] |= ((value & 8) != 0) ? bitmask : (videocard.graphicsController.latch[3] & bitmask);
                        break;
                    case 3: // XOR
                        newValue[0] |= ((value & 1) != 0) ? (~videocard.graphicsController.latch[0] & bitmask) : (videocard.graphicsController.latch[0] & bitmask);
                        newValue[1] |= ((value & 2) != 0) ? (~videocard.graphicsController.latch[1] & bitmask) : (videocard.graphicsController.latch[1] & bitmask);
                        newValue[2] |= ((value & 4) != 0) ? (~videocard.graphicsController.latch[2] & bitmask) : (videocard.graphicsController.latch[2] & bitmask);
                        newValue[3] |= ((value & 8) != 0) ? (~videocard.graphicsController.latch[3] & bitmask) : (videocard.graphicsController.latch[3] & bitmask);
                        break;
                }
            }
                break;

            case 3: /* write mode 3 */
            {
                final byte bitmask = (byte) (videocard.graphicsController.bitMask & value);
                final byte set_reset = videocard.graphicsController.setReset;

                /* perform rotate on CPU data */
                if (videocard.graphicsController.dataRotate != 0)
                {
                    value = (byte) ((value >> videocard.graphicsController.dataRotate) | (value << (8 - videocard.graphicsController.dataRotate)));
                }
                newValue[0] = (byte) (videocard.graphicsController.latch[0] & ~bitmask);
                newValue[1] = (byte) (videocard.graphicsController.latch[1] & ~bitmask);
                newValue[2] = (byte) (videocard.graphicsController.latch[2] & ~bitmask);
                newValue[3] = (byte) (videocard.graphicsController.latch[3] & ~bitmask);

                value &= bitmask;

                switch (videocard.graphicsController.dataOperation)
                {
                    case 0: // write
                        newValue[0] |= ((set_reset & 1) != 0) ? value : 0;
                        newValue[1] |= ((set_reset & 2) != 0) ? value : 0;
                        newValue[2] |= ((set_reset & 4) != 0) ? value : 0;
                        newValue[3] |= ((set_reset & 8) != 0) ? value : 0;
                        break;
                    case 1: // AND
                        newValue[0] |= (((set_reset & 1) != 0) ? value : 0) & videocard.graphicsController.latch[0];
                        newValue[1] |= (((set_reset & 2) != 0) ? value : 0) & videocard.graphicsController.latch[1];
                        newValue[2] |= (((set_reset & 4) != 0) ? value : 0) & videocard.graphicsController.latch[2];
                        newValue[3] |= (((set_reset & 8) != 0) ? value : 0) & videocard.graphicsController.latch[3];
                        break;
                    case 2: // OR
                        newValue[0] |= (((set_reset & 1) != 0) ? value : 0) | videocard.graphicsController.latch[0];
                        newValue[1] |= (((set_reset & 2) != 0) ? value : 0) | videocard.graphicsController.latch[1];
                        newValue[2] |= (((set_reset & 4) != 0) ? value : 0) | videocard.graphicsController.latch[2];
                        newValue[3] |= (((set_reset & 8) != 0) ? value : 0) | videocard.graphicsController.latch[3];
                        break;
                    case 3: // XOR
                        newValue[0] |= (((set_reset & 1) != 0) ? value : 0) ^ videocard.graphicsController.latch[0];
                        newValue[1] |= (((set_reset & 2) != 0) ? value : 0) ^ videocard.graphicsController.latch[1];
                        newValue[2] |= (((set_reset & 4) != 0) ? value : 0) ^ videocard.graphicsController.latch[2];
                        newValue[3] |= (((set_reset & 8) != 0) ? value : 0) ^ videocard.graphicsController.latch[3];
                        break;
                }
            }
                break;

            default:
                logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " vga_mem_write: write mode " + videocard.graphicsController.writeMode + " ?");
        }

        if ((videocard.sequencer.mapMask & 0x0f) != 0)
        {
            videocard.vgaMemReqUpdate = true;
            if (videocard.sequencer.mapMaskArray[0] != 0)
                videocard.vgaMemory[plane0 + offset] = newValue[0];
            if (videocard.sequencer.mapMaskArray[1] != 0)
                videocard.vgaMemory[plane1 + offset] = newValue[1];
            if (videocard.sequencer.mapMaskArray[2] != 0)
            {
                if ((offset & 0xe000) == videocard.sequencer.charMapAddress)
                {
                    screen.setByteInCodePage((offset & 0x1fff), newValue[2]);
                }
                videocard.vgaMemory[plane2 + offset] = newValue[2];
            }
            if (videocard.sequencer.mapMaskArray[3] != 0)
                videocard.vgaMemory[plane3 + offset] = newValue[3];

            int x_tileno, y_tileno;

            if (videocard.graphicsController.shift256Reg == 2)
            {
                offset -= startAddress;
                x_tileno = (offset % videocard.lineOffset) * 4 / (VideoCard.X_TILESIZE / 2);
                if (videocard.crtControllerRegister.scanDoubling != 0)
                {
                    y_tileno = (offset / videocard.lineOffset) / (VideoCard.Y_TILESIZE / 2);
                }
                else
                {
                    y_tileno = (offset / videocard.lineOffset) / VideoCard.Y_TILESIZE;
                }
               videocard.setTileUpdate(x_tileno, y_tileno, true);
            }
            else
            {
                if (videocard.lineCompare < videocard.verticalDisplayEnd)
                {
                    if (videocard.lineOffset > 0)
                    {
                        if (videocard.sequencer.dotClockRate != 0)
                        {
                            x_tileno = (offset % videocard.lineOffset) / (VideoCard.X_TILESIZE / 16);
                        }
                        else
                        {
                            x_tileno = (offset % videocard.lineOffset) / (VideoCard.X_TILESIZE / 8);
                        }
                        if (videocard.crtControllerRegister.scanDoubling != 0)
                        {
                            y_tileno = ((offset / videocard.lineOffset) * 2 + videocard.lineCompare + 1) / VideoCard.Y_TILESIZE;
                        }
                        else
                        {
                            y_tileno = ((offset / videocard.lineOffset) + videocard.lineCompare + 1) / VideoCard.Y_TILESIZE;
                        }
                       videocard.setTileUpdate(x_tileno, y_tileno, true);
                    }
                }
                if (offset >= startAddress)
                {
                    offset -= startAddress;
                    if (videocard.lineOffset > 0)
                    {
                        if (videocard.sequencer.dotClockRate != 0)
                        {
                            x_tileno = (offset % videocard.lineOffset) / (VideoCard.X_TILESIZE / 16);
                        }
                        else
                        {
                            x_tileno = (offset % videocard.lineOffset) / (VideoCard.X_TILESIZE / 8);
                        }
                        if (videocard.crtControllerRegister.scanDoubling != 0)
                        {
                            y_tileno = (offset / videocard.lineOffset) / (VideoCard.Y_TILESIZE / 2);
                        }
                        else
                        {
                            y_tileno = (offset / videocard.lineOffset) / VideoCard.Y_TILESIZE;
                        }
                       videocard.setTileUpdate(x_tileno, y_tileno, true);
                    }
                }
            }
        }
    }

    /*
     * This class is based on VGALowMemoryRegion, found in JPC's VGAcard, and is used
     * to connect the memory range A0000 - C0000 to the vgaMemory array in VideoCard
     */
    public class DiosJPCVideoConnect
    {
        // Added because needed - need to check if used elsewhere in JPC
        private int bankOffset = 0;
        private int latch = 0;
        private final int[] mask16 = new int[]{
            0x00000000,
            0x000000ff,
            0x0000ff00,
            0x0000ffff,
            0x00ff0000,
            0x00ff00ff,
            0x00ffff00,
            0x00ffffff,
            0xff000000,
            0xff0000ff,
            0xff00ff00,
            0xff00ffff,
            0xffff0000,
            0xffff00ff,
            0xffffff00,
            0xffffffff
            };
        private int planeUpdated;
        
        public boolean isCacheable() {return false;}
        public boolean isVolatile() {return true;}
        public void copyContentsInto(int address, byte[] buffer, int off, int len) {
            throw new IllegalStateException("copyContentsInto: Invalid Operation for VGA Card");
        }
        public void copyContentsFrom(int address, byte[] buffer, int off, int len) {
            throw new IllegalStateException("copyContentsFrom: Invalid Operation for VGA Card");
        }
        public long getSize()
        {
            return 0x20000;
        }
        
            public boolean isAllocated()
            {
                return false;
            }

        public byte getByte(int offset)
        {
            // All functionality already implemented. Just need to call with correct parameters.
            return readMode(offset + 0xA0000);
        }
        
        public short getWord(int offset)
        {
            int v = 0xFF & getByte(offset);
            v |= getByte(offset + 1) << 8;
            return (short) v;
        }
        
        public int getDoubleWord(int offset)
        {
            int v = 0xFF & getByte(offset);
            v |= (0xFF & getByte(offset + 1)) << 8;
            v |= (0xFF & getByte(offset + 2)) << 16;
            v |= (0xFF & getByte(offset + 3)) << 24;       
            return v;
        }
        
        public long getQuadWord(int offset)
        {
            long v = 0xFFl & getByte(offset);
            v |= (0xFFl & getByte(offset + 1)) << 8;
            v |= (0xFFl & getByte(offset + 2)) << 16;
            v |= (0xFFl & getByte(offset + 3)) << 24; 
            v |= (0xFFl & getByte(offset + 4)) << 32; 
            v |= (0xFFl & getByte(offset + 5)) << 40; 
            v |= (0xFFl & getByte(offset + 6)) << 48; 
            v |= (0xFFl & getByte(offset + 7)) << 56;       
            return v;
        }
        
        public long getLowerDoubleQuadWord(int offset)
        {
            return getQuadWord(offset);
        }
        
        public long getUpperDoubleQuadWord(int offset)
        {
            return getQuadWord(offset+8);
        }
        
        public void setByte(int offset, byte data)
        {
            // All functionality already implemented. Just need to call with correct parameters.
//        	System.out.printf("%d : %d\n", offset + 0xA0000, data);
            writeMode(offset + 0xA0000, data);
        }
        
        public void setWord(int offset, short data)
        {
            setByte(offset++, (byte)data);
            data >>>= 8;
            setByte(offset, (byte)data);
        }
        
        public void setDoubleWord(int offset, int data)
        {
            setByte(offset++, (byte)data);
            data >>>= 8;
            setByte(offset++, (byte)data);
            data >>>= 8;
            setByte(offset++, (byte)data);
            data >>>= 8;
            setByte(offset, (byte)data);    
        }
        
        public void setQuadWord(int offset, long data)
        {
            setDoubleWord(offset, (int) data);
            setDoubleWord(offset+4, (int) (data >> 32));
        }
        
        public void setLowerDoubleQuadWord(int offset, long data)
        {
            setDoubleWord(offset, (int) data);
            setDoubleWord(offset+4, (int) (data >> 32));
        }
        
        public void setUpperDoubleQuadWord(int offset, long data)
        {
            offset += 8;
            setDoubleWord(offset, (int) data);
            setDoubleWord(offset+4, (int) (data >> 32));
        }
        
        public void clear()
        {
            // Do we need this?
//            internalReset();
        }
        
        public void clear(int start, int length)
        {
            clear();
        }
        
        public int execute(Processor cpu, int offset)
        {
            throw new IllegalStateException("Invalid Operation");
        }
         
        
    }
    
}
