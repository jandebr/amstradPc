package org.maia.amstrad.basic;

public abstract class BasicByteCode extends BasicCode {

	private byte[] bytes;

	protected BasicByteCode(BasicLanguage language, byte[] bytes) {
		super(language);
		this.bytes = bytes;
	}

	public boolean isEmpty() {
		return getByteCount() == 0;
	}

	public int getByteCount() {
		return getBytes().length;
	}

	public byte getByte(int index) {
		return getBytes()[index];
	}

	public int getWord(int index) {
		// little Endian
		return (getByte(index) & 0xff) | ((getByte(index + 1) << 8) & 0xff00);
	}

	public void setByte(int index, byte value) {
		getBytes()[index] = value;
	}

	public void setWord(int index, int value) {
		// little Endian
		byte b1 = (byte) (value % 256);
		byte b2 = (byte) (value / 256);
		setByte(index, b1);
		setByte(index + 1, b2);
	}

	public byte[] getBytes() {
		return bytes;
	}

}