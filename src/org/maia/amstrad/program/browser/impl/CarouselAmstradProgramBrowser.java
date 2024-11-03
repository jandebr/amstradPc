package org.maia.amstrad.program.browser.impl;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.carousel.CarouselProgramBrowserDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.repo.AmstradProgramRepository;

public class CarouselAmstradProgramBrowser extends AmstradProgramBrowser {

	public static final String STYLE_NAME = "Carousel";

	public CarouselAmstradProgramBrowser(AmstradPc amstradPc, AmstradProgramRepository programRepository) {
		super(amstradPc, programRepository);
	}

	@Override
	protected ProgramBrowserDisplaySource createDisplaySource() {
		return new CarouselProgramBrowserDisplaySource(this);
	}

	@Override
	public boolean isShowMonitor() {
		return false;
	}

	@Override
	public boolean isShowControlKeys() {
		return false;
	}

}