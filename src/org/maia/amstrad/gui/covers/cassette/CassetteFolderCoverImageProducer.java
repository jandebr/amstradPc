package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.cassette.CassetteCoverImageMaker.CoverImageEmbedding;
import org.maia.amstrad.gui.covers.cassette.CassettePosterImageProducer.PosterImage;
import org.maia.amstrad.gui.covers.util.Randomizer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CassetteFolderCoverImageProducer extends AmstradFolderCoverImageProducer {

	private CassettePosterImageProducer posterMaker;

	private OpenCassetteCoverImageMaker imageMaker;

	public CassetteFolderCoverImageProducer(Dimension imageSize) {
		this(imageSize, null);
	}

	public CassetteFolderCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		super(imageSize, backgroundColor);
		double scaleFactor = imageSize.getHeight() / OpenCassetteCoverImageMaker.CANONICAL_SIZE.getHeight();
		this.posterMaker = new CassettePosterImageProducer(imageSize);
		this.imageMaker = new OpenCassetteCoverImageMaker(null, scaleFactor);
	}

	@Override
	protected synchronized Image produceImage(FolderNode folderNode, ProgramNode showcaseProgramNode) {
		PosterImage posterImage = producePosterImage(folderNode, showcaseProgramNode);
		OpenCassetteCoverImageMaker imageMaker = getImageMaker();
		imageMaker.setTitle(folderNode.getName());
		imageMaker.setRandomizer(new Randomizer(folderNode.getName()));
		Dimension size = imageMaker.scaleSize(OpenCassetteCoverImageMaker.CANONICAL_SIZE);
		CoverImageEmbedding embedding = new CoverImageEmbedding(size, getBackgroundColor());
		embedding.setPadTopFraction(0);
		return imageMaker.makeCoverImage(posterImage.getImage(), embedding);
	}

	protected PosterImage producePosterImage(FolderNode folderNode, ProgramNode showcaseProgramNode) {
		CassettePosterImageProducer posterMaker = getPosterMaker();
		posterMaker.setImageSize(getImageMaker().getScaledFrontImageSize());
		if (showcaseProgramNode != null) {
			return posterMaker.producePosterImage(showcaseProgramNode);
		} else {
			return posterMaker.inventPosterImage(folderNode);
		}
	}

	private CassettePosterImageProducer getPosterMaker() {
		return posterMaker;
	}

	private OpenCassetteCoverImageMaker getImageMaker() {
		return imageMaker;
	}

}