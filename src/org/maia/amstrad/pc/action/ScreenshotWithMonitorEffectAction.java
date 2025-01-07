package org.maia.amstrad.pc.action;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public class ScreenshotWithMonitorEffectAction extends ScreenshotAction {

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc) {
		this(amstradPc, "Capture monitor...");
	}

	public ScreenshotWithMonitorEffectAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	protected boolean invokeOn(AmstradKeyboardEvent keyEvent) {
		return keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_J && keyEvent.isControlDown()
				&& !keyEvent.isShiftDown();
	}

	@Override
	protected BufferedImage captureImage() {
		return getAmstradPc().getMonitor().makeScreenshot(true);
	}

}