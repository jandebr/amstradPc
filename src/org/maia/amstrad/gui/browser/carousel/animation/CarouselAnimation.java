package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Graphics2D;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;

public abstract class CarouselAnimation {

	private CarouselHost host;

	protected CarouselAnimation(CarouselHost host) {
		this.host = host;
	}

	public void start() {
		// Subclasses may extend
	}

	public abstract void renderOntoDisplay(Graphics2D displayGraphics, int displayWidth, int displayHeight,
			long elapsedTimeMillis);

	public void stop() {
		// Subclasses may extend
	}

	protected CarouselHost getHost() {
		return host;
	}

}