package org.maia.amstrad.gui.browser.carousel.animation.item;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;

public class CarouselHighlightItemAnimation extends CarouselItemAnimation {

	private long highlightDurationMillis = 1000L; // 1s

	private long highlightMinimumDelayMillis = 5000L; // 5s

	private long highlightMaximumDelayMillis = 10000L; // 10s

	private long highlightNextStartTimeMillis; // in elapsed time

	private Shape highlightOverlayClip;

	private int highlightOverlayClipHeight;

	public CarouselHighlightItemAnimation(CarouselItem item, Rectangle itemCarouselBounds) {
		super(item, itemCarouselBounds);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		if (getItemCarouselBounds() != null) {
			setHighlightOverlayClip(createHighlightOverlayClip(getItemCarouselBounds()));
			setHighlightOverlayClipHeight(getHighlightOverlayClip().getBounds().height);
		}
		defineNextStartTime(0L);
	}

	protected Shape createHighlightOverlayClip(Rectangle itemBounds) {
		int cw = itemBounds.width;
		int ch = itemBounds.height / 2;
		int y0 = cw / 2;
		Polygon polygon = new Polygon();
		polygon.addPoint(0, 0);
		polygon.addPoint(cw, y0);
		polygon.addPoint(cw, y0 + ch);
		polygon.addPoint(0, ch);
		return polygon;
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		long startTime = getHighlightNextStartTimeMillis();
		if (elapsedTimeMillis >= startTime) {
			long highlightTime = elapsedTimeMillis - startTime;
			long highlightDuration = getHighlightDurationMillis();
			if (highlightTime <= highlightDuration) {
				float highlightTimeFraction = highlightTime / (float) highlightDuration;
				renderHighlight(g, highlightTimeFraction);
			} else {
				defineNextStartTime(elapsedTimeMillis);
			}
		}
	}

	protected void renderHighlight(Graphics2D g, float timeFraction) {
		Rectangle bounds = getItemCarouselBounds();
		Image overlay = getItemHighlightOverlayImage();
		if (bounds != null && overlay != null) {
			Shape clip = getHighlightOverlayClip();
			int clipHeight = getHighlightOverlayClipHeight();
			int dy = Math.round(timeFraction * bounds.height + (timeFraction - 1.0f) * (2 * clipHeight));
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(bounds.x, bounds.y + dy);
			g2.setClip(clip);
			g2.drawImage(overlay, 0, -dy, null);
			g2.dispose();
		}
	}

	private void defineNextStartTime(long elapsedTimeMillis) {
		long min = getHighlightMinimumDelayMillis();
		long max = getHighlightMaximumDelayMillis();
		long delay = Math.round(min + (max - min) * Math.random());
		setHighlightNextStartTimeMillis(elapsedTimeMillis + delay);
	}

	public long getHighlightDurationMillis() {
		return highlightDurationMillis;
	}

	public void setHighlightDurationMillis(long durationMillis) {
		this.highlightDurationMillis = durationMillis;
	}

	public long getHighlightMinimumDelayMillis() {
		return highlightMinimumDelayMillis;
	}

	public void setHighlightMinimumDelayMillis(long delayMillis) {
		this.highlightMinimumDelayMillis = delayMillis;
	}

	public long getHighlightMaximumDelayMillis() {
		return highlightMaximumDelayMillis;
	}

	public void setHighlightMaximumDelayMillis(long delayMillis) {
		this.highlightMaximumDelayMillis = delayMillis;
	}

	private long getHighlightNextStartTimeMillis() {
		return highlightNextStartTimeMillis;
	}

	private void setHighlightNextStartTimeMillis(long timeMillis) {
		this.highlightNextStartTimeMillis = timeMillis;
	}

	private Shape getHighlightOverlayClip() {
		return highlightOverlayClip;
	}

	private void setHighlightOverlayClip(Shape clip) {
		this.highlightOverlayClip = clip;
	}

	private int getHighlightOverlayClipHeight() {
		return highlightOverlayClipHeight;
	}

	private void setHighlightOverlayClipHeight(int height) {
		this.highlightOverlayClipHeight = height;
	}

}