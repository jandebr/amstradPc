package org.maia.amstrad.gui.covers.repo;

import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class FeaturedProgramFolderCoverImageProducer extends AmstradFolderCoverImageProducer {

	public FeaturedProgramFolderCoverImageProducer(Dimension imageSize) {
		super(imageSize, null);
	}

	@Override
	protected Image produceImage(FolderNode folderNode, ProgramNode featuredProgramNode) {
		Image image = getCoverImageFromRepository(featuredProgramNode);
		if (image != null) {
			image = frameImageToSize(image);
		}
		return image;
	}

}