package org.maia.amstrad.gui.covers;

import java.awt.Image;
import java.awt.image.BufferedImage;

import org.maia.graphics2d.image.ImageUtils;

public class AmstradProgramPosterImage {

	private BufferedImage image;

	private boolean untitledImage;

	public AmstradProgramPosterImage(Image image, boolean untitledImage) {
		this.image = ImageUtils.convertToBufferedImage(image);
		this.untitledImage = untitledImage;
	}

	public BufferedImage getImage() {
		return image;
	}

	public boolean isUntitledImage() {
		return untitledImage;
	}

}