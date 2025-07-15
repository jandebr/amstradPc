package org.maia.amstrad.gui.browser.carousel;

import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.CarouselProgramBrowserDisplaySourceSkeleton.EnterFolderAction;
import org.maia.amstrad.gui.browser.carousel.CarouselProgramBrowserDisplaySourceSkeleton.RunProgramAction;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public interface CarouselHost {

	void enterFolderAsync(FolderNode folderNode);

	void runProgramAsync(ProgramNode programNode);

	boolean isFailedToRun(ProgramNode programNode);

	boolean isFocusOnCarousel();

	boolean isFocusOnBreadcrumb();

	EnterFolderAction getEnterFolderActionInProgress();

	RunProgramAction getRunProgramActionInProgress();

	Rectangle getCarouselItemBounds(Node node);

}