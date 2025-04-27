package org.maia.amstrad.gui.browser.carousel.breadcrumb;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.swing.animate.itemslide.SlidingItem;

public interface CarouselBreadcrumbItem extends SlidingItem {

	boolean isSeparator();

	CarouselBreadcrumb getCarouselBreadcrumb();

	FolderNode getFolderNode();

}