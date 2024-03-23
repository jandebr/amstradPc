package org.maia.amstrad.tape.decorate;

public abstract class SequenceDecoration implements Decoration {

	private long offset;

	private long length;

	protected SequenceDecoration(long offset, long length) {
		this.offset = offset;
		this.length = length;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(48);
		sb.append(getOffset());
		sb.append(" -> ");
		sb.append(getEnd());
		sb.append(" : ");
		sb.append(getHumanReadableDecoration());
		return sb.toString();
	}

	protected abstract String getHumanReadableDecoration();

	public long getEnd() {
		return getOffset() + getLength() - 1L;
	}

	public long getOffset() {
		return offset;
	}

	public long getLength() {
		return length;
	}

}