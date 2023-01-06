package org.maia.amstrad.basic;

public abstract class BasicByteCode extends BasicCode {

	private byte[] bytes;

	protected BasicByteCode(byte[] bytes) {
		setBytes(bytes);
	}

	@Override
	public BasicByteCode clone() {
		BasicByteCode clone = null;
		try {
			clone = (BasicByteCode) super.clone();
		} catch (CloneNotSupportedException e) {
			// never the case
		}
		byte[] clonedBytes = new byte[getByteCount()];
		System.arraycopy(getBytes(), 0, clonedBytes, 0, getByteCount());
		clone.setBytes(clonedBytes);
		return clone;
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

	protected void setByte(int index, byte value) {
		getBytes()[index] = value;
	}

	protected void setWord(int index, int value) {
		// little Endian
		byte b1 = (byte) (value % 256);
		byte b2 = (byte) (value / 256);
		setByte(index, b1);
		setByte(index + 1, b2);
	}

	public byte[] getBytes() {
		return bytes;
	}

	protected void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}