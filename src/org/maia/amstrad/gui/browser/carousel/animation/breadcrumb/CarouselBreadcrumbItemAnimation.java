package org.maia.amstrad.gui.browser.carousel.animation.breadcrumb;

import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.animation.CarouselBaseAnimation;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItem;

public abstract class CarouselBreadcrumbItemAnimation extends CarouselBaseAnimation {

	private CarouselBreadcrumbItem item;

	private Rectangle itemBreadcrumbBounds; // nullable

	protected CarouselBreadcrumbItemAnimation(CarouselBreadcrumbItem item, Rectangle itemBreadcrumbBounds) {
		this.item = item;
		this.itemBreadcrumbBounds = itemBreadcrumbBounds;
	}

	protected CarouselBreadcrumbItem getItem() {
		return item;
	}

	protected Rectangle getItemBreadcrumbBounds() {
		return itemBreadcrumbBounds;
	}

}