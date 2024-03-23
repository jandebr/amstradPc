package org.maia.amstrad.tape.decorate;

import java.util.List;

import org.maia.amstrad.tape.decorate.TapeDecorator.TapeSectionDecoration;
import org.maia.amstrad.tape.model.ByteSequence;
import org.maia.amstrad.tape.model.profile.TapeProfile;
import org.maia.amstrad.tape.model.profile.TapeSection;
import org.maia.amstrad.tape.model.profile.TapeSectionType;
import org.maia.amstrad.tape.read.AudioTapeInputStream;
import org.maia.amstrad.tape.read.BlockReaderListener;
import org.maia.amstrad.tape.read.TapeInputStream;

public class TapeDecorator extends SequenceDecorator<TapeSectionDecoration> implements BlockReaderListener {

	private TapeProfile tapeProfile;

	private long previousPosition;

	private boolean startOfTape;

	public TapeDecorator() {
		super(100);
		this.tapeProfile = new TapeProfile();
	}

	public void decorate(TapeSection section) {
		addDecoration(new TapeSectionDecoration(section));
		getTapeProfile().addSection(section);
	}

	public void atStartOfTape(TapeInputStream tape) {
		this.startOfTape = true;
	}

	@Override
	public void atSilenceBeforeBlock(TapeInputStream tape) {
		// already reported data residue
	}

	@Override
	public void atStartOfBlockHeader(TapeInputStream tape) {
		if (this.startOfTape) {
			sectionEnded(tape, TapeSectionType.TAPE_BEGIN);
			this.startOfTape = false;
		} else {
			sectionEnded(tape, TapeSectionType.SILENCE);
		}
	}

	@Override
	public void atEndOfBlockHeader(TapeInputStream tape) {
		sectionEnded(tape, TapeSectionType.HEADER);
	}

	@Override
	public void atSpacerBeforeBlockData(TapeInputStream tape) {
		sectionEnded(tape, TapeSectionType.HEADER_RESIDUE);
	}

	@Override
	public void atStartOfBlockData(TapeInputStream tape) {
		sectionEnded(tape, TapeSectionType.HEADER_SPACER);
	}

	@Override
	public void atStartOfBlockDataResidue(TapeInputStream tape) {
		sectionEnded(tape, TapeSectionType.DATA);
	}

	@Override
	public void atEndOfBlockData(TapeInputStream tape) {
		// report overall residue at end of block
	}

	@Override
	public void atEndOfBlock(TapeInputStream tape) {
		sectionEnded(tape, TapeSectionType.DATA_RESIDUE);
	}

	@Override
	public void readByte(ByteSequence bytecode, int bytecodeOffset, long audioSampleOffset, long audioSampleLength) {
		// no interest
	}

	@Override
	public void overflowBytes(ByteSequence bytecode, int bytecodeOffset) {
		// no interest
	}

	public void enteredNewProgram(TapeInputStream tape) {
		getTapeProfile().lastSilenceSeparatesPrograms();
	}

	public void atEndOfTape(TapeInputStream tape) {
		sectionEnded(tape, TapeSectionType.TAPE_END);
	}

	private void sectionEnded(TapeInputStream tape, TapeSectionType type) {
		long pos = type.equals(TapeSectionType.TAPE_END) ? getLastPosition(tape) : getPosition(tape);
		TapeSection section = new TapeSection(type, this.previousPosition, pos - 1L);
		decorate(section);
		this.previousPosition = pos;
	}

	private long getPosition(TapeInputStream tape) {
		long pos = 0L;
		if (tape instanceof AudioTapeInputStream) {
			pos = ((AudioTapeInputStream) tape).getSamplePosition();
		}
		return pos;
	}

	private long getLastPosition(TapeInputStream tape) {
		long pos = 0L;
		if (tape instanceof AudioTapeInputStream) {
			pos = ((AudioTapeInputStream) tape).getAudioFile().getNumberOfSamples();
		}
		return pos;
	}

	@Override
	protected int getMaximumDecorationsInToString() {
		return size(); // don't truncate
	}

	public List<TapeSectionDecoration> getDecorationsInsideRange(long audioSampleFrom, long audioSampleUntil) {
		return getDecorationsInRange(audioSampleFrom, audioSampleUntil, false);
	}

	public List<TapeSectionDecoration> getDecorationsOverlappingRange(long audioSampleFrom, long audioSampleUntil) {
		return getDecorationsInRange(audioSampleFrom, audioSampleUntil, true);
	}

	public TapeProfile getTapeProfile() {
		return tapeProfile;
	}

	public static class TapeSectionDecoration extends SequenceDecoration {

		private TapeSection section;

		public TapeSectionDecoration(TapeSection section) {
			super(section.getStartPosition(), section.getEndPosition() - section.getStartPosition() + 1L);
			this.section = section;
		}

		@Override
		protected String getHumanReadableDecoration() {
			return getSection().getType().toString();
		}

		public TapeSection getSection() {
			return section;
		}

	}

}