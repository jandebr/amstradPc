package org.maia.amstrad.gui.covers;

import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class AmstradProgramCoverImage extends AmstradCoverImage {

	public AmstradProgramCoverImage(ProgramNode programNode, AmstradProgramCoverImageProducer imageProducer) {
		super(programNode, imageProducer);
	}

	public ProgramNode getProgramNode() {
		return (ProgramNode) getNode();
	}

}