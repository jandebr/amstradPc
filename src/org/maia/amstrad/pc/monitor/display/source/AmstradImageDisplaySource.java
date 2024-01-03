package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class AmstradImageDisplaySource extends AmstradAbstractDisplaySource {

	private Image image;

	public AmstradImageDisplaySource(AmstradPc amstradPc, Image image) {
		super(amstradPc);
		setImage(image);
	}

	public static AmstradImageDisplaySource createFreezeFrame(AmstradMonitor monitor) {
		return new AmstradImageDisplaySource(monitor.getAmstradPc(), monitor.makeScreenshot(false));
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		display.drawImage(getImage(), displayBounds.x, displayBounds.y, displayBounds.width, displayBounds.height,
				null);
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