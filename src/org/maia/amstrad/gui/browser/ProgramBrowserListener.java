package org.maia.amstrad.gui.browser;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.util.AmstradListener;

public interface ProgramBrowserListener extends AmstradListener {

	void programLoadedFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program);

	void programRunFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program);

}