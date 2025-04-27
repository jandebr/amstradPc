package org.maia.amstrad.gui.browser.carousel.breadcrumb;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;

public interface CarouselBreadcrumbItemMaker {

	CarouselBreadcrumbItem createBreadcrumbItemForFolder(FolderNode folderNode, CarouselBreadcrumb breadcrumb);

	CarouselBreadcrumbItem createBreadcrumbSeparatorItem(CarouselBreadcrumb breadcrumb);

}