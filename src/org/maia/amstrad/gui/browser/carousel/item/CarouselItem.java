package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.swing.animate.itemslide.SlidingItem;

public abstract class CarouselItem implements SlidingItem {

	private CarouselComponent carouselComponent;

	private Dimension size;

	private Insets margin;

	protected CarouselItem(CarouselComponent carouselComponent, Dimension size, Insets margin) {
		this.carouselComponent = carouselComponent;
		this.size = size;
		this.margin = margin;
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

}