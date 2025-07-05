package org.maia.amstrad.gui.covers.repo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class RepositoryFolderCoverImageProducer extends AmstradFolderCoverImageProducer {

	public RepositoryFolderCoverImageProducer(Dimension imageSize) {
		this(imageSize, null);
	}

	public RepositoryFolderCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		super(imageSize, backgroundColor);
	}

	@Override
	protected Image produceImage(FolderNode folderNode, ProgramNode featuredProgramNode) {
		Image image = getCoverImageFromRepository(folderNode);
		if (image != null) {
			image = frameImageToSize(image);
		}
		return image;
	}

}