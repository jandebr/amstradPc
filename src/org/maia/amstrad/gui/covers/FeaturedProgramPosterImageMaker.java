package org.maia.amstrad.gui.covers;

import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class FeaturedProgramPosterImageMaker implements AmstradFolderPosterImageMaker {

	private AmstradProgramPosterImageMaker featuredImageMaker;

	public FeaturedProgramPosterImageMaker(AmstradProgramPosterImageMaker featuredImageMaker) {
		this.featuredImageMaker = featuredImageMaker;
	}

	@Override
	public Image makePosterImage(FolderNode folderNode, ProgramNode featuredProgramNode, Dimension size) {
		AmstradProgramPosterImage posterImage = getFeaturedImageMaker().makePosterImage(featuredProgramNode, size,
				ImageDetailLevel.MINIMAL);
		if (posterImage != null) {
			return posterImage.getImage();
		} else {
			return null;
		}
	}

	private AmstradProgramPosterImageMaker getFeaturedImageMaker() {
		return featuredImageMaker;
	}

}