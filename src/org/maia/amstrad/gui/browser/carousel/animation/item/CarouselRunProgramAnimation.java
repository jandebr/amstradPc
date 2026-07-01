package org.maia.amstrad.gui.browser.carousel.animation.item;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;

public class CarouselRunProgramAnimation extends CarouselItemAnimation {

	public CarouselRunProgramAnimation(CarouselProgramItem item, Rectangle itemCarouselBounds) {
		super(item, itemCarouselBounds);
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		renderItemHighlightBlinking(g, elapsedTimeMillis);
	}

}