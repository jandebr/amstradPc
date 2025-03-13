package org.maia.amstrad.gui.browser.carousel;

import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public interface CarouselItemMaker {

	CarouselItem createCarouselItemForNode(Node repositoryNode, CarouselComponent comp);

	CarouselItem createCarouselItemForEmptyFolder(CarouselComponent comp);

}