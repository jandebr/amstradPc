package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.cassette.CassetteCoverImageMaker.CoverImageEmbedding;
import org.maia.amstrad.gui.covers.cassette.CassettePosterImageProducer.PosterImage;
import org.maia.amstrad.gui.covers.util.Randomizer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CassetteProgramCoverImageProducer extends AmstradProgramCoverImageProducer {

	private CassettePosterImageProducer posterMaker;

	private ClosedCassetteCoverImageMaker imageMaker;

	public CassetteProgramCoverImageProducer(Dimension imageSize, Color backgroundColor, Font titleFont,
			Color titleColor, Color titleBackground, float titleRelativeVerticalPosition) {
		super(imageSize, backgroundColor);
		double scaleFactor = imageSize.getHeight() / ClosedCassetteCoverImageMaker.CANONICAL_SIZE.getHeight();
		this.posterMaker = new CassettePosterImageProducer(imageSize);
		this.imageMaker = new ClosedCassetteCoverImageMaker(null, scaleFactor);
		this.imageMaker.setTitleFont(titleFont);
		this.imageMaker.setTitleColor(titleColor);
		this.imageMaker.setTitleBackground(titleBackground);
		this.imageMaker.setTitleRelativeVerticalPosition(titleRelativeVerticalPosition);
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		PosterImage posterImage = producePosterImage(programNode);
		ClosedCassetteCoverImageMaker imageMaker = getImageMaker();
		imageMaker.setTitle(posterImage.isUntitledImage() ? programNode.getName() : null);
		imageMaker.setRandomizer(new Randomizer(programNode.getName()));
		CoverImageEmbedding embedding = new CoverImageEmbedding(getImageSize(), getBackgroundColor());
		embedding.setPadTopFraction(0.32f);
		return imageMaker.makeCoverImage(posterImage.getImage(), embedding);
	}

	protected PosterImage producePosterImage(ProgramNode programNode) {
		CassettePosterImageProducer posterMaker = getPosterMaker();
		posterMaker.setImageSize(getImageMaker().getScaledPosterSize());
		return posterMaker.producePosterImage(programNode);
	}

	private CassettePosterImageProducer getPosterMaker() {
		return posterMaker;
	}

	private ClosedCassetteCoverImageMaker getImageMaker() {
		return imageMaker;
	}

}