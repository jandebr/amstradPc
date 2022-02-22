package org.maia.amstrad.jemu.menu;

import java.awt.event.ActionEvent;

import org.maia.amstrad.jemu.AmstradPc;
import org.maia.amstrad.jemu.AmstradPcStateListener;

public class PauseResumeAction extends AmstradPcAction implements AmstradPcStateListener {

	public static String NAME_PAUSE = "Pause";

	public static String NAME_RESUME = "Resume";

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
	public void amstradPcStarted(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		updateName();
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		updateName();
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
	}

	private void updateName() {
		if (getAmstradPc().isPaused()) {
			changeName(NAME_RESUME);
		} else {
			changeName(NAME_PAUSE);
		}
	}

}