package org.maia.amstrad.tape.read;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.tape.model.Bit;
import org.maia.amstrad.tape.model.Block;
import org.maia.amstrad.tape.model.BlockData;
import org.maia.amstrad.tape.model.BlockHeader;
import org.maia.amstrad.tape.model.ByteSequence;

public class BlockReader {

	private List<BlockReaderListener> listeners;

	private static int CHUNK_DATA_SIZE = 256; // in bytes

	private static int CHUNK_CHECKSUM_SIZE = 2; // in bytes

	private static int MAXIMUM_CHUNKS = 8; // 8 chunks times 256 bytes = 2k blocks

	public BlockReader() {
		this.listeners = new Vector<BlockReaderListener>();
	}

	public void addListener(BlockReaderListener listener) {
		getListeners().add(listener);
	}

	public void removeListener(BlockReaderListener listener) {
		getListeners().remove(listener);
	}

	public BlockHeader findAndReadNextBlockHeader(TapeInputStream tape) throws Exception {
		// Advance to start of header
		if (!seekSilence(tape))
			return null;
		fireAtSilenceBeforeBlock(tape);
		if (!skipSilence(tape))
			return null;
		// Read header
		fireAtStartOfBlockHeader(tape);
		BlockHeader header = findAndReadBlockHeader(tape);
		if (header != null)
			fireAtEndOfBlockHeader(tape);
		return header;
	}

	public Block findAndReadNextBlockData(TapeInputStream tape, BlockHeader header, ByteSequence dataBuffer)
			throws Exception {
		// Skip spacer between header and data
		if (!seekSilence(tape))
			return null;
		fireAtSpacerBeforeBlockData(tape);
		if (!skipSilence(tape))
			return null;
		// Read payload
		fireAtStartOfBlockData(tape);
		int dataBufferPreviousLength = dataBuffer.getLength();
		if (!findAndReadBlockData(tape, dataBuffer))
			return null;
		ByteSequence blockData = dataBuffer.subSequence(dataBufferPreviousLength, dataBuffer.getLength());
		int nchunks = blockData.getLength() / CHUNK_DATA_SIZE;
		BlockData data = new BlockData(blockData, nchunks);
		fireAtEndOfBlockData(tape);
		// Advance to end of block
		seekSilence(tape);
		fireAtEndOfBlock(tape);
		return new Block(header, data);
	}

	private boolean seekSilence(TapeInputStream tape) throws IOException {
		System.out.println("\tSeeking silence: " + tape);
		boolean found = tape.seekSilence();
		if (found) {
			System.out.println("\tMoved to start of silence: " + tape);
		} else {
			System.out.println("\tNo silence ahead");
		}
		return found;
	}

	private boolean skipSilence(TapeInputStream tape) throws IOException {
		System.out.println("\tSkipping silence: " + tape);
		boolean found = tape.skipSilence();
		if (found) {
			System.out.println("\tMoved to end of silence: " + tape);
		} else {
			System.out.println("\tNo signal ahead");
		}
		return found;
	}

	private BlockHeader findAndReadBlockHeader(TapeInputStream tape) throws Exception {
		// Find cue
		if (!findAndReadMatchingBit(Bit.ZERO, tape))
			return null;
		Byte bite = tape.nextByte();
		if (bite == null)
			return null;
		if ((bite.byteValue() & 0xff) != 44)
			System.err.println("Expected block header cue byte 44 but read " + bite);
		// Read program name
		System.out.println("\tStart reading block header: " + tape);
		StringBuilder programName = new StringBuilder(16);
		for (int i = 0; i < 16; i++) {
			bite = tape.nextByte();
			if (bite != null) {
				int a = bite.byteValue() & 0xff;
				if (a > 0)
					programName.append((char) a);
			}
		}
		if (programName.length() == 0)
			return null;
		// Read block number
		bite = tape.nextByte();
		if (bite == null)
			return null;
		int blockNumber = bite.byteValue() & 0xff;
		System.out.println("\tEnd reading block header: " + tape);
		return new BlockHeader(programName.toString(), blockNumber);
	}

	private boolean findAndReadBlockData(TapeInputStream tape, ByteSequence dataBuffer) throws Exception {
		// Find cue
		if (!findAndReadMatchingBit(Bit.ZERO, tape))
			return false;
		Byte bite = tape.nextByte();
		if (bite == null)
			return false;
		if ((bite.byteValue() & 0xff) != 22)
			System.err.println("Expected block payload cue byte 22 but read " + bite);
		// Read payload
		ByteSequence blockData = new ByteSequence();
		int dataBufferPreviousLength = dataBuffer.getLength();
		int i = 0;
		int chunkSize = CHUNK_DATA_SIZE + CHUNK_CHECKSUM_SIZE;
		int maxDataBytes = MAXIMUM_CHUNKS * CHUNK_DATA_SIZE;
		boolean isAudioTape = tape instanceof AudioTapeInputStream;
		long audioSampleOffset = 0L;
		long audioSampleLength = 0L;
		System.out.println("\tStart reading block data: " + tape);
		do {
			if (isAudioTape) {
				audioSampleOffset = ((AudioTapeInputStream) tape).getSamplePosition();
			}
			bite = tape.nextByte();
			if (bite != null) {
				boolean isChecksum = (i++ % chunkSize >= CHUNK_DATA_SIZE);
				if (!isChecksum) {
					if (isAudioTape) {
						audioSampleLength = ((AudioTapeInputStream) tape).getSamplePosition() - audioSampleOffset;
					}
					blockData.addByte(bite);
					dataBuffer.addByte(bite);
					fireReadByte(dataBuffer, dataBuffer.getLength() - 1, audioSampleOffset, audioSampleLength);
					if (blockData.getLength() == maxDataBytes) {
						// consume final checksum
						for (int c = 0; c < CHUNK_CHECKSUM_SIZE; c++)
							tape.nextByte();
					}
				}
			}
		} while (bite != null && blockData.getLength() < maxDataBytes);
		System.out.println("\tEnd reading block data: " + tape);
		// Truncate to complete chunks
		int nchunks = blockData.getLength() / CHUNK_DATA_SIZE;
		int nchunkBytes = nchunks * CHUNK_DATA_SIZE;
		ByteSequence residue = blockData.subSequence(nchunkBytes, blockData.getLength());
		blockData.truncate(nchunkBytes);
		dataBuffer.truncate(dataBufferPreviousLength + nchunkBytes);
		fireOverflowBytes(dataBuffer, dataBuffer.getLength());
		// Print block info
		System.out.println("\tBlock data byte count: " + nchunkBytes);
		System.out.println("\tBlock data: "
				+ (nchunkBytes == 0 ? "(empty)" : blockData.subSequence(0, 16).toHumanReadableString()
						+ "....."
						+ blockData.subSequence(blockData.getLength() - 16, blockData.getLength())
								.toHumanReadableString()));
		// Read trailing bytes as residue
		fireAtStartOfBlockDataResidue(tape);
		while ((bite = tape.nextByte()) != null) {
			residue.addByte(bite);
		}
		System.out.println("\tBlock residue bytes: " + residue.toHumanReadableString());
		return true;
	}

	private boolean findAndReadMatchingBit(Bit bit, TapeInputStream tape) throws Exception {
		Bit b;
		do {
			b = tape.nextBit();
		} while (b != null && !bit.equals(b));
		return b != null;
	}

	private void fireAtSilenceBeforeBlock(TapeInputStream tape) {
		for (BlockReaderListener listener : getListeners())
			listener.atSilenceBeforeBlock(tape);
	}

	private void fireAtStartOfBlockHeader(TapeInputStream tape) {
		for (BlockReaderListener listener : getListeners())
			listener.atStartOfBlockHeader(tape);
	}

	private void fireAtEndOfBlockHeader(TapeInputStream tape) {
		for (BlockReaderListener listener : getListeners())
			listener.atEndOfBlockHeader(tape);
	}

	private void fireAtSpacerBeforeBlockData(TapeInputStream tape) {
		for (BlockReaderListener listener : getListeners())
			listener.atSpacerBeforeBlockData(tape);
	}

	private void fireAtStartOfBlockData(TapeInputStream tape) {
		for (BlockReaderListener listener : getListeners())
			listener.atStartOfBlockData(tape);
	}

	private void fireAtEndOfBlockData(TapeInputStream tape) {
		for (BlockReaderListener listener : getListeners())
			listener.atEndOfBlockData(tape);
	}

	private void fireAtEndOfBlock(TapeInputStream tape) {
		for (BlockReaderListener listener : getListeners())
			listener.atEndOfBlock(tape);
	}

	private void fireAtStartOfBlockDataResidue(TapeInputStream tape) {
		for (BlockReaderListener listener : getListeners())
			listener.atStartOfBlockDataResidue(tape);
	}

	private void fireReadByte(ByteSequence bytecode, int bytecodeOffset, long audioSampleOffset, long audioSampleLength) {
		for (BlockReaderListener listener : getListeners())
			listener.readByte(bytecode, bytecodeOffset, audioSampleOffset, audioSampleLength);
	}

	private void fireOverflowBytes(ByteSequence bytecode, int bytecodeOffset) {
		for (BlockReaderListener listener : getListeners())
			listener.overflowBytes(bytecode, bytecodeOffset);
	}

	private List<BlockReaderListener> getListeners() {
		return listeners;
	}

}