package org.maia.amstrad.gui.browser.carousel.item;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public interface CarouselItemMaker {

	CarouselItem createCarouselItemForEmptyFolder(CarouselComponent comp);

	CarouselItem createCarouselItemForFolder(FolderNode folderNode, ProgramNode showcaseProgramNode,
			CarouselComponent comp);

	CarouselItem createCarouselItemForProgram(ProgramNode programNode, CarouselComponent comp);

}