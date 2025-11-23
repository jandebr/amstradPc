package org.maia.amstrad.gui.browser.carousel.action;

import org.maia.amstrad.gui.browser.carousel.animation.CarouselAnimation;
import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;
import org.maia.util.SystemUtils;

public abstract class CarouselAction {

	private CarouselHost host;

	private CarouselAnimation animation;

	private boolean animationSuppressed;

	private long animationStartTimeMillis;

	private long actionStartTimeMillis;

	protected CarouselAction(CarouselHost host, CarouselAnimation animation) {
		this.host = host;
		this.animation = animation;
	}

	public final void perform() {
		setActionStartTimeMillis(System.currentTimeMillis());
		doPerform();
	}

	protected abstract void doPerform();

	public synchronized void startAnimationWhenAppropriate() {
		if (!isAnimationStarted() && !isAnimationSuppressed() && isPassedAnimationDelay() && getAnimation() != null) {
			setAnimationStartTimeMillis(System.currentTimeMillis());
			getAnimation().init();
		}
	}

	public synchronized void stopAnimation() {
		if (isAnimationStarted()) {
			setAnimationStartTimeMillis(0L);
			getAnimation().dispose();
		}
	}

	public synchronized void suppressAnimation() {
		stopAnimation();
		setAnimationSuppressed(true);
	}

	public void sleepCurrentThreadUntilMinimumAnimationDuration() {
		if (isAnimationStarted()) {
			SystemUtils.sleep(getMinimumAnimationDurationMillis() - getAnimationElapsedTimeMillis());
		}
	}

	public boolean isAnimationStarted() {
		return getAnimationStartTimeMillis() > 0L;
	}

	public boolean isPassedAnimationDelay() {
		return System.currentTimeMillis() >= getActionStartTimeMillis() + getMinimumAnimationDelayMillis();
	}

	public long getMinimumAnimationDelayMillis() {
		return getAnimation() != null ? getAnimation().getMinimumDelayMillis() : 0L;
	}

	public long getMinimumAnimationDurationMillis() {
		return getAnimation() != null ? getAnimation().getMinimumDurationMillis() : 0L;
	}

	public long getAnimationElapsedTimeMillis() {
		return System.currentTimeMillis() - getAnimationStartTimeMillis();
	}

	protected CarouselHost getHost() {
		return host;
	}

	public CarouselAnimation getAnimation() {
		return animation;
	}

	public boolean isAnimationSuppressed() {
		return animationSuppressed;
	}

	private void setAnimationSuppressed(boolean suppressed) {
		this.animationSuppressed = suppressed;
	}

	public long getAnimationStartTimeMillis() {
		return animationStartTimeMillis;
	}

	private void setAnimationStartTimeMillis(long ms) {
		this.animationStartTimeMillis = ms;
	}

	public long getActionStartTimeMillis() {
		return actionStartTimeMillis;
	}

	private void setActionStartTimeMillis(long ms) {
		this.actionStartTimeMillis = ms;
	}

}