package org.maia.amstrad.tape.model;

public class Block {

	private BlockHeader header;

	private BlockData data;

	private int numberOfDataChunks;

	public Block(BlockHeader header, BlockData data) {
		this.header = header;
		this.data = data;
	}

	public String toString() {
		return getHeader().toString();
	}

	public BlockHeader getHeader() {
		return header;
	}

	public BlockData getData() {
		return data;
	}

}