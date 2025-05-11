package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.swing.layout.FillMode;
import org.maia.swing.animate.itemslide.SlidingItem;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.text.TextLabel;

public abstract class CarouselItem implements SlidingItem {

	private CarouselComponent carouselComponent;

	private Dimension size;

	private Insets margin;

	private Font font;

	private TextLabel label;

	protected CarouselItem(CarouselComponent carouselComponent, Dimension size, Insets margin, Font font) {
		this.carouselComponent = carouselComponent;
		this.size = size;
		this.margin = margin;
		this.font = font;
	}

	protected TextLabel createLabel() {
		TextLabel label = TextLabel.createSizedLabel(getTitle(), getFont(), new Dimension(getWidth(null), 40));
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		label.setFillMode(FillMode.FIT_DOWNSCALE);
		label.setBackground(new Color(0, 0, 0, 200));
		label.setForeground(Color.WHITE);
		return label;
	}

	@Override
	public void render(Graphics2D g, SlidingItemListComponent component) {
		int w = getWidth(g);
		int h = getHeight(g);
		Image image = getCoverImage();
		if (image != null) {
			g.drawImage(image, 0, 0, w, h, getBackgroundColor(), null);
		} else {
			g.setColor(getBackgroundColor());
			g.fillRect(0, 0, w, h);
		}
		getLabel().paint(g);
	}

	public abstract void execute();

	public abstract String getTitle();

	protected abstract Color getBackgroundColor();

	protected abstract Image getCoverImage();

	@Override
	public int getWidth(Graphics2D g) {
		return getSize().width;
	}

	@Override
	public int getHeight(Graphics2D g) {
		return getSize().height;
	}

	private Dimension getSize() {
		return size;
	}

	@Override
	public Insets getMargin() {
		return margin;
	}

	private Font getFont() {
		return font;
	}

	private TextLabel getLabel() {
		if (label == null) {
			label = createLabel();
		}
		return label;
	}

	protected CarouselHost getHost() {
		return getCarouselComponent().getHost();
	}

	protected CarouselComponent getCarouselComponent() {
		return carouselComponent;
	}

}