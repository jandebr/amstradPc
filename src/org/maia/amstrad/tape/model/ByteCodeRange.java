package org.maia.amstrad.tape.model;

public class ByteCodeRange {

	private int byteCodeOffset;

	private int byteCodeLength;

	public ByteCodeRange(int byteCodeOffset, int byteCodeLength) {
		this.byteCodeOffset = byteCodeOffset;
		this.byteCodeLength = byteCodeLength;
	}

	public String toString() {
		return "ByteCode range [" + getByteCodeOffset() + " , " + getByteCodeEnd() + "]";
	}

	public int getByteCodeEnd() {
		return getByteCodeOffset() + getByteCodeLength() - 1;
	}

	public int getByteCodeOffset() {
		return byteCodeOffset;
	}

	public int getByteCodeLength() {
		return byteCodeLength;
	}

}