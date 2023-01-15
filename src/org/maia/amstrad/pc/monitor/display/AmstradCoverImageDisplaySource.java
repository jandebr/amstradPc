package org.maia.amstrad.pc.monitor.display;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.monitor.AmstradMonitor;

public class AmstradCoverImageDisplaySource implements AmstradAlternativeDisplaySource {

	private Image coverImage;

	public AmstradCoverImageDisplaySource(Image coverImage) {
		setCoverImage(coverImage);
	}

	public static AmstradCoverImageDisplaySource createFreezeFrame(AmstradMonitor monitor) {
		return new AmstradCoverImageDisplaySource(monitor.makeScreenshot(false));
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext,
			AmstradKeyboardController keyboardController) {
		// no action
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		display.drawImage(getCoverImage(), displayBounds.x, displayBounds.y, displayBounds.width, displayBounds.height,
				null);
	}

	@Override
	public void dispose(JComponent displayComponent) {
		// no action
	}

	@Override
	public boolean isRestoreMonitorSettingsOnDispose() {
		return false;
	}

	public Image getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(Image coverImage) {
		this.coverImage = coverImage;
	}

}