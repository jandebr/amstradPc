package org.maia.amstrad.gui.browser.classic;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;

public class ClassicProgramInfoDisplaySource extends ClassicProgramBrowserDisplaySource {

	public ClassicProgramInfoDisplaySource(AmstradProgramBrowser programBrowser, AmstradProgram program) {
		super(programBrowser, program.getProgramName(), Window.PROGRAM_INFO_STANDALONE);
		setProgramInfoSheet(createProgramInfoSheet(program));
	}

}