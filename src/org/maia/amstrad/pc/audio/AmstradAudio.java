package org.maia.amstrad.pc.audio;

import org.maia.amstrad.pc.AmstradPcDevice;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.util.GenericListenerList;

public abstract class AmstradAudio extends AmstradPcDevice {

	private GenericListenerList<AmstradAudioListener> audioListeners;

	protected AmstradAudio(AmstradPc amstradPc) {
		super(amstradPc);
		this.audioListeners = new GenericListenerList<AmstradAudioListener>();
	}

	public abstract void mute();

	public abstract void unmute();

	public abstract boolean isMuted();

	public void addAudioListener(AmstradAudioListener listener) {
		getAudioListeners().addListener(listener);
	}

	public void removeAudioListener(AmstradAudioListener listener) {
		getAudioListeners().removeListener(listener);
	}

	protected void fireAudioMutedEvent() {
		for (AmstradAudioListener listener : getAudioListeners())
			listener.amstradAudioMuted(this);
	}

	protected void fireAudioUnmutedEvent() {
		for (AmstradAudioListener listener : getAudioListeners())
			listener.amstradAudioUnmuted(this);
	}

	protected GenericListenerList<AmstradAudioListener> getAudioListeners() {
		return audioListeners;
	}

}