package edu.iisc.base.emulator;

public class Loader {
	
	private Processor processor;
	
	public Loader(Processor processor) {
		this.processor = processor;
	}
	
	/**
	 * Load the base code ELF code for executing solution for data longevity
	 * @param name 
	 */
	public void loadApplication(String name) {
		
		// load the QEMU emulator cross compiled for MIPSR2000
		ELFReader reader = new ELFReader(processor.addressSpace, name);
		reader.processELFFile();
		//Sets the PC value to start from the memory location given
		processor.setPC(reader.startAddress);
		
		//initialize stack for the main method call		
		processor.addressSpace.setByte(0x76ffffff, (byte)'\0');
		processor.addressSpace.setByte(0x76fffffe, (byte)'o');
		processor.addressSpace.setByte(0x76fffffd, (byte)'.');
		processor.addressSpace.setByte(0x76fffffc, (byte)'s');
		processor.addressSpace.setByte(0x76fffffb, (byte)'p');
		processor.addressSpace.setByte(0x76fffffa, (byte)'i');
		processor.addressSpace.setByte(0x76fffff9, (byte)'m');
		processor.addressSpace.setByte(0x76fffff8, (byte)'_');
		processor.addressSpace.setByte(0x76fffff7, (byte)'t');
		processor.addressSpace.setByte(0x76fffff6, (byte)'s');
		processor.addressSpace.setByte(0x76fffff5, (byte)'e');
		processor.addressSpace.setByte(0x76fffff4, (byte)'t'); 
		processor.addressSpace.setWord(0x76fffff0, 0); //argv[1]
		processor.addressSpace.setWord(0x76ffffec, 0x76fffff4); //argv[0]
		processor.addressSpace.setWord(0x76ffffe8, 0x76ffffec); //argv
		processor.addressSpace.setWord(0x76ffffe4, 1); //argc
		processor.addressSpace.setWord(0x76ffffe0, 0);
		processor.setSP(0x76ffffe0);
		
		processor.CP0.R[CoPROCESSOR_0.REG_STATUS].setValue(0x20000010);
		
	}
}
