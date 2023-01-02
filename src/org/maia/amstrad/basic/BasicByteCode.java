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
		return (getByte(index) & 0xff) | ((getByte(index + 1) << 8) & 0xff00);
	}

	public byte[] getBytes() {
		return bytes;
	}

}