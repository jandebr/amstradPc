package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CassetteFolderCoverImageProducer extends AmstradFolderCoverImageProducer {

	public CassetteFolderCoverImageProducer(Dimension imageSize) {
		super(imageSize);
	}

	public CassetteFolderCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		super(imageSize, backgroundColor);
	}

	@Override
	protected Image produceImage(FolderNode folderNode, ProgramNode showcaseProgramNode) {
		return null; // TODO
	}

}