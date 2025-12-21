package org.maia.amstrad.program.browser.impl;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.carousel.CarouselProgramBrowserDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.browser.AmstradProgramBrowserStyle;
import org.maia.amstrad.program.repo.AmstradProgramRepository;

public class CarouselAmstradProgramBrowser extends AmstradProgramBrowser {

	CarouselAmstradProgramBrowser(AmstradPc amstradPc, AmstradProgramRepository programRepository,
			AmstradProgramBrowserStyle style) {
		super(amstradPc, programRepository, style);
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
	public boolean isShowPause() {
		return false;
	}

	@Override
	public boolean isShowControlKeys() {
		return false;
	}

	@Override
	public boolean isMonitorResizable() {
		return false;
	}

}