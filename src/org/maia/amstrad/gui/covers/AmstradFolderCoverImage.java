package org.maia.amstrad.gui.covers;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class AmstradFolderCoverImage extends AmstradCoverImage {

	private ProgramNode showcaseProgramNode;

	public AmstradFolderCoverImage(FolderNode folderNode, AmstradFolderCoverImageProducer imageProducer) {
		super(folderNode, imageProducer);
	}

	public AmstradFolderCoverImage(FolderNode folderNode, ProgramNode showcaseProgramNode,
			AmstradFolderCoverImageProducer imageProducer) {
		this(folderNode, imageProducer);
		this.showcaseProgramNode = showcaseProgramNode;
	}

	public FolderNode getFolderNode() {
		return (FolderNode) getNode();
	}

	public ProgramNode getShowcaseProgramNode() {
		return showcaseProgramNode;
	}

}