package org.maia.amstrad.gui.memory.model;

public class MemorySegment {

	private int byteOffset;

	private int byteLength;

	private String label;

	private int colorIndex;

	public MemorySegment(int byteOffset, int byteLength, String label, int colorIndex) {
		this.byteOffset = byteOffset;
		this.byteLength = byteLength;
		this.label = label;
		this.colorIndex = colorIndex;
	}

	public boolean isEmpty() {
		return getByteLength() == 0;
	}

	public int getByteEnd() {
		return getByteOffset() + getByteLength() - 1;
	}

	public int getByteOffset() {
		return byteOffset;
	}

	public int getByteLength() {
		return byteLength;
	}

	public String getLabel() {
		return label;
	}

	public int getColorIndex() {
		return colorIndex;
	}

}