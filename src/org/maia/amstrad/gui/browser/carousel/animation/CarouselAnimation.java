package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Graphics2D;

public abstract class CarouselAnimation {

	private long minimumDelayMillis;

	private long minimumDurationMillis;

	protected CarouselAnimation() {
	}

	public void init() {
		// Subclasses may extend
	}

	public abstract void renderOntoDisplay(Graphics2D displayGraphics, int displayWidth, int displayHeight,
			long elapsedTimeMillis);

	public void dispose() {
		// Subclasses may extend
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