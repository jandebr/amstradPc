package org.maia.amstrad.tape.model;

public class SourceCodePosition {

	private int lineNumber;

	private int linePosition;

	public SourceCodePosition(int lineNumber, int linePosition) {
		this.lineNumber = lineNumber;
		this.linePosition = linePosition;
	}

	@Override
	public String toString() {
		return String.valueOf(getLineNumber()) + "." + String.valueOf(getLinePosition());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SourceCodePosition))
			return false;
		SourceCodePosition other = (SourceCodePosition) obj;
		return other.getLineNumber() == getLineNumber() && other.getLinePosition() == getLinePosition();
	}

	@Override
	public int hashCode() {
		return getLineNumber() << 8 | getLinePosition();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getLinePosition() {
		return linePosition;
	}

	public void setLinePosition(int linePosition) {
		this.linePosition = linePosition;
	}

}