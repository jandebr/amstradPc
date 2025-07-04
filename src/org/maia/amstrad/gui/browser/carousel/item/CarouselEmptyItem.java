package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.BorderFactory;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.layout.FillMode;
import org.maia.swing.text.TextLabel;

public class CarouselEmptyItem extends CarouselItem {

	private TextLabel label;

	private Font font;

	public CarouselEmptyItem(CarouselComponent carouselComponent, Dimension size, Insets margin, Font font) {
		super(carouselComponent, size, margin);
		this.font = font;
	}

	@Override
	public void render(Graphics2D g, SlidingItemListComponent component) {
		int w = getWidth(g);
		int h = getHeight(g);
		g.setColor(component.getBackground());
		g.fillRect(0, 0, w, h);
		getLabel().paint(g);
	}

	@Override
	public void execute(CarouselHost host) {
		// does nothing
	}

	protected TextLabel createLabel() {
		TextLabel label = TextLabel.createSizedLabel("<empty>", getFont(), getSize());
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		label.setFillMode(FillMode.FIT_DOWNSCALE);
		label.setForeground(Color.WHITE);
		return label;
	}

	private TextLabel getLabel() {
		if (label == null) {
			label = createLabel();
		}
		return label;
	}

	private Font getFont() {
		return font;
	}

}