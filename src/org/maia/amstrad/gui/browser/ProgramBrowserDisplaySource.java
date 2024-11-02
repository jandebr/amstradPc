package org.maia.amstrad.gui.browser;

import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public interface ProgramBrowserDisplaySource extends AmstradAlternativeDisplaySource {

	void addListener(ProgramBrowserListener listener);

	void removeListener(ProgramBrowserListener listener);

}