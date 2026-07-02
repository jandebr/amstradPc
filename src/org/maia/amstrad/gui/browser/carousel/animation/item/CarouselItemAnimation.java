package org.maia.amstrad.gui.browser.carousel.animation.item;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.animation.CarouselBaseAnimation;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;

public abstract class CarouselItemAnimation extends CarouselBaseAnimation {

	private CarouselItem item;

	private Rectangle itemCarouselBounds;

	private Image itemHighlightOverlayImage; // nullable

	protected CarouselItemAnimation(CarouselItem item, Rectangle itemCarouselBounds) {
		this.item = item;
		this.itemCarouselBounds = itemCarouselBounds;
		this.itemHighlightOverlayImage = item.getHighlightOverlayImage();
	}

	protected void renderItemHighlightBlinking(Graphics2D g, long elapsedTimeMillis) {
		if (elapsedTimeMillis % 400L < 200L) {
			renderItemHighlight(g);
		}
	}

	protected void renderItemHighlight(Graphics2D g) {
		Rectangle bounds = getItemCarouselBounds();
		if (bounds != null) {
			Image overlay = getItemHighlightOverlayImage();
			if (overlay != null) {
				g.drawImage(overlay, bounds.x, bounds.y, null);
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		Image overlay = getItemHighlightOverlayImage();
		if (overlay != null) {
			overlay.flush();
		}
	}

	protected CarouselItem getItem() {
		return item;
	}

	protected Rectangle getItemCarouselBounds() {
		return itemCarouselBounds;
	}

	protected Image getItemHighlightOverlayImage() {
		return itemHighlightOverlayImage;
	}

}