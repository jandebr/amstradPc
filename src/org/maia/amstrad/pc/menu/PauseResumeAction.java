package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class PauseResumeAction extends AmstradPcAction {

	private static String NAME_PAUSE = "Pause";

	private static String NAME_RESUME = "Resume";

	public PauseResumeAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addStateListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (getAmstradPc().isPaused()) {
			getAmstradPc().resume();
		} else {
			getAmstradPc().pause();
		}
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

	private void updateName() {
		if (getAmstradPc().isPaused()) {
			changeName(NAME_RESUME);
		} else {
			changeName(NAME_PAUSE);
		}
	}

}