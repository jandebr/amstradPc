package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.audio.AmstradAudio;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public class AudioAction extends AmstradPcAction {

	private static String NAME_MUTE = "Audio off";

	private static String NAME_UNMUTE = "Audio on";

	public AudioAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.getAudio().addAudioListener(this);
		amstradPc.getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleAudio();
	}

	@Override
	public void amstradAudioMuted(AmstradAudio audio) {
		super.amstradAudioMuted(audio);
		updateName();
	}

	@Override
	public void amstradAudioUnmuted(AmstradAudio audio) {
		super.amstradAudioUnmuted(audio);
		updateName();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_A && event.isControlDown()
				&& !event.isShiftDown()) {
			toggleAudio();
		}
	}

	private void updateName() {
		if (isMuted()) {
			changeName(NAME_UNMUTE);
		} else {
			changeName(NAME_MUTE);
		}
	}

	private void toggleAudio() {
		if (isMuted()) {
			getAmstradPc().getAudio().unmute();
		} else {
			getAmstradPc().getAudio().mute();
		}
	}

	public boolean isMuted() {
		return getAmstradPc().getAudio().isMuted();
	}

}