package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;

public abstract class CarouselItemAnimation extends CarouselAnimation {

	private CarouselItem item;

	private Rectangle itemBounds;

	protected CarouselItemAnimation(CarouselItem item, Rectangle itemBounds) {
		this.item = item;
		this.itemBounds = itemBounds;
	}

	protected CarouselItem getItem() {
		return item;
	}

	protected Rectangle getItemBounds() {
		return itemBounds;
	}

}