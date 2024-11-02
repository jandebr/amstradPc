package org.maia.amstrad.gui.browser;

import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;

public interface ProgramBrowserDisplaySource extends AmstradAlternativeDisplaySource {

	AmstradProgramBrowser getProgramBrowser();

	AmstradProgram getCurrentProgram();

}