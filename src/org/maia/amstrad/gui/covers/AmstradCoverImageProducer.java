package org.maia.amstrad.gui.covers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.graphics2d.image.pool.PooledImageProducer;

public abstract class AmstradCoverImageProducer implements PooledImageProducer {

	private Dimension imageSize;

	protected AmstradCoverImageProducer(Dimension imageSize) {
		this.imageSize = imageSize;
	}

	protected Image getDefaultCoverImage(Node node) {
		Image def = null;
		AmstradProgramImage img = node.getCoverImage();
		if (img != null) {
			def = img.getImage();
			img.disposeImage(); // free up cache
		} else {
			def = ImageUtils.createImage(getImageSize(), Color.BLACK);
		}
		return def;
	}

	protected Image fitImageCentered(Image image, Dimension size) {
		if (ImageUtils.getSize(image).equals(size)) {
			return image;
		} else {
			// TODO
			return null;
		}
	}

	protected Dimension getImageSize() {
		return imageSize;
	}

}