package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public class PauseResumeAction extends AmstradPcAction {

	private static String NAME_PAUSE = "Pause";

	private static String NAME_RESUME = "Resume";

	public PauseResumeAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addStateListener(this);
		amstradPc.getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		togglePauseResume();
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		super.amstradPcPausing(amstradPc);
		updateName();
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		super.amstradPcResuming(amstradPc);
		updateName();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (!isTriggeredByMenuKeyBindings()) {
			if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_PAUSE) {
				togglePauseResume();
			}
		}
	}

	private void updateName() {
		if (isPaused()) {
			changeName(NAME_RESUME);
		} else {
			changeName(NAME_PAUSE);
		}
	}

	private void togglePauseResume() {
		if (isPaused()) {
			getAmstradPc().resume();
		} else {
			getAmstradPc().pause();
		}
	}

	public boolean isPaused() {
		return getAmstradPc().isPaused();
	}

}