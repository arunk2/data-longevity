package edu.iisc.base.emulator;

import java.io.IOException;

/**
 * CPU clock implementation.
 * 
 * @author casl
 *
 */
public class SystemClock {
	
	private Processor processor;
	private boolean running = true;
		
	public void stopRunning() {
		running = false;
		/////////////
//		try {
//			processor.writer.flush();
//			processor.writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		/////////////
	}
	
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
	public void startProcess() {

		running = true;
		new Thread() {
			@Override
			public void run() {
				long start;
				long end;
				int i = 0;
				
				while (running) {
					start = System.nanoTime();
					// Execute an instruction
					processor.executeInstruction();
					
					//Check for interrupts 
					if (i == 25) {
						processor.checkInterrupts();
						i = 0;
						CPU.getInstance().video.update();
					}
					i++;
										
					end = System.nanoTime();
					
					//Wait for 1000 nano-second or 1 micro-second elapse
					while (end - start < 1000) {
						end = System.nanoTime();
					}
					
				}
			}
		}.start();
	}
}