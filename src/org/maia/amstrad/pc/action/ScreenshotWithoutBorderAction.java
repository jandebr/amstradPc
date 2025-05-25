package org.maia.amstrad.pc.action;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.graphics2d.image.ImageUtils;

public class ScreenshotWithoutBorderAction extends ScreenshotAction {

	public ScreenshotWithoutBorderAction(AmstradPc amstradPc) {
		this(amstradPc, "Capture graphics area...");
	}

	public ScreenshotWithoutBorderAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	protected boolean invokeOn(AmstradKeyboardEvent keyEvent) {
		return keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_J && keyEvent.isControlDown()
				&& !keyEvent.isShiftDown();
	}

	@Override
	protected BufferedImage captureImage() {
		BufferedImage image = getAmstradPc().getMonitor().makeScreenshot(false);
		Insets borderInsets = getBorderInsets(ImageUtils.getSize(image));
		return ImageUtils.cropSides(image, borderInsets);
	}

	private Insets getBorderInsets(Dimension frameSize) {
		double frameWidth = frameSize.getWidth();
		double frameHeight = frameSize.getHeight();
		int left = (int) Math.round(64.0 / 768 * frameWidth);
		int right = left;
		int top = (int) Math.round(80.0 / 544 * frameHeight);
		int bottom = (int) Math.round(64.0 / 544 * frameHeight);
		return new Insets(top, left, bottom, right);
	}

}