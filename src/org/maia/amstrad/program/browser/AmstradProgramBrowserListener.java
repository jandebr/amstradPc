package org.maia.amstrad.program.browser;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.util.GenericListener;

public interface AmstradProgramBrowserListener extends GenericListener {

	void programLoadedFromBrowser(AmstradProgramBrowser programBrowser, AmstradProgram program);

	void programRunFromBrowser(AmstradProgramBrowser programBrowser, AmstradProgram program);

}