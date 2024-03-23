package org.maia.amstrad.tape.model;

public class AudioRange {

	private long sampleOffset;

	private long sampleLength;

	public AudioRange(long sampleOffset, long sampleLength) {
		this.sampleOffset = sampleOffset;
		this.sampleLength = sampleLength;
	}

	public String toString() {
		return "Audio range [" + getSampleOffset() + " , " + getSampleEnd() + "]";
	}

	public boolean contains(long samplePosition) {
		return samplePosition >= getSampleOffset() && samplePosition <= getSampleEnd();
	}

	public long getSampleEnd() {
		return getSampleOffset() + getSampleLength() - 1L;
	}

	public long getSampleOffset() {
		return sampleOffset;
	}

	public long getSampleLength() {
		return sampleLength;
	}

}