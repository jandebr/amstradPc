package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Point;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.graphics2d.image.ImageUtils;

public abstract class CarouselPortholePixelatedAnimation extends CarouselPortholeStartupAnimation {

	private int pixelSize;

	protected CarouselPortholePixelatedAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setPixelSize(derivePixelSize());
		getPanorama(); // create upfront since it may involve pixelating images
	}

	protected int derivePixelSize() {
		return Math.max(1, Math.round(getPortholeWidth() / (float) getTargetPixelWidth()));
	}

	protected int getTargetPixelWidth() {
		return 96;
	}

	protected BufferedImage loadPixelatedImage(String resourceName) {
		return pixelate(UIResources.loadImage(resourceName));
	}

	protected BufferedImage pixelate(BufferedImage image) {
		return ImageUtils.pixelate(image, getPixelSize());
	}

	@Override
	protected Point projectLandscapeCoordinateToView(Point coord) {
		int ps = getPixelSize();
		Point p = super.projectLandscapeCoordinateToView(coord);
		if (p != null && ps != 1) {
			return new Point(p.x / ps, p.y / ps);
		} else {
			return p;
		}
	}

	protected int getPortholePixelWidth() {
		return Math.round(getPortholeWidth() / (float) getPixelSize());
	}

	protected int getPortholePixelHeight() {
		return Math.round(getPortholeHeight() / (float) getPixelSize());
	}

	protected int getPixelSize() {
		return pixelSize;
	}

	private void setPixelSize(int pixelSize) {
		this.pixelSize = pixelSize;
	}

}