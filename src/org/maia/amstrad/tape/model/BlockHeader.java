package org.maia.amstrad.tape.model;

public class BlockHeader {

	private String programName;

	private int blockNumber;

	public BlockHeader(String programName, int blockNumber) {
		this.programName = programName;
		this.blockNumber = blockNumber;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(24);
		sb.append('"').append(getProgramName()).append('"');
		sb.append(" block ").append(getBlockNumber());
		return sb.toString();
	}

	public String getProgramName() {
		return programName;
	}

	public int getBlockNumber() {
		return blockNumber;
	}

}
