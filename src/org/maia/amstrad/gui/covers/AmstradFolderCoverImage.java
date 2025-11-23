package org.maia.amstrad.gui.covers;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class AmstradFolderCoverImage extends AmstradCoverImage {

	private ProgramNode featuredProgramNode;

	public AmstradFolderCoverImage(FolderNode folderNode, AmstradFolderCoverImageProducer imageProducer) {
		super(folderNode, imageProducer);
	}

	public AmstradFolderCoverImage(FolderNode folderNode, ProgramNode featuredProgramNode,
			AmstradFolderCoverImageProducer imageProducer) {
		this(folderNode, imageProducer);
		this.featuredProgramNode = featuredProgramNode;
	}

	public FolderNode getFolderNode() {
		return (FolderNode) getNode();
	}

	public ProgramNode getFeaturedProgramNode() {
		return featuredProgramNode;
	}

	@Override
	public AmstradFolderCoverImageProducer getImageProducer() {
		return (AmstradFolderCoverImageProducer) super.getImageProducer();
	}

}