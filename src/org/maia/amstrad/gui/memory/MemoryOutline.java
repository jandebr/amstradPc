package org.maia.amstrad.gui.memory;

import java.util.List;
import java.util.Vector;

public class MemoryOutline {

	private List<MemorySegment> segments;

	private int byteOffset;

	public MemoryOutline() {
		this.segments = new Vector<MemorySegment>();
	}

	public MemorySegment appendSegment(int byteLength, String label, int colorIndex) {
		MemorySegment segment = new MemorySegment(getByteOffset(), byteLength, label, colorIndex);
		appendSegment(segment);
		return segment;
	}

	public void appendSegment(MemorySegment segment) {
		getSegments().add(segment);
		setByteOffset(getByteOffset() + segment.getByteLength());
	}

	public int getSegmentCount() {
		return getSegments().size();
	}

	public MemorySegment getSegment(int index) {
		return getSegments().get(index);
	}

	public int getByteLength() {
		return getByteOffset();
	}

	private List<MemorySegment> getSegments() {
		return segments;
	}

	private int getByteOffset() {
		return byteOffset;
	}

	private void setByteOffset(int byteOffset) {
		this.byteOffset = byteOffset;
	}

}