package edu.iisc.base.emulator;

public class Display {
	
	public static Display display = null;
	
	AddressSpace addressSpace;
	
	int curRow = 0, curCol = 0;
	int maxRow = 25, maxCol = 80;
	int VRAM_TEXTMODE_START = 0xb8000;
	
	private Display() {
	}
	
	public void setAddressSpace(AddressSpace addressSpace) {
		this.addressSpace = addressSpace;
	}
	
	public static Display getInstance () {
		
		if(display == null)
			display = new Display();
		return display;
	}
	
	public void printString (String str) {
		for (Character ch : str.toCharArray())
			printChar(ch);
//		printChar(' ');
	}
	
	public void printChar(Character ch) {
		
		//Reset the pointer
		if (curCol >= maxCol) {
			curRow++;
			curCol = 0;
		}
		if (curRow >= maxRow) {
			curRow = 0;
//			clearScreen();
		}
		
		// for \n
		if (getAscii(ch) == 10) {
			curRow++;
			curCol = 0;
		}
		
		addressSpace.setByte(VRAM_TEXTMODE_START+ (curRow*maxCol*2) + curCol*2, getAscii(ch));
		curCol++;
	}
	
	public void printChar(byte ch) {
		
		//Reset the pointer
		if (curCol >= maxCol) {
			curRow++;
			curCol = 0;
		}
		if (curRow >= maxRow) {
			curRow = 0;
//			clearScreen();
		}
		
		// for \n
		if (ch == 10) {
			curRow++;
			curCol = 0;
		}
		
		addressSpace.setByte(VRAM_TEXTMODE_START+ (curRow*maxCol*2) + curCol*2, ch);
		curCol++;
	}
	
	private void clearScreen() {
		for (curRow = 0; curRow < maxRow; curRow++) {
			for (curCol = 0; curCol < maxCol; curCol++) {
				addressSpace.setByte(VRAM_TEXTMODE_START+ (curRow*maxCol*2) + curCol*2, getAscii(' '));
			}
		}
		curCol = 0;
		curRow = 0;
	}

	public byte getAscii(Character ch) {
		Integer val = (int)ch;
		return val.byteValue();
	}

}
