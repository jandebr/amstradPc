package org.maia.amstrad.jemu.menu;

import java.awt.event.ActionEvent;

import org.maia.amstrad.jemu.AmstradPc;

public class PauseResumeAction extends AmstradPcAction {

	public static String NAME_PAUSE = "Pause";

	public static String NAME_RESUME = "Resume";

	public PauseResumeAction(AmstradPc amstradPc) {
		super(amstradPc, amstradPc.isPaused() ? NAME_RESUME : NAME_PAUSE);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (getAmstradPc().isPaused()) {
			getAmstradPc().resume();
			changeName(NAME_PAUSE);
		} else {
			getAmstradPc().pause();
			changeName(NAME_RESUME);
		}
	}

}