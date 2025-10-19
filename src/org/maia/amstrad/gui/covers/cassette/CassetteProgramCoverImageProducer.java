package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImageMaker;
import org.maia.amstrad.gui.covers.cassette.CassetteCoverImageMaker.CoverImageEmbedding;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.swing.layout.FillMode;
import org.maia.util.Randomizer;

public class CassetteProgramCoverImageProducer extends AmstradProgramCoverImageProducer {

	private ClosedCassetteCoverImageMaker imageMaker;

	private AmstradProgramPosterImageMaker posterImageMaker;

	private Color posterBackgroundColorDark;

	private Color posterBackgroundColorBright;

	public CassetteProgramCoverImageProducer(Dimension imageSize, Color backgroundColor,
			Color posterBackgroundColorDark, Color posterBackgroundColorBright, Font titleFont, Color titleColor,
			Color titleBackground, float titleRelativeVerticalPosition,
			AmstradProgramPosterImageMaker posterImageMaker) {
		super(imageSize, backgroundColor);
		double scaleFactor = imageSize.getHeight() / ClosedCassetteCoverImageMaker.CANONICAL_SIZE.getHeight();
		this.imageMaker = new ClosedCassetteCoverImageMaker(null, scaleFactor);
		this.imageMaker.setTitleFont(titleFont);
		this.imageMaker.setTitleColor(titleColor);
		this.imageMaker.setTitleBackground(titleBackground);
		this.imageMaker.setTitleRelativeVerticalPosition(titleRelativeVerticalPosition);
		this.posterBackgroundColorDark = posterBackgroundColorDark;
		this.posterBackgroundColorBright = posterBackgroundColorBright;
		this.posterImageMaker = posterImageMaker;
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		ClosedCassetteCoverImageMaker imageMaker = getImageMaker();
		ProgramPosterImage posterImage = producePosterImage(programNode, imageMaker.getScaledPosterSize());
		imageMaker.setTitle(posterImage.isUntitledImage() ? programNode.getName() : null);
		imageMaker.setRandomizer(new Randomizer(programNode.getName()));
		CoverImageEmbedding embedding = new CoverImageEmbedding(getImageSize(), getBackgroundColor());
		embedding.setPadTopFraction(0.32f);
		return imageMaker.makeCoverImage(posterImage.getImage(), embedding);
	}

	protected ProgramPosterImage producePosterImage(ProgramNode programNode, Dimension posterSize) {
		Image image = getCoverImageFromRepository(programNode);
		if (image != null) {
			return toPosterImage(image, posterSize, new Randomizer(programNode.getName()));
		} else {
			return inventPosterImage(programNode, posterSize);
		}
	}

	protected ProgramPosterImage toPosterImage(Image image, Dimension posterSize, Randomizer rnd) {
		Color bgDark = getPosterBackgroundColorDark();
		Color bgBright = getPosterBackgroundColorBright();
		Color bg = chooseImageFrameColor(image, bgDark, bgBright, rnd);
		Image framedImage = frameImageToSize(image, posterSize, FillMode.FIT, bg);
		return new ProgramPosterImage(framedImage, false); // assuming titled
	}

	protected ProgramPosterImage inventPosterImage(ProgramNode programNode, Dimension posterSize) {
		Image image = getPosterImageMaker().makePosterImage(programNode, posterSize);
		return new ProgramPosterImage(image, true); // assuming untitled
	}

	private ClosedCassetteCoverImageMaker getImageMaker() {
		return imageMaker;
	}

	public Color getPosterBackgroundColorDark() {
		return posterBackgroundColorDark;
	}

	public Color getPosterBackgroundColorBright() {
		return posterBackgroundColorBright;
	}

	private AmstradProgramPosterImageMaker getPosterImageMaker() {
		return posterImageMaker;
	}

	static class ProgramPosterImage {

		private BufferedImage image;

		private boolean untitledImage;

		public ProgramPosterImage(Image image, boolean untitledImage) {
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