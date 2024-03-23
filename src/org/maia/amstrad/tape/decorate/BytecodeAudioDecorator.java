package org.maia.amstrad.tape.decorate;

import java.util.Iterator;
import java.util.List;

import org.maia.amstrad.tape.decorate.BytecodeAudioDecorator.BytecodeAudioDecoration;
import org.maia.amstrad.tape.model.ByteSequence;
import org.maia.amstrad.tape.read.BlockReaderListener;
import org.maia.amstrad.tape.read.TapeInputStream;

public class BytecodeAudioDecorator extends SequenceDecorator<BytecodeAudioDecoration> implements BlockReaderListener {

	public BytecodeAudioDecorator() {
		super(2048);
	}

	@Override
	public void atSilenceBeforeBlock(TapeInputStream tape) {
		// no interest
	}

	@Override
	public void atStartOfBlockHeader(TapeInputStream tape) {
		// no interest
	}

	@Override
	public void atEndOfBlockHeader(TapeInputStream tape) {
		// no interest
	}

	@Override
	public void atSpacerBeforeBlockData(TapeInputStream tape) {
		// no interest
	}

	@Override
	public void atStartOfBlockData(TapeInputStream tape) {
		// no interest
	}

	@Override
	public void atStartOfBlockDataResidue(TapeInputStream tape) {
		// no interest
	}

	@Override
	public void atEndOfBlockData(TapeInputStream tape) {
		// no interest
	}

	@Override
	public void atEndOfBlock(TapeInputStream tape) {
		// no interest
	}

	@Override
	public void readByte(ByteSequence bytecode, int bytecodeOffset, long audioSampleOffset, long audioSampleLength) {
		decorate(bytecode, bytecodeOffset, 1, audioSampleOffset, audioSampleLength);
	}

	@Override
	public void overflowBytes(ByteSequence bytecode, int bytecodeOffset) {
		Iterator<BytecodeAudioDecoration> it = getDecorationsInOrderIterator();
		while (it.hasNext()) {
			BytecodeAudioDecoration decoration = it.next();
			if (decoration.getOffset() >= bytecodeOffset) {
				it.remove();
			}
		}
	}

	public void decorate(ByteSequence bytecode, int bytecodeOffset, int bytecodeLength, long audioSampleOffset,
			long audioSampleLength) {
		addDecoration(new BytecodeAudioDecoration(bytecode, bytecodeOffset, bytecodeLength, audioSampleOffset,
				audioSampleLength));
	}

	public List<BytecodeAudioDecoration> getDecorationsInsideRange(int bytecodeFrom, int bytecodeUntil) {
		return getDecorationsInRange(bytecodeFrom, bytecodeUntil, false);
	}

	public List<BytecodeAudioDecoration> getDecorationsOverlappingRange(int bytecodeFrom, int bytecodeUntil) {
		return getDecorationsInRange(bytecodeFrom, bytecodeUntil, true);
	}

	public static class BytecodeAudioDecoration extends SequenceDecoration {

		private ByteSequence bytecode;

		private long audioSampleOffset;

		private long audioSampleLength;

		public BytecodeAudioDecoration(ByteSequence bytecode, int bytecodeOffset, int bytecodeLength,
				long audioSampleOffset, long audioSampleLength) {
			super(bytecodeOffset, bytecodeLength);
			this.bytecode = bytecode;
			this.audioSampleOffset = audioSampleOffset;
			this.audioSampleLength = audioSampleLength;
		}

		@Override
		protected String getHumanReadableDecoration() {
			StringBuilder sb = new StringBuilder(48);
			int i = (int) getOffset();
			int j = i + (int) getLength();
			sb.append(getBytecode().subSequence(i, j).toHumanReadableString());
			sb.append(" (audio@");
			sb.append(getAudioSampleOffset());
			sb.append("->");
			sb.append(getAudioSampleOffset() + getAudioSampleLength() - 1L);
			sb.append(")");
			return sb.toString();
		}

		public ByteSequence getBytecode() {
			return bytecode;
		}

		public long getAudioSampleEnd() {
			return getAudioSampleOffset() + getAudioSampleLength() - 1L;
		}

		public long getAudioSampleOffset() {
			return audioSampleOffset;
		}

		public long getAudioSampleLength() {
			return audioSampleLength;
		}

	}

}