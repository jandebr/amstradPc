package org.maia.amstrad.tape.model.profile;

import java.io.Serializable;

public class TapeSection implements Serializable {

	private static final long serialVersionUID = -9152643622280283353L;

	private TapeSectionType type;

	private long startPosition;

	private long endPosition;

	public TapeSection(TapeSectionType type, long startPosition, long endPosition) {
		this.type = type;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		sb.append(getType());
		sb.append(" from ");
		sb.append(getStartPosition());
		sb.append(" to ");
		sb.append(getEndPosition());
		return sb.toString();
	}

	public void changeType(TapeSectionType newType) {
		this.type = newType;
	}

	public TapeSectionType getType() {
		return type;
	}

	public long getStartPosition() {
		return startPosition;
	}

	public long getEndPosition() {
		return endPosition;
	}

}