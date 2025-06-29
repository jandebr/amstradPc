package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.covers.util.ResourcePaths;
import org.maia.graphics2d.image.ImageUtils;

public class CassetteTextureImageMaker implements ResourcePaths {

	public CassetteTextureImageMaker() {
	}

	public BufferedImage createTextureImage() {
		BufferedImage coloredTexture = ImageUtils.readFromFile(CASSETTE_CLOSED_PATH + "blue-texture-300x480.png");
		BufferedImage border = ImageUtils.readFromFile(CASSETTE_CLOSED_PATH + "border-300x480.png");
		int width = ImageUtils.getWidth(coloredTexture);
		int height = ImageUtils.getHeight(coloredTexture);
		BufferedImage texture = ImageUtils.createImage(width, height, Color.BLACK);
		float[] hsb = new float[3];
		float[] hsbMin = getMinimumHSB(coloredTexture, width, height);
		float[] hsbMax = getMaximumHSB(coloredTexture, width, height);
		float[] hsbAvg = getAverageHSB(coloredTexture, width, height);
		float scaleHue = 127f / Math.max(hsbMax[0] - hsbAvg[0], hsbAvg[0] - hsbMin[0]);
		float scaleSat = 127f / Math.max(hsbMax[1] - hsbAvg[1], hsbAvg[1] - hsbMin[1]);
		float scaleBri = 127f / Math.max(hsbMax[2] - hsbAvg[2], hsbAvg[2] - hsbMin[2]);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sampleHSB(coloredTexture, x, y, hsb);
				int red = Math.round(128f + scaleHue * (hsb[0] - hsbAvg[0]));
				int green = Math.round(128f + scaleSat * (hsb[1] - hsbAvg[1]));
				int blue = Math.round(128f + scaleBri * (hsb[2] - hsbAvg[2]));
				int alpha = (int) Math.round(Math.pow((border.getRGB(x, y) >>> 24) / 255.0, 2.0) * 255.0);
				int rgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
				texture.setRGB(x, y, rgb);
			}
		}
		return texture;
	}

	private float[] getMinimumHSB(BufferedImage image, int width, int height) {
		float[] hsb = new float[3];
		float[] hsbMin = new float[3];
		sampleHSB(image, 0, 0, hsbMin);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sampleHSB(image, x, y, hsb);
				hsbMin[0] = Math.min(hsbMin[0], hsb[0]);
				hsbMin[1] = Math.min(hsbMin[1], hsb[1]);
				hsbMin[2] = Math.min(hsbMin[2], hsb[2]);
			}
		}
		return hsbMin;
	}

	private float[] getMaximumHSB(BufferedImage image, int width, int height) {
		float[] hsb = new float[3];
		float[] hsbMax = new float[3];
		sampleHSB(image, 0, 0, hsbMax);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sampleHSB(image, x, y, hsb);
				hsbMax[0] = Math.max(hsbMax[0], hsb[0]);
				hsbMax[1] = Math.max(hsbMax[1], hsb[1]);
				hsbMax[2] = Math.max(hsbMax[2], hsb[2]);
			}
		}
		return hsbMax;
	}

	private float[] getAverageHSB(BufferedImage image, int width, int height) {
		float[] hsb = new float[3];
		float[] hsbAvg = new float[3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sampleHSB(image, x, y, hsb);
				hsbAvg[0] += hsb[0];
				hsbAvg[1] += hsb[1];
				hsbAvg[2] += hsb[2];
			}
		}
		int n = width * height;
		hsbAvg[0] /= n;
		hsbAvg[1] /= n;
		hsbAvg[2] /= n;
		return hsbAvg;
	}

	private void sampleHSB(BufferedImage image, int x, int y, float[] hsb) {
		int rgb = image.getRGB(x, y);
		int red = (rgb & 0xff0000) >>> 16;
		int green = (rgb & 0xff00) >>> 8;
		int blue = rgb & 0xff;
		Color.RGBtoHSB(red, green, blue, hsb);
	}

}