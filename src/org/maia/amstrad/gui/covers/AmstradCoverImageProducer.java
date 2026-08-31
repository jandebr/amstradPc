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
		if (node != null) {
			AmstradProgramImage imageProxy = node.getCoverImage();
			if (imageProxy != null) {
				image = imageProxy.getImage();
				imageProxy.disposeImage(); // free up image pool
			}
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
		Color color = getOpaqueOutlineConstantColor(bufImage);
		if (color == null) {
			float brightness = getOpaqueOutlineBrightness(bufImage);
			if (brightness >= 0f) {
				// frame matching brightness of the opaque outline
				if (brightness < ColorUtils.getBrightness(darkFrameColor)
						|| brightness > ColorUtils.getBrightness(brightFrameColor)) {
					color = new Color(Color.HSBtoRGB(0, 0, brightness));
				} else if (brightness < 0.5f) {
					color = darkFrameColor;
				} else {
					color = brightFrameColor;
				}
			} else {
				// (semi)transparent outline, frame providing contrast with content
				brightness = getContentBrightness(bufImage, rnd);
				if (brightness < 0.5f) {
					color = brightFrameColor;
				} else {
					color = darkFrameColor;
				}
			}
		}
		return color;
	}

	private static Color getOpaqueOutlineConstantColor(BufferedImage image) {
		Color color = null;
		ImageOutlineTraverser traverser = new ImageOutlineTraverser(image);
		while (traverser.hasNextRgb()) {
			int rgb = traverser.nextRgb();
			int alpha = rgb >>> 24;
			if (alpha < 0xff) {
				return null; // not opaque
			}
			if (color == null) {
				color = new Color(rgb);
			} else if (color.getRGB() != rgb) {
				return null; // not constant
			}
		}
		return color;
	}

	private static float getOpaqueOutlineBrightness(BufferedImage image) {
		float brightness = 0f;
		int red = 0, green = 0, blue = 0;
		ImageOutlineTraverser traverser = new ImageOutlineTraverser(image);
		while (traverser.hasNextRgb()) {
			int rgb = traverser.nextRgb();
			int alpha = rgb >>> 24;
			if (alpha < 0xff) {
				return -1f; // not opaque
			}
			red += (rgb & 0xff0000) >> 16;
			green += (rgb & 0xff00) >> 8;
			blue += rgb & 0xff;
		}
		int nrSamples = traverser.getOutlinePixelCount();
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

	public int getCoverImageBaselineMeasuredFromBottom() {
		return 0; // Subclasses to override
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

	private static class ImageOutlineTraverser {

		private BufferedImage image;

		private int outlineWidth;

		private int outlineHeight;

		private int outlinePixelCount;

		private int outlinePixelIndex;

		public ImageOutlineTraverser(BufferedImage image) {
			this.image = image;
			this.outlineWidth = ImageUtils.getWidth(image);
			this.outlineHeight = ImageUtils.getHeight(image);
			this.outlinePixelCount = computeOutlinePixelCount();
		}

		private int computeOutlinePixelCount() {
			int width = getOutlineWidth();
			int height = getOutlineHeight();
			if (width == 0 || height == 0) {
				return 0;
			} else if (width == 1) {
				return height;
			} else if (height == 1) {
				return width;
			} else {
				return (width + height) * 2 - 4;
			}
		}

		public boolean hasNextRgb() {
			return getOutlinePixelIndex() < getOutlinePixelCount();
		}

		public int nextRgb() {
			int x = 0, y = 0;
			int width = getOutlineWidth();
			int height = getOutlineHeight();
			int i = getOutlinePixelIndex();
			if (i < width) {
				x = i;
			} else if (i < width + height - 1) {
				x = width - 1;
				y = 1 + i - width;
			} else if (i < width * 2 + height - 2) {
				x = width * 2 + height - 3 - i;
				y = height - 1;
			} else if (i < getOutlinePixelCount()) {
				y = height * 2 + width * 2 - 4 - i;
			}
			int rgb = getImage().getRGB(x, y);
			setOutlinePixelIndex(i + 1);
			return rgb;
		}

		public BufferedImage getImage() {
			return image;
		}

		public int getOutlineWidth() {
			return outlineWidth;
		}

		public int getOutlineHeight() {
			return outlineHeight;
		}

		public int getOutlinePixelCount() {
			return outlinePixelCount;
		}

		private int getOutlinePixelIndex() {
			return outlinePixelIndex;
		}

		private void setOutlinePixelIndex(int index) {
			this.outlinePixelIndex = index;
		}

	}

}