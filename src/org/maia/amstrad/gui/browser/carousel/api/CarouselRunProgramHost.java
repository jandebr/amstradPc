package org.maia.amstrad.gui.browser.carousel.api;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.impl.CarouselAmstradProgramBrowser;

public interface CarouselRunProgramHost extends CarouselHost {

	void acquireKeyboard();

	void releaseKeyboard();

	void close();

	void notifyProgramRunFailState(AmstradProgram program, boolean failed);

	AmstradPc getAmstradPc();

	CarouselAmstradProgramBrowser getProgramBrowser();

}