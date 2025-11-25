package org.maia.amstrad.gui.browser.carousel.action;

import org.maia.amstrad.gui.browser.carousel.animation.CarouselAnimation;
import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;

public class CarouselItemHighlightAction extends CarouselAction {

	public CarouselItemHighlightAction(CarouselHost host, CarouselAnimation animation) {
		super(host, animation);
	}

	@Override
	protected void doPerform() {
		// this action is merely a wrapper around its animation
	}

}