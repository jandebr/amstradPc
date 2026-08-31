package org.maia.amstrad.gui.covers;

import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public interface AmstradFolderPosterImageMaker {

	Image makePosterImage(FolderNode folderNode, ProgramNode featuredProgramNode, Dimension size);

}