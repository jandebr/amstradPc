package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.covers.AmstradCoverImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;

public abstract class CarouselRepositoryItem extends CarouselItem {

	private Node repositoryNode;

	private AmstradCoverImage coverImage;

	protected CarouselRepositoryItem(Node repositoryNode, CarouselComponent carouselComponent,
			AmstradCoverImage coverImage, Insets margin) {
		super(carouselComponent, coverImage.getImageSize(), margin);
		this.repositoryNode = repositoryNode;
		this.coverImage = coverImage;
	}

	public void preLoad() {
		getCoverImage().getImage();
	}

	@Override
	public void render(Graphics2D g, SlidingItemListComponent component) {
		Image image = getCoverImage().getImage();
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}

	public Node getRepositoryNode() {
		return repositoryNode;
	}

	public AmstradCoverImage getCoverImage() {
		return coverImage;
	}

}