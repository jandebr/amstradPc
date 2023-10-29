package org.maia.amstrad.gui.browser;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.util.GenericListener;

public interface ProgramBrowserListener extends GenericListener {

	void programLoadedFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program);

	void programRunFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program);

}