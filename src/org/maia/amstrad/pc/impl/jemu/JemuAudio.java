package org.maia.amstrad.pc.impl.jemu;

import org.maia.amstrad.pc.audio.AmstradAudio;

import jemu.settings.Settings;
import jemu.ui.Switches;

public class JemuAudio extends AmstradAudio {

	public JemuAudio(JemuAmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public void mute() {
		Switches.audioenabler = 0;
		Settings.setBoolean(Settings.AUDIO, false);
		fireAudioMutedEvent();
	}

	@Override
	public void unmute() {
		Switches.audioenabler = 1;
		Settings.setBoolean(Settings.AUDIO, true);
		fireAudioUnmutedEvent();
	}

	@Override
	public boolean isMuted() {
		return !Settings.getBoolean(Settings.AUDIO, true);
	}

}