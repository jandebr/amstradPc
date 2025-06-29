package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CassetteProgramCoverImageProducer extends AmstradProgramCoverImageProducer {

	public CassetteProgramCoverImageProducer(Dimension imageSize) {
		super(imageSize);
	}

	public CassetteProgramCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		super(imageSize, backgroundColor);
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		return null; // TODO
	}

}