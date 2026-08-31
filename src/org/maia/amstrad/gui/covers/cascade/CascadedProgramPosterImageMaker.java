package org.maia.amstrad.gui.covers.cascade;

import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.covers.AmstradProgramPosterImage;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImageMaker;
import org.maia.amstrad.gui.covers.ImageDetailLevel;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CascadedProgramPosterImageMaker implements AmstradProgramPosterImageMaker {

	private List<AmstradProgramPosterImageMaker> imageMakers;

	public CascadedProgramPosterImageMaker() {
		this.imageMakers = new Vector<AmstradProgramPosterImageMaker>();
	}

	public void appendImageMaker(AmstradProgramPosterImageMaker imageMaker) {
		getImageMakers().add(imageMaker);
	}

	@Override
	public AmstradProgramPosterImage makePosterImage(ProgramNode programNode, Dimension size,
			ImageDetailLevel detailLevel) {
		for (AmstradProgramPosterImageMaker imageMaker : getImageMakers()) {
			AmstradProgramPosterImage image = imageMaker.makePosterImage(programNode, size, detailLevel);
			if (image != null)
				return image;
		}
		return null;
	}

	private List<AmstradProgramPosterImageMaker> getImageMakers() {
		return imageMakers;
	}

}