package org.maia.amstrad.pc.action;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public class ScreenshotWithBorderAction extends ScreenshotAction {

	public ScreenshotWithBorderAction(AmstradPc amstradPc) {
		this(amstradPc, "Capture display...");
	}

	public ScreenshotWithBorderAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	protected boolean invokeOn(AmstradKeyboardEvent keyEvent) {
		return keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_I && keyEvent.isControlDown()
				&& !keyEvent.isShiftDown();
	}

	@Override
	protected BufferedImage captureImage() {
		return getAmstradPc().getMonitor().makeScreenshot(false);
	}

}