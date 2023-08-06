package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class AmstradImageDisplaySource implements AmstradAlternativeDisplaySource {

	private Image image;

	public AmstradImageDisplaySource(Image image) {
		setImage(image);
	}

	public static AmstradImageDisplaySource createFreezeFrame(AmstradMonitor monitor) {
		return new AmstradImageDisplaySource(monitor.makeScreenshot(false));
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext,
			AmstradKeyboardController keyboardController) {
		// no action
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		display.drawImage(getImage(), displayBounds.x, displayBounds.y, displayBounds.width, displayBounds.height,
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

	@Override
	public AmstradAlternativeDisplaySourceType getType() {
		return AmstradAlternativeDisplaySourceType.IMAGE;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

}