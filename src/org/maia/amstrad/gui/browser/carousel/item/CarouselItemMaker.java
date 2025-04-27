package org.maia.amstrad.gui.browser.carousel.item;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public interface CarouselItemMaker {

	CarouselItem createCarouselItemForNode(Node repositoryNode, CarouselComponent comp);

	CarouselItem createCarouselItemForEmptyFolder(CarouselComponent comp);

}