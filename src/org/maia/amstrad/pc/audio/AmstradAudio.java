package org.maia.amstrad.pc.audio;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradDevice;
import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradAudio extends AmstradDevice {

	private List<AmstradAudioListener> audioListeners;

	public AmstradAudio(AmstradPc amstradPc) {
		super(amstradPc);
		this.audioListeners = new Vector<AmstradAudioListener>();
	}

	public abstract void mute();

	public abstract void unmute();

	public abstract boolean isMuted();

	public void addAudioListener(AmstradAudioListener listener) {
		getAudioListeners().add(listener);
	}

	public void removeAudioListener(AmstradAudioListener listener) {
		getAudioListeners().remove(listener);
	}

	protected void fireAudioMutedEvent() {
		for (AmstradAudioListener listener : getAudioListeners())
			listener.amstradAudioMuted(this);
	}

	protected void fireAudioUnmutedEvent() {
		for (AmstradAudioListener listener : getAudioListeners())
			listener.amstradAudioUnmuted(this);
	}

	protected List<AmstradAudioListener> getAudioListeners() {
		return audioListeners;
	}

}