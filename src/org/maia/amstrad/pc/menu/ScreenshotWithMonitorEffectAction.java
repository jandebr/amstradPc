package org.maia.amstrad.pc.menu;

import javax.swing.Icon;

import org.maia.amstrad.pc.AmstradPc;

public class ScreenshotWithMonitorEffectAction extends ScreenshotAction {

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc) {
		this(amstradPc, "Save monitor image...");
	}

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	protected boolean includeMonitorEffect() {
		return true;
	}

}