package org.maia.amstrad.gui.covers;

import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public interface AmstradProgramPosterImageMaker {

	Image makePosterImage(ProgramNode programNode, Dimension size);

}