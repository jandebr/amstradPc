package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.covers.util.ResourcePaths;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.ColorUtils;

public class CassetteGlossImageMaker implements ResourcePaths {

	public CassetteGlossImageMaker() {
	}

	public BufferedImage createGlossImage() {
		BufferedImage glossGrayscale = ImageUtils.readFromFile(CASSETTE_OPEN_PATH + "tape-up-gloss.png");
		int width = ImageUtils.getWidth(glossGrayscale);
		int height = ImageUtils.getHeight(glossGrayscale);
		BufferedImage gloss = ImageUtils.createImage(width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = glossGrayscale.getRGB(x, y);
				int alpha = rgb >>> 24;
				if (alpha > 0) {
					float brightness = ColorUtils.getBrightness(new Color(rgb));
					alpha = Math.min(Math.round(128f * brightness / 0.737f), 128);
					if (alpha == 0) {
						gloss.setRGB(x, y, 0);
					} else {
						gloss.setRGB(x, y, (alpha << 24 | 0x00ffffff));
					}
				}
			}
		}
		return gloss;
	}

}