package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;

public class CarouselProgramAnimation extends CarouselItemAnimation {

	public CarouselProgramAnimation(CarouselProgramItem item, Rectangle itemBounds) {
		super(item, itemBounds);
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		if (elapsedTimeMillis % 200L < 100L) {
			Rectangle bounds = getItemBounds();
			if (bounds != null) {
				g.setColor(Color.GREEN);
				g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
			}
		}
	}

}