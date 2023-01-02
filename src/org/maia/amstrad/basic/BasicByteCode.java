package org.maia.amstrad.basic;

public abstract class BasicByteCode extends BasicCode {

	private byte[] bytes;

	protected BasicByteCode(BasicLanguage language, byte[] bytes) {
		super(language);
		this.bytes = bytes;
	}

	@Override
	public String toString() {
		return new BasicByteCodeFormatter().format(this).toString();
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

	public byte[] getBytes() {
		return bytes;
	}

}