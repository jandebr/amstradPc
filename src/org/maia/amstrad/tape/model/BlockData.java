package org.maia.amstrad.tape.model;

public class BlockData {

	private ByteSequence byteSequence;

	private int numberOfDataChunks;

	public static int MAXIMUM_CHUNKS_PER_BLOCK = 8;

	public BlockData(ByteSequence byteSequence, int numberOfDataChunks) {
		this.byteSequence = byteSequence;
		this.numberOfDataChunks = numberOfDataChunks;
	}

	public ByteSequence getByteSequence() {
		return byteSequence;
	}

	public int getNumberOfDataChunks() {
		return numberOfDataChunks;
	}

}