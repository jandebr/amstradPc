package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicException;

public class LocomotiveBasicNumericRepresentation {

	public static final int INTEGER_MIN_VALUE = -32768;

	public static final int INTEGER_MAX_VALUE = 32767;

	private LocomotiveBasicNumericRepresentation() {
	}

	public static int bytesToInteger(byte b1, byte b2) {
		return wordToInteger(bytesToWord(b1, b2));
	}

	public static int wordToInteger(int word) {
		if (word <= INTEGER_MAX_VALUE) {
			return word;
		} else {
			return word - 65536;
		}
	}

	public static byte[] integerToBytes(int integerValue) throws NumberOverflowException {
		return wordToBytes(integerToWord(integerValue));
	}

	public static int integerToWord(int integerValue) throws NumberOverflowException {
		if (integerValue < INTEGER_MIN_VALUE || integerValue > INTEGER_MAX_VALUE)
			throw new NumberOverflowException("Overflow integer: " + integerValue);
		if (integerValue >= 0) {
			return integerValue;
		} else {
			return integerValue + 65536;
		}
	}

	public static double bytesToFloatingPoint(byte[] bytes) {
		return bytesToFloatingPoint(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4]);
	}

	public static double bytesToFloatingPoint(byte m1, byte m2, byte m3, byte m4, byte e) {
		// Mantissa
		long mt1 = m1 & 0xff;
		long mt2 = m2 & 0xff;
		long mt3 = m3 & 0xff;
		long mt4 = m4 & 0xff;
		long mantissa = (mt4 | 0x80) << 24 | mt3 << 16 | mt2 << 8 | mt1;
		// Sign
		double sign = (mt4 & 0x80) == 0 ? 1.0 : -1.0;
		// Exponent
		int exp = (e & 0xff) - 128;
		// Value
		return sign * mantissa / Math.pow(2, 32 - exp);
	}

	public static byte[] floatingPointToBytes(double floatingPointValue) {
		boolean negativeSign = floatingPointValue < 0d;
		double value = Math.abs(floatingPointValue); // positive number
		double fractionalPart = value % 1;
		long integralPart = (long) (value - fractionalPart);
		int[] mantissaBits = new int[32];
		int exponent = 128;
		int mi = 0;
		boolean zeros = true;
		// Integral part
		if (integralPart > 0L) {
			String binaryStr = Long.toString(integralPart, 2);
			for (int i = 0; i < binaryStr.length(); i++)
				mantissaBits[mi++] = binaryStr.charAt(i) - '0';
			exponent += binaryStr.length();
			zeros = false;
		}
		// Fractional part
		while (fractionalPart != 0 && mi < 32) {
			fractionalPart *= 2.0;
			if (fractionalPart >= 1.0) {
				mantissaBits[mi++] = 1;
				fractionalPart = fractionalPart % 1;
				zeros = false;
			} else {
				if (zeros) {
					exponent--;
				} else {
					mantissaBits[mi++] = 0;
				}
			}
		}
		if (fractionalPart >= 0.5) {
			// Round up one binary digit
			mi = 31;
			while (mi > 0 && mantissaBits[mi] == 1)
				mantissaBits[mi--] = 0;
			mantissaBits[mi] = 1;
			if (mi == 0)
				exponent++;
		}
		// Assemble in bytes
		int m4 = bitsToInteger(mantissaBits, 1, 8);
		int m3 = bitsToInteger(mantissaBits, 8, 16);
		int m2 = bitsToInteger(mantissaBits, 16, 24);
		int m1 = bitsToInteger(mantissaBits, 24, 32);
		// Sign
		if (negativeSign) {
			m4 = m4 | 0x80;
		}
		// Special case 0.0
		if (zeros) {
			m4 = 0x28; // convention?
			exponent = 0;
		}
		// Result
		byte[] bytes = new byte[5];
		bytes[0] = (byte) m1;
		bytes[1] = (byte) m2;
		bytes[2] = (byte) m3;
		bytes[3] = (byte) m4;
		bytes[4] = (byte) exponent;
		return bytes;
	}

	private static int bitsToInteger(int[] bits, int fromIndex, int toIndex) {
		int value = 0;
		int f = 1;
		for (int i = 0; i < toIndex - fromIndex; i++) {
			value += f * bits[toIndex - 1 - i];
			f *= 2;
		}
		return value;
	}

	private static int bytesToWord(byte b1, byte b2) {
		return (b1 & 0xff) | ((b2 << 8) & 0xff00); // little Endian
	}

	private static byte[] wordToBytes(int word) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (word % 256); // little Endian
		bytes[1] = (byte) (word / 256);
		return bytes;
	}

	public static class NumberOverflowException extends BasicException {

		public NumberOverflowException(String message) {
			super(message);
		}

	}

}