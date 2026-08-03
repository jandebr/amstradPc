package org.maia.amstrad.program.browser.impl;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.classic.ClassicProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.classic.controlkeys.ProgramInfoControlKey;
import org.maia.amstrad.gui.browser.classic.controlkeys.ProgramMenuControlKey;
import org.maia.amstrad.gui.browser.classic.controlkeys.ProgramRunControlKey;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.browser.AmstradProgramBrowserStyle;
import org.maia.amstrad.program.repo.AmstradProgramRepository;

public class ClassicAmstradProgramBrowser extends AmstradProgramBrowser {

	ClassicAmstradProgramBrowser(AmstradPc amstradPc, AmstradProgramRepository programRepository,
			AmstradProgramBrowserStyle style) {
		super(amstradPc, programRepository, style);
		addAdditionalControlKey(new ProgramMenuControlKey(amstradPc));
		addAdditionalControlKey(new ProgramRunControlKey(amstradPc));
		addAdditionalControlKey(new ProgramInfoControlKey(amstradPc));
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
	public boolean isShowPause() {
		return true;
	}

	@Override
	public boolean isShowControlKeys() {
		return true;
	}

	@Override
	public boolean isMonitorResizable() {
		return true;
	}

}