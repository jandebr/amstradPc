package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImage;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImageMaker;
import org.maia.amstrad.gui.covers.ImageDetailLevel;
import org.maia.amstrad.gui.covers.cassette.CassetteCoverImageMaker.CoverImageEmbedding;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CassetteProgramCoverImageProducer extends AmstradProgramCoverImageProducer {

	private ClosedCassetteCoverImageMaker imageMaker;

	private AmstradProgramPosterImageMaker posterImageMaker;

	public CassetteProgramCoverImageProducer(Dimension imageSize, Color backgroundColor, Font titleFont,
			Color titleColor, Color titleBackground, float titleRelativeVerticalPosition,
			AmstradProgramPosterImageMaker posterImageMaker) {
		super(imageSize, backgroundColor);
		double scaleFactor = imageSize.getHeight() / ClosedCassetteCoverImageMaker.CANONICAL_SIZE.getHeight();
		this.imageMaker = new ClosedCassetteCoverImageMaker(null, scaleFactor);
		this.imageMaker.setTitleFont(titleFont);
		this.imageMaker.setTitleColor(titleColor);
		this.imageMaker.setTitleBackground(titleBackground);
		this.imageMaker.setTitleRelativeVerticalPosition(titleRelativeVerticalPosition);
		this.posterImageMaker = posterImageMaker;
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		ClosedCassetteCoverImageMaker imageMaker = getImageMaker();
		AmstradProgramPosterImage posterImage = producePosterImage(programNode, imageMaker.getScaledPosterSize(),
				ImageDetailLevel.FULL);
		imageMaker.setTitle(posterImage.isUntitledImage() ? programNode.getName() : null);
		imageMaker.setRandomizer(createRandomizer(programNode));
		return imageMaker.makeCoverImage(posterImage.getImage(), true, getEmbedding());
	}

	protected AmstradProgramPosterImage producePosterImage(ProgramNode programNode, Dimension posterSize,
			ImageDetailLevel detailLevel) {
		return getPosterImageMaker().makePosterImage(programNode, posterSize, detailLevel);
	}

	protected CoverImageEmbedding getEmbedding() {
		CoverImageEmbedding embedding = new CoverImageEmbedding(getImageSize(), getBackgroundColor());
		embedding.setPadTopFraction(0.32f);
		return embedding;
	}

	@Override
	public Image produceHighlightOverlayImage(ProgramNode programNode) {
		return getImageMaker().makeCoverHighlightImage(getEmbedding());
	}

	@Override
	public int getCoverImageBaselineMeasuredFromBottom() {
		return getImageMaker().getCoverImageBaselineMeasuredFromBottom(getEmbedding());
	}

	private ClosedCassetteCoverImageMaker getImageMaker() {
		return imageMaker;
	}

	private AmstradProgramPosterImageMaker getPosterImageMaker() {
		return posterImageMaker;
	}

}