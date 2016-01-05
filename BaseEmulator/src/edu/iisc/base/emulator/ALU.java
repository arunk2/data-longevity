package edu.iisc.base.emulator;

import edu.iisc.base.emulator.exception.ProcessorException;
import edu.iisc.base.emulator.exception.ProcessorExceptionType;

public class ALU {
	Integer flag;
	Integer res;
	Integer resHi;
	Integer resLo;
	
	public void add(Integer op1, Integer op2) {

		if (op2 > 0 ? op1 > Integer.MAX_VALUE - op2 
					: op1 < Integer.MIN_VALUE - op2) {
			throw new ProcessorException(ProcessorExceptionType.ARITHMETIC_OVERFLOW);
		}
		res = op1 + op2;
	}
	
	public void sub(Integer op1, Integer op2) {
		add (op1, -op2);
	}
	
	public void mul(Integer op1, Integer op2) {
		Long temp = (long)op1 * (long)op2;
		Long hi = temp >> 16;
		resHi = hi.intValue();
		Long lo = temp & 0X00000000FFFFFFFFL;
		resLo = lo.intValue();
	}
	
	public void div(Integer op1, Integer op2) {
		if (op2.equals(0))
			throw new ProcessorException(ProcessorExceptionType.ARITHMETIC_OVERFLOW);
		resLo = op1 / op2;
		resHi = op1 % op2;
	}

	public void countZeros(Integer value) {
		// TODO Auto-generated method stub
		res = 1;
	}

	public void countOnes(Integer value) {
		// TODO Auto-generated method stub
		res = 1;
	}
}
