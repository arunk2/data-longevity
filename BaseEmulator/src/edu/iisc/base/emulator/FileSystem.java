package edu.iisc.base.emulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class FileSystem {
	
	public static FileSystem instance = null;
	public Map<Integer, String> fdTable = new HashMap<Integer, String>();
	
	private FileSystem() {
	}
	
	public static FileSystem getInstance () {
		if (instance == null)
			instance = new FileSystem();
		
		return instance;
	}
	
	//Always open the corresponding data file
	public int open (int fileName, int mode, int permission) {
		return 3;
	}
	
	public int read (int fd, int buf, int len) {
		
		char[] buffer = new char[1024];
		String data = "data from file\n";
		Integer pos = 0;
		
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader("datafile-eng.txt");

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			bufferedReader.read(buffer);

			// Always close files.
			bufferedReader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/////////
		data = new String(buffer);
//		data = data.trim();
		
		//Read from file (always the file corresponding to user 
//		if (fd == 3) 
		{

			for (Character ch : data.toCharArray()) {
				if(pos >= len-1)
					return pos;
				Integer val = (int)ch;
				CPU.getInstance().addressSpace.setByte(buf+pos, val.byteValue());	
				pos++;
			}			
		}
		return pos;
		
	}
	
	public int write (int fd, int buf, int len) {
		//Write to file - mainly used to write to display
		
		Integer pos = 0;
		//Standard output and error descriptors
		if (fd == 1 || fd == 2) {
			
			for (int i = 0; i < len; i++) {
				byte val = CPU.getInstance().addressSpace.getByte(buf+i);
				Display display = Display.getInstance();
				display.printChar(val);
				pos++;
			}
		}
		
		return pos;
	}
}
