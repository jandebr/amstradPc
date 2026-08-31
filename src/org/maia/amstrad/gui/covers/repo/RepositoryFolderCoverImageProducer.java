package org.maia.amstrad.gui.covers.repo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradFolderPosterImageMaker;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.swing.layout.FillMode;

public class RepositoryFolderCoverImageProducer extends AmstradFolderCoverImageProducer
		implements AmstradFolderPosterImageMaker {

	private Color backgroundColorDark;

	private Color backgroundColorBright;

	public RepositoryFolderCoverImageProducer(Dimension imageSize) {
		super(imageSize, null);
	}

	public RepositoryFolderCoverImageProducer(Dimension imageSize, Color backgroundColorDark,
			Color backgroundColorBright) {
		this(imageSize);
		this.backgroundColorDark = backgroundColorDark;
		this.backgroundColorBright = backgroundColorBright;
	}

	@Override
	protected Image produceImage(FolderNode folderNode, ProgramNode featuredProgramNode) {
		return makePosterImage(folderNode, featuredProgramNode, getImageSize());
	}

	@Override
	public Image makePosterImage(FolderNode folderNode, ProgramNode featuredProgramNode, Dimension size) {
		Image image = getCoverImageFromRepository(folderNode);
		if (image != null) {
			if (isChooseMatchingImageFrameColor()) {
				Color bgDark = getBackgroundColorDark();
				Color bgBright = getBackgroundColorBright();
				Color bg = chooseImageFrameColor(image, bgDark, bgBright, createRandomizer(folderNode));
				image = frameImageToSize(image, size, FillMode.FIT, bg);
			} else {
				image = frameImageToSize(image);
			}
		}
		return image;
	}

	protected boolean isChooseMatchingImageFrameColor() {
		return getBackgroundColorDark() != null && getBackgroundColorBright() != null;
	}

	public Color getBackgroundColorDark() {
		return backgroundColorDark;
	}

	public Color getBackgroundColorBright() {
		return backgroundColorBright;
	}

}