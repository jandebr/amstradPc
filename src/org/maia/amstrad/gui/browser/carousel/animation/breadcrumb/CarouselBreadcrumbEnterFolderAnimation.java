package org.maia.amstrad.gui.browser.carousel.animation.breadcrumb;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItem;

public class CarouselBreadcrumbEnterFolderAnimation extends CarouselBreadcrumbItemAnimation {

	public static Color itemBoundsColor = new Color(249, 212, 37);

	public CarouselBreadcrumbEnterFolderAnimation(CarouselBreadcrumbItem item, Rectangle itemBreadcrumbBounds) {
		super(item, itemBreadcrumbBounds);
	}

	@Override
	public void renderOntoDisplay(Graphics2D displayGraphics, int displayWidth, int displayHeight,
			long elapsedTimeMillis) {
		displayGraphics.setColor(itemBoundsColor);
		renderItemBoundsBlinking(displayGraphics, elapsedTimeMillis);
	}

}