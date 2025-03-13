package org.maia.amstrad.gui.browser.carousel;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;

public interface CarouselHost {

	void runProgram(AmstradProgram program);

	void enterFolder(FolderNode folderNode);

	boolean isFocusOnCarousel();

	boolean isFocusOnBreadcrumb();

}