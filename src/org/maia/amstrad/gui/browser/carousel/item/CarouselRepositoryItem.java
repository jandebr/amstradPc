package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.action.CarouselEnterFolderAction;
import org.maia.amstrad.gui.browser.carousel.action.CarouselRunProgramAction;
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
	public final void render(Graphics2D g, SlidingItemListComponent component) {
		Image image = getCoverImage().getImage();
		if (image != null) {
			Graphics2D g2 = (Graphics2D) g.create();
			if (isRenderFaded()) {
				paintBackground(g2, component);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
			}
			doRenderItem(g2, image);
			g2.dispose();
		} else {
			paintBackground(g, component);
		}
	}

	protected void doRenderItem(Graphics2D g, Image coverImage) {
		g.drawImage(coverImage, 0, 0, null);
	}

	protected boolean isRenderFaded() {
		Node node = getRepositoryNode();
		CarouselRunProgramAction programAction = getCarouselHost().getRunProgramActionInProgress();
		if (programAction != null) {
			return !node.equals(programAction.getProgramNode());
		}
		CarouselEnterFolderAction folderAction = getCarouselHost().getEnterFolderActionInProgress();
		if (folderAction != null) {
			return !node.equals(folderAction.getFolderNode());
		}
		return false;
	}

	public Node getRepositoryNode() {
		return repositoryNode;
	}

	public AmstradCoverImage getCoverImage() {
		return coverImage;
	}

}