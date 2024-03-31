package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcPerformanceListener;

public class TurboAction extends AmstradPcAction implements AmstradPcPerformanceListener {

	private static String NAME_NORMAL_SPEED = "Normal speed";

	private static String NAME_TURBO_SPEED = "Turbo";

	public TurboAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		amstradPc.addPerformanceListener(this);
		updateName();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleTurbo();
	}

	@Override
	public void displayPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int framesPainted,
			int imagesUpdated) {
		// no action
	}

	@Override
	public void processorPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int timerSyncs,
			int laggingSyncs, int throttledSyncs) {
		// no action
	}

	@Override
	public void turboModeChanged(AmstradPc amstradPc) {
		updateName();
	}

	private void updateName() {
		if (isTurbo()) {
			changeName(NAME_NORMAL_SPEED);
		} else {
			changeName(NAME_TURBO_SPEED);
		}
	}

	private void toggleTurbo() {
		getAmstradPc().setTurboMode(!isTurbo());
	}

	public boolean isTurbo() {
		return getAmstradPc().isTurboMode();
	}

}