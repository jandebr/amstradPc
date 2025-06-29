package org.maia.amstrad.gui.covers.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.ColorUtils;

public class ReflectionImageMaker {

	private boolean outlineFullyAttachedToSurface;

	private int maxReflectionHeight;

	private int marginToReflection;

	private float reflectionLuminance = 0.3f;

	private int minimumOpaqueAlphaValue = 1;

	private Color alphaColor;

	private ImageOutline lastImageOutline;

	public ReflectionImageMaker(int maxReflectionHeight) {
		this(maxReflectionHeight, 0);
	}

	public ReflectionImageMaker(int maxReflectionHeight, int marginToReflection) {
		this.maxReflectionHeight = maxReflectionHeight;
		this.marginToReflection = marginToReflection;
	}

	public BufferedImage reflectImage(BufferedImage image) {
		return reflectImage(image, null);
	}

	public BufferedImage reflectImage(BufferedImage image, ImageOutline imageOutline) {
		return reflectImage(image, imageOutline, false);
	}

	public BufferedImage reflectImage(BufferedImage image, ImageOutline imageOutline, boolean imageReadOnly) {
		if (getMaxReflectionHeight() <= 0) {
			return image;
		} else {
			if (imageOutline == null)
				imageOutline = createImageOutline(image);
			BufferedImage cover = imageReadOnly ? ImageUtils.duplicateImage(image) : image;
			applyReflection(cover, imageOutline);
			setLastImageOutline(imageOutline);
			return cover;
		}
	}

	protected void applyReflection(BufferedImage image, ImageOutline imageOutline) {
		int lvd = imageOutline.getLeastVerticalDistance();
		if (lvd < 0)
			return;
		int width = ImageUtils.getWidth(image);
		int height = ImageUtils.getHeight(image);
		int margin = getMarginToReflection();
		int maxRef = getMaxReflectionHeight();
		boolean attached = isOutlineFullyAttachedToSurface();
		float luminance = getReflectionLuminance();
		for (int x = 0; x < width; x++) {
			int vd = imageOutline.getVerticalDistance(x);
			if (vd >= 0) {
				int nRef = Math.min(height - vd, maxRef);
				int baseY = height - 1 - vd;
				int refY = 0;
				if (attached) {
					nRef = Math.min(nRef, vd - margin);
					refY = baseY + 1 + margin;
				} else {
					nRef = Math.min(nRef, lvd - margin - (vd - lvd));
					refY = height - lvd + margin + (vd - lvd);
				}
				if (nRef > 1) {
					for (int i = 0; i < nRef; i++) {
						float r = (1f - luminance) + luminance * i / (nRef - 1);
						int baseRgb = image.getRGB(x, baseY--);
						int destRgb = ColorUtils.interpolate(baseRgb, image.getRGB(x, refY), r);
						image.setRGB(x, refY++, destRgb);
					}
				}
			}
		}
	}

	private ImageOutline createImageOutline(BufferedImage image) {
		int width = ImageUtils.getWidth(image);
		int height = ImageUtils.getHeight(image);
		ImageOutline outline = new ImageOutline(width);
		for (int x = 0; x < width; x++) {
			int y = height - 1;
			int argb = image.getRGB(x, y);
			while (!isOpaque(argb) && y > 0) {
				argb = image.getRGB(x, --y);
			}
			if (isOpaque(argb)) {
				outline.setVerticalDistance(x, height - 1 - y);
			}
		}
		return outline;
	}

	protected boolean isOpaque(int argb) {
		if ((argb >>> 24) < getMinimumOpaqueAlphaValue()) {
			return false;
		} else if (getAlphaColor() != null && getAlphaColor().getRGB() == argb) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isOutlineFullyAttachedToSurface() {
		return outlineFullyAttachedToSurface;
	}

	public void setOutlineFullyAttachedToSurface(boolean attached) {
		this.outlineFullyAttachedToSurface = attached;
	}

	public int getMaxReflectionHeight() {
		return maxReflectionHeight;
	}

	public void setMaxReflectionHeight(int maxReflectionHeight) {
		this.maxReflectionHeight = maxReflectionHeight;
	}

	public int getMarginToReflection() {
		return marginToReflection;
	}

	public void setMarginToReflection(int margin) {
		this.marginToReflection = margin;
	}

	public float getReflectionLuminance() {
		return reflectionLuminance;
	}

	public void setReflectionLuminance(float luminance) {
		this.reflectionLuminance = luminance;
	}

	public int getMinimumOpaqueAlphaValue() {
		return minimumOpaqueAlphaValue;
	}

	public void setMinimumOpaqueAlphaValue(int alphaValue) {
		this.minimumOpaqueAlphaValue = alphaValue;
	}

	public Color getAlphaColor() {
		return alphaColor;
	}

	public void setAlphaColor(Color alphaColor) {
		this.alphaColor = alphaColor;
	}

	public ImageOutline getLastImageOutline() {
		return lastImageOutline;
	}

	private void setLastImageOutline(ImageOutline lastImageOutline) {
		this.lastImageOutline = lastImageOutline;
	}

	public static class ImageOutline {

		private int[] verticalDistances; // by horizontal coordinate, bottom row is distance 0

		private int leastVerticalDistance;

		private ImageOutline(int width) {
			this.verticalDistances = new int[width];
			Arrays.fill(verticalDistances, -1);
			this.leastVerticalDistance = -1;
		}

		private int getVerticalDistance(int x) {
			if (x >= 0 && x < verticalDistances.length) {
				return verticalDistances[x];
			} else {
				return -1;
			}
		}

		private void setVerticalDistance(int x, int distance) {
			if (x >= 0 && x < verticalDistances.length) {
				verticalDistances[x] = distance;
				if (leastVerticalDistance == -1 || distance < leastVerticalDistance)
					leastVerticalDistance = distance;
			}
		}

		private int getLeastVerticalDistance() {
			return leastVerticalDistance;
		}

	}

}