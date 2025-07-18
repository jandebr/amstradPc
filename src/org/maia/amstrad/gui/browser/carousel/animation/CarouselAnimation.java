package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Graphics2D;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;

public abstract class CarouselAnimation {

	private CarouselHost host;

	private long minimumDelayMillis;

	private long minimumDurationMillis;

	protected CarouselAnimation(CarouselHost host) {
		this.host = host;
	}

	public void init() {
		// Subclasses may extend
	}

	public abstract void renderOntoDisplay(Graphics2D displayGraphics, int displayWidth, int displayHeight,
			long elapsedTimeMillis);

	public void dispose() {
		// Subclasses may extend
	}

	protected CarouselHost getHost() {
		return host;
	}

	public long getMinimumDelayMillis() {
		return minimumDelayMillis;
	}

	public void setMinimumDelayMillis(long delayMillis) {
		this.minimumDelayMillis = delayMillis;
	}

	public long getMinimumDurationMillis() {
		return minimumDurationMillis;
	}

	public void setMinimumDurationMillis(long durationMillis) {
		this.minimumDurationMillis = durationMillis;
	}

}