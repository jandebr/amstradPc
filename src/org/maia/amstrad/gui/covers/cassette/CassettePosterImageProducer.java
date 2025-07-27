package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.fabric.FabricPosterImageMaker;
import org.maia.amstrad.gui.covers.fabric.FabricPatchPatternTestGenerator;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public class CassettePosterImageProducer extends AmstradProgramCoverImageProducer {

	private FabricPosterImageMaker imageMaker;

	private Color backgroundColorDark;

	private Color backgroundColorBright;

	public CassettePosterImageProducer(Dimension imageSize, Color backgroundColorDark, Color backgroundColorBright) {
		super(imageSize, null);
		this.imageMaker = createImageMaker();
		this.backgroundColorDark = backgroundColorDark;
		this.backgroundColorBright = backgroundColorBright;
	}

	protected FabricPosterImageMaker createImageMaker() {
		FabricPosterImageMaker imageMaker = new FabricPosterImageMaker();
		imageMaker.addPatternGenerator(new FabricPatchPatternTestGenerator()); // TODO
		return imageMaker;
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		return producePosterImage(programNode).getImage();
	}

	public PosterImage producePosterImage(ProgramNode programNode) {
		Randomizer rnd = new Randomizer(programNode.getName());
		Image image = getCoverImageFromRepository(programNode);
		if (image != null) {
			setBackgroundColor(chooseBackgroundColor(ImageUtils.convertToBufferedImage(image), rnd));
			return new PosterImage(frameImageToSize(image), false); // assuming titled
		} else {
			return inventPosterImage(rnd);
		}
	}

	public PosterImage inventPosterImage(Randomizer rnd) {
		FabricPosterImageMaker imageMaker = getImageMaker();
		imageMaker.setRandomizer(rnd);
		imageMaker.propagateRandomizerToPatternGenerators();
		return new PosterImage(imageMaker.makePosterImage(getImageSize()), true); // certainly untitled
	}

	protected Color chooseBackgroundColor(BufferedImage image, Randomizer rnd) {
		Color bgDark = getBackgroundColorDark();
		Color bgBright = getBackgroundColorBright();
		float brightness = getOpaqueOutlineBrightness(image);
		if (brightness >= 0f) {
			// background matching brightness of the opaque outline
			if (brightness < ColorUtils.getBrightness(bgDark) || brightness > ColorUtils.getBrightness(bgBright)) {
				return new Color(Color.HSBtoRGB(0, 0, brightness));
			} else if (brightness < 0.5f) {
				return bgDark;
			} else {
				return bgBright;
			}
		} else {
			// (semi)transparent outline, background providing contrast with content
			brightness = getContentBrightness(image, rnd);
			if (brightness < 0.5f) {
				return bgBright;
			} else {
				return bgDark;
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

	private FabricPosterImageMaker getImageMaker() {
		return imageMaker;
	}

	public Color getBackgroundColorDark() {
		return backgroundColorDark;
	}

	public Color getBackgroundColorBright() {
		return backgroundColorBright;
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