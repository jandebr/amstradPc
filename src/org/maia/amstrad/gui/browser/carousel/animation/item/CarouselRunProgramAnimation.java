package org.maia.amstrad.gui.browser.carousel.animation.item;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;

public class CarouselRunProgramAnimation extends CarouselItemAnimation {

	private Rectangle itemLeadingBounds;

	public CarouselRunProgramAnimation(CarouselProgramItem item, Rectangle itemCarouselBounds,
			Rectangle itemLeadingBounds) {
		super(item, itemCarouselBounds);
		this.itemLeadingBounds = itemLeadingBounds;
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		renderItemHighlightBlinking(g, elapsedTimeMillis);
		renderLeadingAnimation(g, elapsedTimeMillis);
	}

	protected void renderLeadingAnimation(Graphics2D g, long elapsedTimeMillis) {
		Rectangle bounds = getItemLeadingBounds();
		if (bounds != null) {
			float r = Math.min(elapsedTimeMillis / 1800f, 1f);
			int w = Math.round(r * bounds.width);
			g.setColor(Color.WHITE);
			g.fillRect(bounds.x, bounds.y, w, bounds.height);
			g.setColor(Color.DARK_GRAY);
			g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	protected Rectangle getItemLeadingBounds() {
		return itemLeadingBounds;
	}

}