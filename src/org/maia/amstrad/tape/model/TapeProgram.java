package org.maia.amstrad.tape.model;

import java.util.List;
import java.util.Vector;

public class TapeProgram {

	private List<Block> blocks;

	private ByteSequence byteCode;

	public TapeProgram() {
		this.blocks = new Vector<Block>();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(24);
		sb.append('"').append(getProgramName()).append('"');
		sb.append(' ').append(getNumberOfBlocks()).append(" blocks");
		return sb.toString();
	}

	public boolean accept(Block block) {
		return accept(block.getHeader());
	}

	public boolean accept(BlockHeader header) {
		if (getNumberOfBlocks() == 0)
			return true;
		if (!header.getProgramName().equals(getProgramName()))
			return false;
		return header.getBlockNumber() == getLastBlock().getHeader().getBlockNumber() + 1;
	}

	public void addBlock(Block block) {
		if (!accept(block)) {
			throw new IllegalArgumentException("Block is not a continuation of the program");
		} else {
			if (getNumberOfBlocks() > 0) {
				// Check missing chunks
				Block prevBlock = getLastBlock();
				int missingChunks = BlockData.MAXIMUM_CHUNKS_PER_BLOCK - prevBlock.getData().getNumberOfDataChunks();
				if (missingChunks > 0) {
					System.err.println("There are " + missingChunks + " missing data chunks in " + prevBlock);
				}
			}
			getBlocks().add(block);
			invalidateByteCode();
		}
	}

	private void invalidateByteCode() {
		this.byteCode = null;
	}

	public String getProgramName() {
		if (getNumberOfBlocks() == 0)
			return null;
		return getLastBlock().getHeader().getProgramName();
	}

	public ByteSequence getByteCode() {
		if (byteCode == null) {
			byteCode = generateByteCode();
		}
		return byteCode;
	}

	private ByteSequence generateByteCode() {
		ByteSequence byteCode = new ByteSequence();
		for (Block block : getBlocks()) {
			byteCode.addBytes(block.getData().getByteSequence());
		}
		return byteCode;
	}

	public int getNumberOfBlocks() {
		return getBlocks().size();
	}

	public Block getLastBlock() {
		return getBlocks().get(getNumberOfBlocks() - 1);
	}

	public List<Block> getBlocks() {
		return blocks;
	}

}