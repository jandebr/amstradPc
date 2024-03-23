package org.maia.amstrad.tape.read;

import java.io.IOException;

import org.maia.amstrad.tape.model.AudioRange;

public class ScopedAudioTapeInputStream extends AudioTapeInputStream {

	private AudioRange audioRangeInScope;

	public ScopedAudioTapeInputStream(AudioFile audioFile, AudioRange audioRangeInScope) {
		this(audioFile, audioRangeInScope, DEFAULT_SPEED_FACTOR);
	}

	public ScopedAudioTapeInputStream(AudioFile audioFile, AudioRange audioRangeInScope, float speedFactor) {
		super(audioFile, speedFactor);
		this.audioRangeInScope = audioRangeInScope;
	}

	@Override
	protected short getSample(long index) throws IOException {
		if (getAudioRangeInScope().contains(index)) {
			return super.getSample(index);
		} else {
			return 0; // outside scope
		}
	}

	public AudioRange getAudioRangeInScope() {
		return audioRangeInScope;
	}

}