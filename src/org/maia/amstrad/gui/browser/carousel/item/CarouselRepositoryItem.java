package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.action.CarouselEnterFolderAction;
import org.maia.amstrad.gui.browser.carousel.action.CarouselRunProgramAction;
import org.maia.amstrad.gui.covers.AmstradCoverImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;

public abstract class CarouselRepositoryItem extends CarouselItem {

	private Node repositoryNode;

	private AmstradCoverImage coverImage;

	private Rectangle shrinkedCoverImageBounds;

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
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
			}
			Graphics2D gOverlays = g2;
			if (isRenderShrinked()) {
				Rectangle bounds = getShrinkedCoverImageBounds();
				g2.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
				gOverlays = createShrinkedGraphics2D(g2);
			} else {
				g2.drawImage(image, 0, 0, null);
			}
			renderOverlays(gOverlays);
			gOverlays.dispose();
			g2.dispose();
		}
	}

	protected void renderOverlays(Graphics2D g) {
		// subclasses may override
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

	protected boolean isRenderShrinked() {
		CarouselComponent carousel = getCarouselComponent();
		if (!carousel.isLanded())
			return true;
		if (!carousel.isSelectedItem(this))
			return true;
		if (!carousel.getCursorInnerBoundsInComponent().contains(carousel.getItemBoundsInComponent(this)))
			return true;
		return false;
	}

	private Graphics2D createShrinkedGraphics2D(Graphics2D g) {
		double scale = getShrinkedScale();
		Rectangle bounds = getShrinkedCoverImageBounds();
		Graphics2D gShrinked = (Graphics2D) g.create();
		gShrinked.translate(bounds.x, bounds.y);
		gShrinked.scale(scale, scale);
		return gShrinked;
	}

	private Rectangle createShrinkedCoverImageBounds() {
		Dimension fullSize = getCoverImage().getImageSize();
		int baseline = getCoverImage().getImageProducer().getCoverImageBaselineMeasuredFromBottom();
		float scale = getShrinkedScale();
		int shrinkedWidth = Math.round(fullSize.width * scale);
		int shrinkedHeight = Math.round(fullSize.height * scale);
		int x0 = (fullSize.width - shrinkedWidth) / 2;
		int y0 = (int) Math.floor((fullSize.height - baseline) * (1f - scale));
		return new Rectangle(x0, y0, shrinkedWidth, shrinkedHeight);
	}

	protected float getShrinkedScale() {
		return 0.85f;
	}

	public Node getRepositoryNode() {
		return repositoryNode;
	}

	public AmstradCoverImage getCoverImage() {
		return coverImage;
	}

	protected Rectangle getShrinkedCoverImageBounds() {
		if (shrinkedCoverImageBounds == null) {
			shrinkedCoverImageBounds = createShrinkedCoverImageBounds();
		}
		return shrinkedCoverImageBounds;
	}

}