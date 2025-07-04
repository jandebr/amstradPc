package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.fabric.FabricCoverImageMaker;
import org.maia.amstrad.gui.covers.util.Randomizer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.ColorUtils;

public class CassettePosterImageProducer extends AmstradProgramCoverImageProducer {

	private FabricCoverImageMaker imageMaker;

	public static Color BACKGROUND_BRIGHT = new Color(255, 254, 242);

	public static Color BACKGROUND_DARK = Color.BLACK;

	public CassettePosterImageProducer(Dimension imageSize) {
		super(imageSize, null);
		this.imageMaker = new FabricCoverImageMaker();
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		return producePosterImage(programNode).getImage();
	}

	public synchronized PosterImage producePosterImage(ProgramNode programNode) {
		Randomizer rnd = new Randomizer(programNode.getName());
		Image image = getCoverImageFromRepository(programNode);
		if (image != null) {
			setBackgroundColor(chooseBackgroundColor(ImageUtils.convertToBufferedImage(image), rnd));
			return new PosterImage(frameImageToSize(image), false); // assuming titled
		} else {
			return inventPosterImage(rnd);
		}
	}

	public synchronized PosterImage inventPosterImage(Randomizer rnd) {
		FabricCoverImageMaker imageMaker = getImageMaker();
		imageMaker.setRandomizer(rnd);
		return new PosterImage(imageMaker.makeCoverImage(getImageSize()), true); // certainly untitled
	}

	protected Color chooseBackgroundColor(BufferedImage image, Randomizer rnd) {
		float brightness = getOpaqueOutlineBrightness(image);
		if (brightness >= 0f) {
			// background matching brightness of the opaque outline
			if (brightness < ColorUtils.getBrightness(BACKGROUND_DARK)
					|| brightness > ColorUtils.getBrightness(BACKGROUND_BRIGHT)) {
				return new Color(Color.HSBtoRGB(0, 0, brightness));
			} else if (brightness < 0.5f) {
				return BACKGROUND_DARK;
			} else {
				return BACKGROUND_BRIGHT;
			}
		} else {
			// (semi)transparent outline, background providing contrast with content
			brightness = getContentBrightness(image, rnd);
			if (brightness < 0.5f) {
				return BACKGROUND_BRIGHT;
			} else {
				return BACKGROUND_DARK;
			}
		}
	}

	private float getOpaqueOutlineBrightness(BufferedImage image) {
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

	private float getContentBrightness(BufferedImage image, Randomizer rnd) {
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

	private FabricCoverImageMaker getImageMaker() {
		return imageMaker;
	}

	public static class PosterImage {

		private BufferedImage image;

		private boolean untitledImage;

		public PosterImage(Image image, boolean untitledImage) {
			this.image = ImageUtils.convertToBufferedImage(image);
			this.untitledImage = untitledImage;
		}

		public BufferedImage getImage() {
			return image;
		}

		public boolean isUntitledImage() {
			return untitledImage;
		}

	}

}