package org.maia.amstrad.tape.decorate;

import java.util.List;

import org.maia.amstrad.tape.decorate.BlockAudioDecorator.BlockAudioDecoration;
import org.maia.amstrad.tape.model.Block;
import org.maia.amstrad.tape.model.TapeProgram;
import org.maia.amstrad.tape.model.profile.TapeProfile;
import org.maia.amstrad.tape.model.profile.TapeSectionType;
import org.maia.amstrad.tape.read.TapeReaderListener;

public class BlockAudioDecorator extends SequenceDecorator<BlockAudioDecoration> implements TapeReaderListener {

	private TapeDecorator tapeDecorator;

	public BlockAudioDecorator(TapeDecorator tapeDecorator) {
		super(100);
		this.tapeDecorator = tapeDecorator;
	}

	@Override
	public void startReadingTape() {
		// no interest
	}

	@Override
	public void endReadingTape() {
		// no interest
	}

	@Override
	public void foundNewBlock(Block block) {
		TapeProfile profile = getTapeDecorator().getTapeProfile();
		int n = profile.getSections().size();
		// Start of block
		int i = n - 1;
		while (i > 0 && !profile.getSections().get(i).getType().equals(TapeSectionType.HEADER))
			i--;
		long audioSampleOffset = profile.getSections().get(i).getStartPosition();
		// End of block
		long audioSampleEnd = profile.getSections().get(n - 1).getEndPosition();
		// Add decoration
		decorate(block, audioSampleOffset, audioSampleEnd - audioSampleOffset + 1L);
	}

	@Override
	public void startReadingProgram(TapeProgram program) {
		// no interest
	}

	@Override
	public void endReadingProgram(TapeProgram program, BytecodeAudioDecorator byteCodeDecorator) {
		// no interest
	}

	public void decorate(Block block, long audioSampleOffset, long audioSampleLength) {
		addDecoration(new BlockAudioDecoration(block, audioSampleOffset, audioSampleLength));
	}

	public List<BlockAudioDecoration> getDecorationsInsideRange(long audioSampleFrom, long audioSampleUntil) {
		return getDecorationsInRange(audioSampleFrom, audioSampleUntil, false);
	}

	public List<BlockAudioDecoration> getDecorationsOverlappingRange(long audioSampleFrom, long audioSampleUntil) {
		return getDecorationsInRange(audioSampleFrom, audioSampleUntil, true);
	}

	private TapeDecorator getTapeDecorator() {
		return tapeDecorator;
	}

	public static class BlockAudioDecoration extends SequenceDecoration {

		private Block block;

		public BlockAudioDecoration(Block block, long audioSampleOffset, long audioSampleLength) {
			super(audioSampleOffset, audioSampleLength);
			this.block = block;
		}

		@Override
		protected String getHumanReadableDecoration() {
			return getBlock().toString();
		}

		public Block getBlock() {
			return block;
		}

	}

}