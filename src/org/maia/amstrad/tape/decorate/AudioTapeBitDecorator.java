package org.maia.amstrad.tape.decorate;

import java.util.List;

import org.maia.amstrad.tape.decorate.AudioTapeBitDecorator.AudioTapeBitDecoration;
import org.maia.amstrad.tape.model.Bit;
import org.maia.amstrad.tape.read.AudioFile;
import org.maia.amstrad.tape.read.AudioTapeInputStream;
import org.maia.amstrad.tape.read.AudioTapeInputStreamListener;

public class AudioTapeBitDecorator extends SequenceDecorator<AudioTapeBitDecoration> implements
		AudioTapeInputStreamListener {

	public AudioTapeBitDecorator() {
		super(100000);
	}

	@Override
	public void readBit(Bit bit, long sampleOffset, long sampleLength, AudioTapeInputStream is) {
		decorate(is.getAudioFile(), sampleOffset, sampleLength, bit);
	}

	public void decorate(AudioFile audioFile, long audioSampleOffset, long audioSampleLength, Bit bit) {
		addDecoration(new AudioTapeBitDecoration(audioFile, audioSampleOffset, audioSampleLength, bit));
	}

	public List<AudioTapeBitDecoration> getDecorationsInsideRange(long audioSampleFrom, long audioSampleUntil) {
		return getDecorationsInRange(audioSampleFrom, audioSampleUntil, false);
	}

	public List<AudioTapeBitDecoration> getDecorationsOverlappingRange(long audioSampleFrom, long audioSampleUntil) {
		return getDecorationsInRange(audioSampleFrom, audioSampleUntil, true);
	}

	public static class AudioTapeBitDecoration extends SequenceDecoration {

		private AudioFile audioFile;

		private Bit bit;

		public AudioTapeBitDecoration(AudioFile audioFile, long audioSampleOffset, long audioSampleLength, Bit bit) {
			super(audioSampleOffset, audioSampleLength);
			this.audioFile = audioFile;
			this.bit = bit;
		}

		@Override
		protected String getHumanReadableDecoration() {
			return Bit.ZERO.equals(getBit()) ? "0" : "1";
		}

		public AudioFile getAudioFile() {
			return audioFile;
		}

		public Bit getBit() {
			return bit;
		}

	}

}