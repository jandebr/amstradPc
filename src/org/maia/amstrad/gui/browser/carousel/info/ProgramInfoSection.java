package org.maia.amstrad.gui.browser.carousel.info;

import org.maia.amstrad.gui.browser.carousel.CarouselLayoutManager;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.program.AmstradProgram;

public abstract class ProgramInfoSection extends CarouselInfoSection {

	private AmstradProgram program;

	protected ProgramInfoSection(InfoIcon icon, CarouselProgramBrowserTheme theme, CarouselLayoutManager layout,
			AmstradProgram program) {
		super(icon, theme, layout);
		this.program = program;
	}

	public AmstradProgram getProgram() {
		return program;
	}

}