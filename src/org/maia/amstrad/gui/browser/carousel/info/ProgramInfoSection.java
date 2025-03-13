package org.maia.amstrad.gui.browser.carousel.info;

import org.maia.amstrad.gui.browser.carousel.CarouselComponentFactory;
import org.maia.amstrad.program.AmstradProgram;

public abstract class ProgramInfoSection extends CarouselInfoSection {

	private AmstradProgram program;

	protected ProgramInfoSection(InfoIcon icon, CarouselComponentFactory factory, AmstradProgram program) {
		super(icon, factory);
		this.program = program;
	}

	public AmstradProgram getProgram() {
		return program;
	}

}