package org.maia.amstrad.gui.covers.cascade;

import java.awt.Dimension;
import java.awt.Image;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.covers.AmstradFolderPosterImageMaker;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CascadedFolderPosterImageMaker implements AmstradFolderPosterImageMaker {

	private List<AmstradFolderPosterImageMaker> imageMakers;

	public CascadedFolderPosterImageMaker() {
		this.imageMakers = new Vector<AmstradFolderPosterImageMaker>();
	}

	public void appendImageMaker(AmstradFolderPosterImageMaker imageMaker) {
		getImageMakers().add(imageMaker);
	}

	@Override
	public Image makePosterImage(FolderNode folderNode, ProgramNode featuredProgramNode, Dimension size) {
		for (AmstradFolderPosterImageMaker imageMaker : getImageMakers()) {
			Image image = imageMaker.makePosterImage(folderNode, featuredProgramNode, size);
			if (image != null)
				return image;
		}
		return null;
	}

	private List<AmstradFolderPosterImageMaker> getImageMakers() {
		return imageMakers;
	}

}