package org.maia.amstrad.program.browser.impl;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.classic.ClassicProgramBrowserDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.repo.AmstradProgramRepository;

public class ClassicAmstradProgramBrowser extends AmstradProgramBrowser {

	public static final String STYLE_NAME = "Classic";

	public ClassicAmstradProgramBrowser(AmstradPc amstradPc, AmstradProgramRepository programRepository) {
		super(amstradPc, programRepository);
	}

	@Override
	protected ProgramBrowserDisplaySource createDisplaySource() {
		return new ClassicProgramBrowserDisplaySource(this);
	}

	@Override
	public boolean isShowMonitor() {
		return true;
	}

	@Override
	public boolean isShowControlKeys() {
		return true;
	}

}