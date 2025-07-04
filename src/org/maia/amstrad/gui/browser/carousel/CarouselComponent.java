package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItemMaker;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.swing.animate.itemslide.SlidingCursorMovement;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.animate.itemslide.outline.SlidingItemListOutlineView;

public class CarouselComponent extends SlidingItemListComponent {

	private CarouselItemMaker itemMaker;

	private FolderNode folderNode;

	public CarouselComponent(Dimension size, Insets padding, Color background, SlidingCursorMovement cursorMovement,
			CarouselItemMaker itemMaker) {
		super(size, padding, background, cursorMovement);
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
				CarouselItem item = null;
				if (node.isFolder()) {
					FolderNode childFolderNode = node.asFolder();
					ProgramNode showcaseProgramNode = selectShowcaseProgramNode(childFolderNode);
					item = getItemMaker().createCarouselItemForFolder(childFolderNode, showcaseProgramNode, this);
				} else {
					item = getItemMaker().createCarouselItemForProgram(node.asProgram(), this);
				}
				if (item != null) {
					addItem(item);
				}
			}
			if (focusIndex >= 0) {
				validateLayout();
				moveToItemIndex(focusIndex);
			}
		}
	}

	protected ProgramNode selectShowcaseProgramNode(FolderNode folderNode) {
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
			return winnerWithCoverImage;
		} else if (winnerWithoutCoverImage != null) {
			return winnerWithoutCoverImage;
		} else {
			return null;
		}
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

	private CarouselItemMaker getItemMaker() {
		return itemMaker;
	}

	public FolderNode getFolderNode() {
		return folderNode;
	}

	private void setFolderNode(FolderNode folderNode) {
		this.folderNode = folderNode;
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