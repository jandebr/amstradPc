package org.maia.amstrad.pc.menu;

import org.maia.amstrad.pc.AmstradPc;

public class ScreenshotWithMonitorEffectAction extends ScreenshotAction {

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc) {
		this(amstradPc, "Capture monitor image...");
	}

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	protected boolean includeMonitorEffect() {
		return true;
	}

}