package org.maia.amstrad.program.browser;

import org.maia.amstrad.program.AmstradProgram;

public interface ProgramBrowserListener {

	void programLoadedFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program);

	void programRunFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program);

}