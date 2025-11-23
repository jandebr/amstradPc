package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;
import org.maia.swing.animate.itemslide.SlidingItem;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;

public abstract class CarouselItem implements SlidingItem {

	private CarouselComponent carouselComponent;

	private Dimension size;

	private Insets margin;

	protected CarouselItem(CarouselComponent carouselComponent, Dimension size, Insets margin) {
		this.carouselComponent = carouselComponent;
		this.size = size;
		this.margin = margin;
	}

	protected void paintBackground(Graphics2D g, SlidingItemListComponent component) {
		g.setColor(component.getBackground());
		g.fillRect(0, 0, getWidth(g), getHeight(g));
	}

	public final void execute(CarouselHost host) {
		if (isExecutable()) {
			doExecute(host);
		}
	}

	protected void doExecute(CarouselHost host) {
		// default implementation does nothing
	}

	public abstract boolean isExecutable();

	@Override
	public int getWidth(Graphics2D g) {
		return getSize().width;
	}

	@Override
	public int getHeight(Graphics2D g) {
		return getSize().height;
	}

	public Dimension getSize() {
		return size;
	}

	@Override
	public Insets getMargin() {
		return margin;
	}

	protected CarouselComponent getCarouselComponent() {
		return carouselComponent;
	}

	protected CarouselHost getCarouselHost() {
		return getCarouselComponent().getHost();
	}

}