package org.maia.amstrad.pc.action;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public class ScreenshotWithMonitorEffectAction extends ScreenshotAction {

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc) {
		this(amstradPc, "Capture monitor image...");
	}

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	protected boolean invokeOn(AmstradKeyboardEvent keyEvent) {
		return keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_I && keyEvent.isControlDown()
				&& keyEvent.isShiftDown();
	}

	@Override
	protected boolean includeMonitorEffect() {
		return true;
	}

}