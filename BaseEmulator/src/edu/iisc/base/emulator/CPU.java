package edu.iisc.base.emulator;

import edu.iisc.base.emulator.video.Video;

public class CPU {
	
	private static CPU instance;
	public Processor processor;
	
	public MainMemory ram;						// Main memory
	public Video video;							// Video (VGA)
	public AddressSpace addressSpace;			// Address space
	
	private CPU() {		
		
		video = new Video(CONSTANTS.VIDEO_RAM_BASE, CONSTANTS.VIDEO_RAM_SIZE);	
		ram = new MainMemory(CONSTANTS.RAM_BASE, CONSTANTS.RAM_SIZE);		

		addressSpace = new AddressSpace();		
		addressSpace.addRegion(video);
		addressSpace.addRegion(ram);
		
		processor = new Processor(addressSpace);
		
	}
	
	public static CPU getInstance () {
		if (instance == null)
			instance = new CPU();
		
		return instance;
	}
	
	public void start() {
		//Initialize PC and start Processing
		processor.startProcess();
	}
	
	public void stop() {
		
	}
	
	public void restart() {
		
	}
	
}
