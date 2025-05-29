package org.maia.amstrad.gui.covers.repo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class RepositoryProgramCoverImageProducer extends AmstradProgramCoverImageProducer {

	public RepositoryProgramCoverImageProducer(Dimension imageSize) {
		super(imageSize);
	}

	public RepositoryProgramCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		super(imageSize, backgroundColor);
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		Image image = getCoverImageFromRepository(programNode);
		if (image != null) {
			image = frameImageToSize(image);
		}
		return image;
	}

}