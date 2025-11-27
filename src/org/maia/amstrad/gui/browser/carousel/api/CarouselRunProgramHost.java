package org.maia.amstrad.gui.browser.carousel.api;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.browser.impl.CarouselAmstradProgramBrowser;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public interface CarouselRunProgramHost extends CarouselHost {

	void acquireKeyboard();

	void releaseKeyboard();

	void close();

	void notifyProgramRunFailState(ProgramNode programNode, boolean failed);

	AmstradPc getAmstradPc();

	CarouselAmstradProgramBrowser getProgramBrowser();

}