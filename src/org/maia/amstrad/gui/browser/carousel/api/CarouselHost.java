package org.maia.amstrad.gui.browser.carousel.api;

import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.action.CarouselEnterFolderAction;
import org.maia.amstrad.gui.browser.carousel.action.CarouselRunProgramAction;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public interface CarouselHost {

	void enterFolderAsync(FolderNode folderNode);

	void runProgramAsync(ProgramNode programNode);

	boolean isFailedToRun(ProgramNode programNode);

	boolean isFocusOnCarousel();

	boolean isFocusOnBreadcrumb();

	CarouselEnterFolderAction getEnterFolderActionInProgress();

	CarouselRunProgramAction getRunProgramActionInProgress();

	CarouselItem getCarouselItem(Node node);

	Rectangle getCarouselItemBounds(Node node);

	CarouselComponent getCarouselComponent();

}