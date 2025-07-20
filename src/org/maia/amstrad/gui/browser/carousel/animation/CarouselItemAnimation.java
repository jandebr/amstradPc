package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;

public abstract class CarouselItemAnimation extends CarouselAnimation {

	private Rectangle itemBounds;

	protected CarouselItemAnimation(CarouselHost host, Rectangle itemBounds) {
		super(host);
		this.itemBounds = itemBounds;
	}

	protected Rectangle getItemBounds() {
		return itemBounds;
	}

}