package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Graphics2D;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;

public class CarouselAnimationDummy extends CarouselAnimation {

	public CarouselAnimationDummy(CarouselHost host) {
		super(host);
	}

	@Override
	public void start() {
		super.start();
		System.out.println("start animation");
	}

	@Override
	public void renderOntoDisplay(Graphics2D displayGraphics, int displayWidth, int displayHeight,
			long elapsedTimeMillis) {
		// System.out.println("animate " + elapsedTimeMillis);
	}

	@Override
	public void stop() {
		super.stop();
		System.out.println("stop animation");
	}

	@Override
	public long getMinimumDelayMillis() {
		return 400L;
	}

	@Override
	public long getMinimumDurationMillis() {
		return 1000L;
	}

}