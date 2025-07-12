package org.maia.amstrad.gui.browser.carousel.breadcrumb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.swing.animate.itemslide.SlidingCursorMovement;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;

public class CarouselBreadcrumb extends SlidingItemListComponent {

	private CarouselHost host;

	private CarouselBreadcrumbItemMaker itemMaker;

	public CarouselBreadcrumb(Dimension size, Insets padding, Color background, SlidingCursorMovement cursorMovement,
			CarouselHost host, CarouselBreadcrumbItemMaker itemMaker) {
		super(size, padding, background, cursorMovement);
		this.host = host;
		this.itemMaker = itemMaker;
	}

	public synchronized void syncWith(CarouselComponent comp) {
		removeAllItems();
		populateAncestorsUpTo(comp.getFolderNode());
		moveToLastItem();
	}

	private void populateAncestorsUpTo(FolderNode folderNode) {
		if (folderNode != null) {
			if (!folderNode.isRoot()) {
				populateAncestorsUpTo(folderNode.getParent());
			}
			if (hasItems()) {
				addItem(getItemMaker().createBreadcrumbSeparatorItem(this));
			}
			addItem(getItemMaker().createBreadcrumbItemForFolder(folderNode, this));
		}
	}

	@Override
	public CarouselBreadcrumbItem getItem(int index) {
		return (CarouselBreadcrumbItem) super.getItem(index);
	}

	@Override
	public CarouselBreadcrumbItem getSelectedItem() {
		return (CarouselBreadcrumbItem) super.getSelectedItem();
	}

	public CarouselHost getHost() {
		return host;
	}

	private CarouselBreadcrumbItemMaker getItemMaker() {
		return itemMaker;
	}

}