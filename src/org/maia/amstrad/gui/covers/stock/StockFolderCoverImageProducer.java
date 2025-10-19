package org.maia.amstrad.gui.covers.stock;

import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradFolderPosterImageMaker;
import org.maia.amstrad.gui.covers.stock.fabric.FabricCoverImageMaker;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.util.Randomizer;

public class StockFolderCoverImageProducer extends AmstradFolderCoverImageProducer
		implements AmstradFolderPosterImageMaker {

	private FabricCoverImageMaker imageMaker;

	public StockFolderCoverImageProducer(Dimension imageSize, FabricCoverImageMaker imageMaker) {
		super(imageSize, null);
		this.imageMaker = imageMaker;
	}

	StockFolderCoverImageProducer deriveForImageSize(Dimension imageSize) {
		if (imageSize.equals(getImageSize())) {
			return this;
		} else {
			return new StockFolderCoverImageProducer(imageSize, getImageMaker());
		}
	}

	@Override
	protected Image produceImage(FolderNode folderNode, ProgramNode featuredProgramNode) {
		return makePosterImage(folderNode, getImageSize());
	}

	@Override
	public Image makePosterImage(FolderNode folderNode, Dimension size) {
		FabricCoverImageMaker imageMaker = getImageMaker();
		imageMaker.setRandomizer(new Randomizer(folderNode.getName()));
		return imageMaker.makeCoverImage(size);
	}

	private FabricCoverImageMaker getImageMaker() {
		return imageMaker;
	}

}