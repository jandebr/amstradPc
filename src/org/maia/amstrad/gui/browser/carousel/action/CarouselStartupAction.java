package org.maia.amstrad.gui.browser.carousel.action;

import org.maia.amstrad.gui.browser.carousel.animation.CarouselAnimation;
import org.maia.amstrad.gui.browser.carousel.api.CarouselStartupHost;

public class CarouselStartupAction extends CarouselAction {

	public CarouselStartupAction(CarouselStartupHost host, CarouselAnimation animation) {
		super(host, animation);
	}

	@Override
	protected void doPerform() {
		getHost().pauseBuildingUI();
	}

	@Override
	public synchronized void stopAnimation() {
		super.stopAnimation();
		getHost().resumeBuildingUI();
	}

	@Override
	protected CarouselStartupHost getHost() {
		return (CarouselStartupHost) super.getHost();
	}

}