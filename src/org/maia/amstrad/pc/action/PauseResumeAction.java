package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;

public class PauseResumeAction extends AmstradPcAction {

	private static String NAME_PAUSE = "Pause";

	private static String NAME_RESUME = "Resume";

	public PauseResumeAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addStateListener(this);
		amstradPc.addEventListener(this);
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
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (event instanceof AmstradPcKeyboardEvent) {
			AmstradPcKeyboardEvent keyEvent = (AmstradPcKeyboardEvent) event;
			if (keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_PAUSE) {
				togglePauseResume();
			}
		}
	}

	private void updateName() {
		if (getAmstradPc().isPaused()) {
			changeName(NAME_RESUME);
		} else {
			changeName(NAME_PAUSE);
		}
	}

	private void togglePauseResume() {
		if (getAmstradPc().isPaused()) {
			getAmstradPc().resume();
		} else {
			getAmstradPc().pause();
		}
	}

}