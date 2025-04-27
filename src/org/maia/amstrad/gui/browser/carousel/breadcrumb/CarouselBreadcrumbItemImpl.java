package org.maia.amstrad.gui.browser.carousel.breadcrumb;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.swing.animate.itemslide.SlidingItemComponentAdapter;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.text.TextLabel;

public class CarouselBreadcrumbItemImpl extends SlidingItemComponentAdapter implements CarouselBreadcrumbItem {

	private CarouselBreadcrumb carouselBreadcrumb;

	private FolderNode folderNode;

	private Color color;

	private Color selectedColor;

	public CarouselBreadcrumbItemImpl(CarouselBreadcrumb carouselBreadcrumb, FolderNode folderNode, TextLabel label,
			Insets margin, Color selectedColor) {
		super(label, margin);
		this.carouselBreadcrumb = carouselBreadcrumb;
		this.folderNode = folderNode;
		this.color = label.getForeground();
		this.selectedColor = selectedColor;
	}

	@Override
	public void render(Graphics2D g, SlidingItemListComponent component) {
		if (!isSeparator()) {
			if (getHost().isFocusOnBreadcrumb() && equals(component.getSelectedItem())) {
				getComponent().setForeground(getSelectedColor());
			} else {
				getComponent().setForeground(getColor());
			}
		}
		super.render(g, component);
	}

	@Override
	public boolean isSeparator() {
		return getFolderNode() == null;
	}

	private CarouselHost getHost() {
		return getCarouselBreadcrumb().getHost();
	}

	@Override
	public CarouselBreadcrumb getCarouselBreadcrumb() {
		return carouselBreadcrumb;
	}

	@Override
	public FolderNode getFolderNode() {
		return folderNode;
	}

	private Color getColor() {
		return color;
	}

	private Color getSelectedColor() {
		return selectedColor;
	}

}