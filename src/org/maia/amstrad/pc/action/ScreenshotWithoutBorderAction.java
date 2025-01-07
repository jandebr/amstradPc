package org.maia.amstrad.pc.action;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.swing.util.ImageUtils;

public class ScreenshotWithoutBorderAction extends ScreenshotAction {

	public ScreenshotWithoutBorderAction(AmstradPc amstradPc) {
		this(amstradPc, "Capture frame borderless...");
	}

	public ScreenshotWithoutBorderAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	protected boolean invokeOn(AmstradKeyboardEvent keyEvent) {
		return keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_I && keyEvent.isControlDown()
				&& keyEvent.isShiftDown();
	}

	@Override
	protected BufferedImage captureImage() {
		BufferedImage image = super.captureImage();
		Insets borderInsets = getBorderInsets(ImageUtils.getSize(image));
		return ImageUtils.cropSides(image, borderInsets);
	}

	private Insets getBorderInsets(Dimension frameSize) {
		double frameWidth = frameSize.getWidth();
		double frameHeight = frameSize.getHeight();
		int left = (int) Math.round(64.0 / 768 * frameWidth);
		int right = left;
		int top = (int) Math.round(79.6 / 544 * frameHeight);
		int bottom = (int) Math.round(64.0 / 544 * frameHeight);
		return new Insets(top, left, bottom, right);
	}

}