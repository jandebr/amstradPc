package org.maia.amstrad.pc.audio;

public abstract class AmstradAudioAdapter implements AmstradAudioListener {

	protected AmstradAudioAdapter() {
	}

	@Override
	public void amstradAudioMuted(AmstradAudio audio) {
		// Subclasses can override this
	}

	@Override
	public void amstradAudioUnmuted(AmstradAudio audio) {
		// Subclasses can override this
	}

}