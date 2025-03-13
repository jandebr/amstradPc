package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.swing.util.ImageUtils;

public class CarouselFolderItem extends CarouselRepositoryItem {

	private static Image coverImage = ImageUtils
			.readFromResource("org/maia/amstrad/gui/browser/carousel/image/buzz.png");

	public CarouselFolderItem(FolderNode folderNode, CarouselComponent carouselComponent, Dimension size, Insets margin,
			Font font) {
		super(folderNode, carouselComponent, size, margin, font);
	}

	@Override
	public void execute() {
		getHost().enterFolder(getFolderNode());
	}

	@Override
	protected Color getBackgroundColor() {
		return new Color(7, 36, 11);
	}

	@Override
	protected Image getCoverImage() {
		return coverImage;
	}

	public FolderNode getFolderNode() {
		return getRepositoryNode().asFolder();
	}

}