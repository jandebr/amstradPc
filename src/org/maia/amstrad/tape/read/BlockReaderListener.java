package org.maia.amstrad.tape.read;

import org.maia.amstrad.tape.model.ByteSequence;

public interface BlockReaderListener {

	void atSilenceBeforeBlock(TapeInputStream tape);

	void atStartOfBlockHeader(TapeInputStream tape);

	void atEndOfBlockHeader(TapeInputStream tape);

	void atSpacerBeforeBlockData(TapeInputStream tape);

	void atStartOfBlockData(TapeInputStream tape);

	void atStartOfBlockDataResidue(TapeInputStream tape);

	void atEndOfBlockData(TapeInputStream tape);

	void atEndOfBlock(TapeInputStream tape);

	void readByte(ByteSequence bytecode, int bytecodeOffset, long audioSampleOffset, long audioSampleLength);

	void overflowBytes(ByteSequence bytecode, int bytecodeOffset);

}