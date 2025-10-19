package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradFolderPosterImageMaker;
import org.maia.amstrad.gui.covers.cassette.CassetteCoverImageMaker.CoverImageEmbedding;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.Randomizer;

public class CassetteFolderCoverImageProducer extends AmstradFolderCoverImageProducer {

	private OpenCassetteCoverImageMaker imageMaker;

	private CassetteProgramCoverImageProducer programImageMaker;

	private AmstradFolderPosterImageMaker folderImageMaker;

	public CassetteFolderCoverImageProducer(Dimension imageSize, Color backgroundColor, Font titleFont,
			Color titleColor, CassetteProgramCoverImageProducer programImageMaker,
			AmstradFolderPosterImageMaker folderImageMaker) {
		super(imageSize, backgroundColor);
		double scaleFactor = imageSize.getHeight() / OpenCassetteCoverImageMaker.CANONICAL_SIZE.getHeight();
		this.imageMaker = new OpenCassetteCoverImageMaker(null, scaleFactor);
		this.imageMaker.setTitleFont(titleFont);
		this.imageMaker.setTitleColor(titleColor);
		this.programImageMaker = programImageMaker;
		this.folderImageMaker = folderImageMaker;
	}

	@Override
	protected Image produceImage(FolderNode folderNode, ProgramNode featuredProgramNode) {
		OpenCassetteCoverImageMaker imageMaker = getImageMaker();
		Dimension posterSize = imageMaker.scaleSize(ClosedCassetteCoverImageMaker.CANONICAL_POSTER_REGION.getSize());
		Image posterImage = producePosterImage(folderNode, featuredProgramNode, posterSize);
		imageMaker.setTitle(folderNode.getName());
		imageMaker.setRandomizer(new Randomizer(folderNode.getName()));
		imageMaker.setPrintColor(imageMaker.drawPrintColor());
		CoverImageEmbedding embedding = new CoverImageEmbedding(getImageSize(), getBackgroundColor());
		embedding.setPadTopFraction(0);
		return imageMaker.makeCoverImage(ImageUtils.convertToBufferedImage(posterImage), embedding);
	}

	protected Image producePosterImage(FolderNode folderNode, ProgramNode featuredProgramNode, Dimension posterSize) {
		if (featuredProgramNode != null) {
			return getProgramImageMaker().producePosterImage(featuredProgramNode, posterSize).getImage();
		} else {
			return getFolderImageMaker().makePosterImage(folderNode, posterSize);
		}
	}

	private OpenCassetteCoverImageMaker getImageMaker() {
		return imageMaker;
	}

	private CassetteProgramCoverImageProducer getProgramImageMaker() {
		return programImageMaker;
	}

	private AmstradFolderPosterImageMaker getFolderImageMaker() {
		return folderImageMaker;
	}

}