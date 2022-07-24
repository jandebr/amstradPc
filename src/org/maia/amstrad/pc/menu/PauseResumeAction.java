package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;

public class PauseResumeAction extends AmstradPcAction implements AmstradPcStateListener {

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

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc,
			AmstradAlternativeDisplaySource alternativeDisplaySource) {
	}

	private void updateName() {
		if (getAmstradPc().isPaused()) {
			changeName(NAME_RESUME);
		} else {
			changeName(NAME_PAUSE);
		}
	}

}