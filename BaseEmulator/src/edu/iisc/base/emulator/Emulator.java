package edu.iisc.base.emulator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Top class owning all classes of the emulator. Entry point
 *
 */
public class Emulator implements Runnable
{

    private GUI gui;
    private CPU cpu;
    
	// Logging
	private static Logger logger = Logger.getLogger("Emulator");
    
	public Emulator(GUI owner)
	{
        this.gui = owner;
        cpu = CPU.getInstance();
    }
	
	public void run()
	{
            
            logger.log(Level.INFO, "===================  RESET MODULES  ===================");
            resetModules();

            logger.log(Level.INFO, "=================== INIT OUTPUT DEVICES ===================");        
            initScreenOutputDevice();
            
            logger.log(Level.INFO, "=================== START CPU ===================");
    		cpu.start();
            
	}
  
    /**
     * Get the gui.
     * 
     * @return gui
     */
    public GUI getGui()
    {
        return this.gui;
    }
  

	public String getScreenText()
	{
		if (cpu.video != null)
		{
			return cpu.video.getVideoBufferCharacters();
		}
		return null;
	}
    
    private boolean resetModules() {
    	cpu.video.screen.reset();
		return true;
	}

	/**
     * Init Screen Output Device.
     */
    public boolean initScreenOutputDevice()
    {
        if (cpu.video.screen != null)
        {
            gui.setScreen(cpu.video.screen.getScreen());
            return true;
        }
        else
        {
            logger.log(Level.WARNING, "[emu] No screen available.");
            return false;
        }
    }
    
}
