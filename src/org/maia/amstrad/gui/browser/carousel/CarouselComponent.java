package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItemMaker;
import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.swing.animate.itemslide.SlidingCursorMovement;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.animate.itemslide.impl.SlidingCursorFactory.SolidOutlineCursor;
import org.maia.swing.animate.itemslide.outline.SlidingItemListOutlineView;
import org.maia.util.ColorUtils;

public class CarouselComponent extends SlidingItemListComponent {

	private CarouselHost host;

	private CarouselItemMaker itemMaker;

	private FolderNode folderNode;

	public CarouselComponent(Dimension size, Insets padding, Color background, SlidingCursorMovement cursorMovement,
			Color cursorColor, CarouselHost host, CarouselItemMaker itemMaker) {
		super(size, padding, background, cursorMovement);
		setSlidingCursor(new CarouselCursor(cursorColor));
		this.host = host;
		this.itemMaker = itemMaker;
	}

	public synchronized void populateFolderContents(FolderNode folderNode, Node childNodeInFocus) {
		setFolderNode(folderNode);
		removeAllItems();
		if (folderNode.isEmpty()) {
			addItem(getItemMaker().createCarouselItemForEmptyFolder(this));
		} else {
			int focusIndex = -1;
			for (Node node : folderNode.getChildNodes()) {
				if (node.equals(childNodeInFocus)) {
					focusIndex = getItemCount();
				}
				if (node.isFolder()) {
					FolderNode childFolderNode = node.asFolder();
					ProgramNode featuredProgramNode = selectFeaturedProgramNode(childFolderNode);
					addItem(getItemMaker().createCarouselItemForFolder(childFolderNode, featuredProgramNode, this));
				} else {
					ProgramNode childProgramNode = node.asProgram();
					CarouselProgramItem item = getItemMaker().createCarouselItemForProgram(childProgramNode, this);
					item.setPreviousRunFailed(getHost().isFailedToRun(childProgramNode));
					addItem(item);
				}
			}
			if (focusIndex >= 0) {
				validateLayout();
				moveToItemIndex(focusIndex);
			}
		}
	}

	protected ProgramNode selectFeaturedProgramNode(FolderNode folderNode) {
		ProgramNode featured = folderNode.getFeaturedProgramNode();
		if (featured == null) {
			ProgramNode winnerWithCoverImage = null;
			ProgramNode winnerWithoutCoverImage = null;
			int winnerWithCoverImageBlocksOnTape = -1;
			int winnerWithoutCoverImageBlocksOnTape = -1;
			for (Node node : folderNode.getChildNodes()) {
				if (node.isProgram()) {
					ProgramNode programNode = node.asProgram();
					int blocks = programNode.getProgram().getBlocksOnTape();
					if (programNode.getCoverImage() != null) {
						if (blocks > winnerWithCoverImageBlocksOnTape) {
							winnerWithCoverImageBlocksOnTape = blocks;
							winnerWithCoverImage = programNode;
						}
					} else {
						if (blocks > winnerWithoutCoverImageBlocksOnTape) {
							winnerWithoutCoverImageBlocksOnTape = blocks;
							winnerWithoutCoverImage = programNode;
						}
					}
				}
			}
			if (winnerWithCoverImage != null) {
				featured = winnerWithCoverImage;
			} else if (winnerWithoutCoverImage != null) {
				featured = winnerWithoutCoverImage;
			}
		}
		return featured;
	}

	public CarouselOutline createOutline(int thickness) {
		return new CarouselOutline(this, thickness);
	}

	@Override
	public CarouselItem getItem(int index) {
		return (CarouselItem) super.getItem(index);
	}

	@Override
	public CarouselItem getSelectedItem() {
		return (CarouselItem) super.getSelectedItem();
	}

	public CarouselHost getHost() {
		return host;
	}

	private CarouselItemMaker getItemMaker() {
		return itemMaker;
	}

	public FolderNode getFolderNode() {
		return folderNode;
	}

	private void setFolderNode(FolderNode folderNode) {
		this.folderNode = folderNode;
	}

	private class CarouselCursor extends SolidOutlineCursor {

		public CarouselCursor(Color color) {
			super(color, 4, 4, true);
		}

		@Override
		protected Color getSlidingColor(SlidingItemListComponent component) {
			Color c = super.getSlidingColor(component);
			CarouselItem item = getSelectedItem();
			if (item == null || !item.isExecutable()) {
				c = ColorUtils.adjustSaturationAndBrightness(c, 1f, -0.8f);
			}
			return c;
		}

	}

	public static class CarouselOutline extends SlidingItemListOutlineView {

		private CarouselOutline(CarouselComponent component, int thickness) {
			super(component, thickness, component.getBackground());
		}

		@Override
		public CarouselComponent getComponent() {
			return (CarouselComponent) super.getComponent();
		}

	}

}