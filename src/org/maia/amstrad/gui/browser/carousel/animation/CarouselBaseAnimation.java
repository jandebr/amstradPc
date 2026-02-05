package org.maia.amstrad.gui.browser.carousel.animation;

public abstract class CarouselBaseAnimation implements CarouselAnimation {

	private long minimumDelayMillis;

	private long minimumDurationMillis;

	protected CarouselBaseAnimation() {
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		// Subclasses may extend
	}

	@Override
	public void dispose() {
		// Subclasses may extend
	}

	@Override
	public long getMinimumDelayMillis() {
		return minimumDelayMillis;
	}

	public void setMinimumDelayMillis(long delayMillis) {
		this.minimumDelayMillis = delayMillis;
	}

	@Override
	public long getMinimumDurationMillis() {
		return minimumDurationMillis;
	}

	public void setMinimumDurationMillis(long durationMillis) {
		this.minimumDurationMillis = durationMillis;
	}

}