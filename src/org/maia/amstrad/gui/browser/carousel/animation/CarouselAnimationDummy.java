package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;

public class CarouselAnimationDummy extends CarouselAnimation {

	private Rectangle itemBounds;

	public CarouselAnimationDummy(CarouselHost host, Rectangle itemBounds) {
		super(host);
		this.itemBounds = itemBounds;
	}

	@Override
	public void renderOntoDisplay(Graphics2D displayGraphics, int displayWidth, int displayHeight,
			long elapsedTimeMillis) {
		if (elapsedTimeMillis % 200L < 100L) {
			Rectangle bounds = getItemBounds();
			if (bounds != null) {
				displayGraphics.setColor(Color.RED);
				displayGraphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
			}
		}
	}

	private Rectangle getItemBounds() {
		return itemBounds;
	}

}