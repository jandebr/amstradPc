package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import org.maia.swing.util.ImageUtils;

public class CarouselEmptyItem extends CarouselItem {

	private static Image coverImage = ImageUtils
			.readFromResource("org/maia/amstrad/gui/browser/carousel/image/rex.png");

	public CarouselEmptyItem(CarouselComponent carouselComponent, Dimension size, Insets margin, Font font) {
		super(carouselComponent, size, margin, font);
	}

	@Override
	public void execute() {
		// does nothing
	}

	@Override
	public String getTitle() {
		return "<empty>";
	}

	@Override
	protected Color getBackgroundColor() {
		return Color.GRAY;
	}

	@Override
	protected Image getCoverImage() {
		return coverImage;
	}

}