package org.maia.amstrad.gui.covers.repo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImage;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImageMaker;
import org.maia.amstrad.gui.covers.ImageDetailLevel;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.swing.layout.FillMode;

public class RepositoryProgramCoverImageProducer extends AmstradProgramCoverImageProducer
		implements AmstradProgramPosterImageMaker {

	private Color backgroundColorDark;

	private Color backgroundColorBright;

	public RepositoryProgramCoverImageProducer(Dimension imageSize) {
		super(imageSize, null);
	}

	public RepositoryProgramCoverImageProducer(Dimension imageSize, Color backgroundColorDark,
			Color backgroundColorBright) {
		this(imageSize);
		this.backgroundColorDark = backgroundColorDark;
		this.backgroundColorBright = backgroundColorBright;
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		AmstradProgramPosterImage posterImage = makePosterImage(programNode, getImageSize(), ImageDetailLevel.FULL);
		if (posterImage != null) {
			return posterImage.getImage();
		} else {
			return null;
		}
	}

	@Override
	public AmstradProgramPosterImage makePosterImage(ProgramNode programNode, Dimension size,
			ImageDetailLevel detailLevel) {
		AmstradProgramPosterImage posterImage = null;
		Image image = getCoverImageFromRepository(programNode);
		if (image != null) {
			Image framedImage = image;
			if (isChooseMatchingImageFrameColor()) {
				Color bgDark = getBackgroundColorDark();
				Color bgBright = getBackgroundColorBright();
				Color bg = chooseImageFrameColor(image, bgDark, bgBright, createRandomizer(programNode));
				framedImage = frameImageToSize(image, size, FillMode.FIT, bg);
			} else {
				framedImage = frameImageToSize(image);
			}
			posterImage = new AmstradProgramPosterImage(framedImage, false); // assuming titled
		}
		return posterImage;
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