package org.maia.amstrad.gui.browser.carousel.item;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public interface CarouselItemMaker {

	CarouselEmptyItem createCarouselItemForEmptyFolder(CarouselComponent comp);

	CarouselFolderItem createCarouselItemForFolder(FolderNode folderNode, ProgramNode featuredProgramNode,
			CarouselComponent comp);

	CarouselProgramItem createCarouselItemForProgram(ProgramNode programNode, CarouselComponent comp);

}