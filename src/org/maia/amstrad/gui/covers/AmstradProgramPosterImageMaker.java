package org.maia.amstrad.gui.covers;

import java.awt.Dimension;

import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public interface AmstradProgramPosterImageMaker {

	AmstradProgramPosterImage makePosterImage(ProgramNode programNode, Dimension size, ImageDetailLevel detailLevel);

}