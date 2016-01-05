package edu.iisc.base.emulator;

public class MemoryBlock
{
	
	MainMemory mainMemory;
    private Integer blockStart;
    
    public MemoryBlock(MainMemory mainMemory, Integer address) {
		this.blockStart = address;
		this.mainMemory = mainMemory;
	}
    
    public byte getByte(int offset) {
    	return mainMemory.memory[blockStart + offset & CONSTANTS.PAGE_OFFSET_MASK];
    }

    public void setByte(int offset, byte data) {
    	mainMemory.memory[blockStart + offset & CONSTANTS.PAGE_OFFSET_MASK] = data;
    }

	public void clear() {
		for (int i = 0; i < CONSTANTS.BLOCK_SIZE; i++)
			setByte(i, (byte) 0);
	}

	public void clear(int start, int length) {
		
		int limit = start + length;
		if (limit > CONSTANTS.BLOCK_SIZE)
			throw new ArrayIndexOutOfBoundsException(
					"Attempt to clear outside of memory bounds");
		for (int i = start; i < limit; i++)
			setByte(i, (byte) 0);
	}

	public void copyContentsIntoArray(int address, byte[] buffer, int off, int len) {
		for (int i = off; i < off + len; i++, address++)
			buffer[i] = getByte(address);
	}

    public void copyArrayIntoContents(int address, byte[] buffer, int off, int len) {
    	
        for (int i=off; i<off+len; i++, address++)
            setByte(address, buffer[i]);
    }

    protected final short getHalfWordInBytes(int offset)
    {
        int result = 0xFF & getByte(offset+1);
        result <<= 8;
        result |= (0xFF & getByte(offset));
        return (short) result;
    }

    /**
     * Get little-endian word at <code>offset</code> by repeated calls to
     * <code>getByte</code>.
     * @param offset index of first byte of word.
     * @return word at <code>offset</code> as an int.
     */
    protected final int getWordInBytes(int offset)
    {
        int result = 0xFFFF & getHalfWordInBytes(offset+2);
        result <<= 16;
        result |= (0xFFFF & getHalfWordInBytes(offset));
        return result;
    }

    /**
     * Get little-endian doubleword at <code>offset</code> by repeated calls to
     * <code>getByte</code>.
     * @param offset index of first byte of doubleword.
     * @return doubleword at <code>offset</code> as a long.
     */
    protected final long getDoubleWordInBytes(int offset)
    {
        long result = 0xFFFFFFFFl & getWordInBytes(offset+4);
        result <<= 32;
        result |= (0xFFFFFFFFl & getWordInBytes(offset));
        return result;
    }
    
    public short getHalfWord(int offset)
    {
        return getHalfWordInBytes(offset);
    }

    public int getWord(int offset)
    {
        return getWordInBytes(offset);
    } 

    public long getDoubleWord(int offset)
    {
        return getDoubleWordInBytes(offset);
    }

    public long getLowerDoubleWord(int offset)
    {
        return getDoubleWordInBytes(offset);
    }

    public long getUpperDoubleWord(int offset)
    {
        return getDoubleWordInBytes(offset+8);
    }

    /**
     * Set little-endian half word at <code>offset</code> by repeated calls to
     * <code>setByte</code>.
     * @param offset index of first byte of half word.
     * @param data new value as a short.
     */
    protected final void setHalfWordInBytes(int offset, short data)
    {
        setByte(offset, (byte) data);
	offset++;
        setByte(offset, (byte) (data >> 8));
    }

    /**
     * Set little-endian word at <code>offset</code> by repeated calls to
     * <code>setByte</code>.
     * @param offset index of first byte of word.
     * @param data new value as an int.
     */
    protected final void setWordInBytes(int offset, int data)
    {
        setByte(offset, (byte) data);
	offset++;
        data >>= 8;
        setByte(offset, (byte) data);
	offset++;
        data >>= 8;
        setByte(offset, (byte) data);
	offset++;
        data >>= 8;
        setByte(offset, (byte) data);
    }

    /**
     * Set little-endian Doubleword at <code>offset</code> by repeated calls to
     * <code>setByte</code>.
     * @param offset index of first byte of Doubleword.
     * @param data new value as a long.
     */
    protected final void setDoubleWordInBytes(int offset, long data)
    {
        setWordInBytes(offset, (int) data);
        setWordInBytes(offset+4, (int) (data >> 32));
    }

    public void setHalfWord(int offset, short data)
    {
        setHalfWordInBytes(offset, data);
    }

    public void setWord(int offset, int data)
    {
        setWordInBytes(offset, data);
    }

    public void setDoubleWord(int offset, long data)
    {
        setDoubleWordInBytes(offset, data);
    }

    public void setLowerDoubleWord(int offset, long data)
    {
        setDoubleWordInBytes(offset, data);
    }

    public void setUpperDoubleWord(int offset, long data)
    {
        setDoubleWordInBytes(offset+8, data);
    }
    
}
