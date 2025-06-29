package org.maia.amstrad.gui.covers.fabric;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.covers.util.RandomImageMaker;
import org.maia.amstrad.gui.covers.util.Randomizer;
import org.maia.amstrad.gui.covers.util.ResourcePaths;
import org.maia.graphics2d.image.ImageUtils;

public class ClothTextureImageMaker extends RandomImageMaker implements ResourcePaths {

	public ClothTextureImageMaker() {
		this(new Randomizer());
	}

	public ClothTextureImageMaker(Randomizer randomizer) {
		super(randomizer);
	}

	public BufferedImage createTextureImage() {
		BufferedImage cloth = ImageUtils.readFromFile(CLOTH_PATH + "cloth.png");
		BufferedImage clothHighContrast = ImageUtils.readFromFile(CLOTH_PATH + "cloth-high-contrast.png");
		BufferedImage clothMidContrast = ImageUtils.readFromFile(CLOTH_PATH + "cloth-mid-contrast.png");
		int width = ImageUtils.getWidth(cloth);
		int height = ImageUtils.getHeight(cloth);
		boolean[][] visited = new boolean[width][height];
		BufferedImage texture = ImageUtils.createImage(width, height, Color.BLACK);
		List<Point> dots = new Vector<Point>();
		for (int yc = 0; yc < height; yc++) {
			for (int xc = 0; xc < width; xc++) {
				if (!visited[xc][yc]) {
					int value = clothHighContrast.getRGB(xc, yc) & 0xff;
					if (value == 0) {
						int textureValue = deriveTextureValue(cloth, clothHighContrast, xc, yc);
						texture.setRGB(xc, yc, textureValue << 24);
						visited[xc][yc] = true;
						dots.clear();
						dots.add(new Point(xc, yc));
						expandDots(dots, cloth, clothHighContrast, texture, visited);
					}
				}
				int textureValue = texture.getRGB(xc, yc) >>> 24;
				int midValue = clothMidContrast.getRGB(xc, yc) & 0xff;
				int finalTextureValue = Math.min(textureValue, midValue);
				int argb = 0xff000000 | (finalTextureValue << 16) | (finalTextureValue << 8) | finalTextureValue;
				texture.setRGB(xc, yc, argb);
			}
		}
		return texture;
	}

	private void expandDots(List<Point> dots, BufferedImage cloth, BufferedImage clothHighContrast,
			BufferedImage texture, boolean[][] visited) {
		while (!dots.isEmpty()) {
			int i = drawIntegerNumber(0, dots.size() - 1);
			Point dot = dots.remove(i);
			for (int yd = -1; yd <= 1; yd++) {
				int y = dot.y + yd;
				for (int xd = -1; xd <= 1; xd++) {
					int x = dot.x + xd;
					if (x >= 0 && x < visited.length && y >= 0 && y < visited[x].length && !visited[x][y]) {
						int value = clothHighContrast.getRGB(x, y) & 0xff;
						if (value <= 200) {
							int textureValue = deriveTextureValue(cloth, clothHighContrast, x, y);
							texture.setRGB(x, y, textureValue << 24);
							dots.add(new Point(x, y));
						}
						visited[x][y] = true;
					}
				}
			}
		}
	}

	private int deriveTextureValue(BufferedImage cloth, BufferedImage clothHighContrast, int x, int y) {
		int value = clothHighContrast.getRGB(x, y) & 0xff;
		if (value == 0) {
			return 0;
		} else {
			value = cloth.getRGB(x, y) & 0xff;
			int t = 100;
			return (int) Math.round((Math.max(value, t) - t) / (255.0 - t) * 255.0);
		}
	}

}