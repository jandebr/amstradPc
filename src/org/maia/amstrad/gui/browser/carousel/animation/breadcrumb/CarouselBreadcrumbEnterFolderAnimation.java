package org.maia.amstrad.gui.browser.carousel.animation.breadcrumb;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItem;

public class CarouselBreadcrumbEnterFolderAnimation extends CarouselBreadcrumbItemAnimation {

	private Color boundsColor;

	public CarouselBreadcrumbEnterFolderAnimation(CarouselBreadcrumbItem item, Rectangle itemBreadcrumbBounds,
			Color boundsColor) {
		super(item, itemBreadcrumbBounds);
		this.boundsColor = boundsColor;
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		if (elapsedTimeMillis % 400L < 200L) {
			g.setColor(getBoundsColor());
		} else {
			g.setColor(getItem().getCarouselBreadcrumb().getBackground());
		}
		renderItemBounds(g);
	}

	protected void renderItemBounds(Graphics2D g) {
		Rectangle bounds = getItemBreadcrumbBounds();
		if (bounds != null) {
			g.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 2, bounds.height + 2);
			g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	public Color getBoundsColor() {
		return boundsColor;
	}

}