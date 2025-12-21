package org.maia.amstrad.program.browser.impl;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.browser.AmstradProgramBrowserStyle;
import org.maia.amstrad.program.repo.AmstradProgramRepository;

public class ClassicAmstradProgramBrowserStyle extends AmstradProgramBrowserStyle {

	public ClassicAmstradProgramBrowserStyle() {
		super("Classic");
	}

	@Override
	protected AmstradProgramBrowser createProgramBrowser(AmstradPc amstradPc, AmstradProgramRepository repository) {
		return new ClassicAmstradProgramBrowser(amstradPc, repository, this);
	}

}