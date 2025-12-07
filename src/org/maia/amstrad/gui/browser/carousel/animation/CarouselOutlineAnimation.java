package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Graphics2D;

import org.maia.amstrad.gui.browser.carousel.api.CarouselOutlineHost;

public class CarouselOutlineAnimation extends CarouselAnimation {

	private CarouselOutlineHost host;

	public CarouselOutlineAnimation(CarouselOutlineHost host) {
		this.host = host;
	}

	@Override
	public void init() {
		super.init();
		getHost().showCarouselOutline();
	}

	@Override
	public void dispose() {
		super.dispose();
		getHost().hideCarouselOutline();
	}

	@Override
	public void renderOntoDisplay(Graphics2D displayGraphics, int displayWidth, int displayHeight,
			long elapsedTimeMillis) {
		// the outline component does the rendering
	}

	private CarouselOutlineHost getHost() {
		return host;
	}

}