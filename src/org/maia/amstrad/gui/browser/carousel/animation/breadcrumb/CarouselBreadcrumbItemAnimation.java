package org.maia.amstrad.gui.browser.carousel.animation.breadcrumb;

import java.awt.Graphics2D;
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

	protected void renderItemBoundsBlinking(Graphics2D g, long elapsedTimeMillis) {
		if (elapsedTimeMillis % 400L < 200L) {
			renderItemBounds(g);
		}
	}

	protected void renderItemBounds(Graphics2D g) {
		Rectangle bounds = getItemBreadcrumbBounds();
		if (bounds != null) {
			g.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 2, bounds.height + 2);
			g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	protected CarouselBreadcrumbItem getItem() {
		return item;
	}

	protected Rectangle getItemBreadcrumbBounds() {
		return itemBreadcrumbBounds;
	}

}