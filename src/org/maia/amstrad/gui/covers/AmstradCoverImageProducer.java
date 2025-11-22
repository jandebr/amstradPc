package org.maia.amstrad.gui.covers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.graphics2d.image.pool.PooledImageProducer;
import org.maia.swing.layout.FillMode;
import org.maia.swing.layout.HorizontalAlignment;
import org.maia.swing.layout.InnerRegionLayout;
import org.maia.swing.layout.VerticalAlignment;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public abstract class AmstradCoverImageProducer implements PooledImageProducer {

	private Dimension imageSize;

	private Color backgroundColor;

	protected AmstradCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		this.imageSize = imageSize;
		this.backgroundColor = backgroundColor;
	}

	protected String getProducerIdentifier() {
		StringBuilder sb = new StringBuilder(64);
		sb.append(getClass().getSimpleName());
		sb.append('_').append(getImageSize().width).append('x').append(getImageSize().height);
		if (getBackgroundColor() != null) {
			sb.append('c').append(getBackgroundColor().getRGB());
		}
		return sb.toString();
	}

	protected Image getCoverImageFromRepository(Node node) {
		Image image = null;
		AmstradProgramImage imageProxy = node.getCoverImage();
		if (imageProxy != null) {
			image = imageProxy.getImage();
			imageProxy.disposeImage(); // free up image pool
		}
		return image;
	}

	protected Image frameImageToSize(Image image) {
		return frameImageToSize(image, FillMode.FIT);
	}

	protected Image frameImageToSize(Image image, FillMode fillMode) {
		return frameImageToSize(image, getImageSize(), fillMode, getBackgroundColor());
	}

	protected static Image frameImageToSize(Image image, Dimension targetSize, FillMode fillMode,
			Color backgroundColor) {
		Dimension sourceSize = ImageUtils.getSize(image);
		if (sourceSize.equals(targetSize)) {
			if (backgroundColor == null) {
				return image;
			} else if (image instanceof BufferedImage && ImageUtils.isFullyOpaque((BufferedImage) image)) {
				return image;
			}
		}
		InnerRegionLayout layout = new InnerRegionLayout(targetSize, sourceSize);
		layout.setHorizontalAlignment(HorizontalAlignment.CENTER);
		layout.setVerticalAlignment(VerticalAlignment.CENTER);
		layout.setFillMode(fillMode);
		Rectangle bounds = layout.getInnerRegionLayoutBounds();
		BufferedImage framedImage = ImageUtils.createImage(targetSize, backgroundColor);
		Graphics2D g2 = framedImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
		g2.dispose();
		return framedImage;
	}

	protected static Color chooseImageFrameColor(Image image, Color darkFrameColor, Color brightFrameColor,
			Randomizer rnd) {
		BufferedImage bufImage = ImageUtils.convertToBufferedImage(image);
		float brightness = getOpaqueOutlineBrightness(bufImage);
		if (brightness >= 0f) {
			// background matching brightness of the opaque outline
			if (brightness < ColorUtils.getBrightness(darkFrameColor)
					|| brightness > ColorUtils.getBrightness(brightFrameColor)) {
				return new Color(Color.HSBtoRGB(0, 0, brightness));
			} else if (brightness < 0.5f) {
				return darkFrameColor;
			} else {
				return brightFrameColor;
			}
		} else {
			// (semi)transparent outline, background providing contrast with content
			brightness = getContentBrightness(bufImage, rnd);
			if (brightness < 0.5f) {
				return brightFrameColor;
			} else {
				return darkFrameColor;
			}
		}
	}

	private static float getOpaqueOutlineBrightness(BufferedImage image) {
		float brightness = 0f;
		int width = ImageUtils.getWidth(image);
		int height = ImageUtils.getHeight(image);
		int nrSamples = (width + height) * 2;
		int red = 0, green = 0, blue = 0;
		for (int i = 0; i < nrSamples; i++) {
			int x = 0, y = 0;
			if (i < width) {
				x = i;
			} else if (i < width + height) {
				x = width - 1;
				y = i - width;
			} else if (i < width * 2 + height) {
				x = i - width - height;
				y = height - 1;
			} else {
				y = i - width * 2 - height;
			}
			int rgb = image.getRGB(x, y);
			int alpha = rgb >>> 24;
			if (alpha < 0xff) {
				return -1f;
			}
			red += (rgb & 0xff0000) >> 16;
			green += (rgb & 0xff00) >> 8;
			blue += rgb & 0xff;
		}
		if (nrSamples > 0) {
			red /= nrSamples;
			green /= nrSamples;
			blue /= nrSamples;
			brightness = ColorUtils.getBrightness(new Color(red, green, blue));
		}
		return brightness;
	}

	private static float getContentBrightness(BufferedImage image, Randomizer rnd) {
		float brightness = 0f;
		int width = ImageUtils.getWidth(image);
		int height = ImageUtils.getHeight(image);
		int minSamples = Math.min(width * height, 100);
		int nrSamples = 0;
		int red = 0, green = 0, blue = 0;
		int i = 0, maxi = 1000;
		while (i++ < maxi && nrSamples < minSamples) {
			int x = rnd.drawIntegerNumber(0, width - 1);
			int y = rnd.drawIntegerNumber(0, height - 1);
			int rgb = image.getRGB(x, y);
			int alpha = rgb >>> 24;
			if (alpha == 0xff) {
				red += (rgb & 0xff0000) >> 16;
				green += (rgb & 0xff00) >> 8;
				blue += rgb & 0xff;
				nrSamples++;
			}
		}
		if (nrSamples > 0) {
			red /= nrSamples;
			green /= nrSamples;
			blue /= nrSamples;
			brightness = ColorUtils.getBrightness(new Color(red, green, blue));
		}
		return brightness;
	}

	protected Randomizer createRandomizer(Node node) {
		return new Randomizer(node.getName());
	}

	public Dimension getImageSize() {
		return imageSize;
	}

	public void setImageSize(Dimension imageSize) {
		this.imageSize = imageSize;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

}