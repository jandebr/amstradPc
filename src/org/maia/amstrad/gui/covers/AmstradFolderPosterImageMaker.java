package org.maia.amstrad.gui.covers;

import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;

public interface AmstradFolderPosterImageMaker {

	Image makePosterImage(FolderNode folderNode, Dimension size);

}