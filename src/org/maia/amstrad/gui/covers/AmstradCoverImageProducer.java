package org.maia.amstrad.gui.covers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.graphics2d.image.pool.PooledImageProducer;
import org.maia.swing.layout.FillMode;
import org.maia.swing.layout.HorizontalAlignment;
import org.maia.swing.layout.InnerRegionLayout;
import org.maia.swing.layout.VerticalAlignment;

public abstract class AmstradCoverImageProducer implements PooledImageProducer {

	private Dimension imageSize;

	private Color backgroundColor;

	protected AmstradCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		this.imageSize = imageSize;
		this.backgroundColor = backgroundColor;
	}

	protected String getProducerIdentifier() {
		StringBuilder sb = new StringBuilder(64);
		sb.append(getClass().getSimpleName());
		sb.append('_').append(getImageSize().width).append('x').append(getImageSize().height);
		if (getBackgroundColor() != null) {
			sb.append('c').append(getBackgroundColor().getRGB());
		}
		return sb.toString();
	}

	protected Image getCoverImageFromRepository(Node node) {
		Image image = null;
		AmstradProgramImage imageProxy = node.getCoverImage();
		if (imageProxy != null) {
			image = imageProxy.getImage();
			imageProxy.disposeImage(); // free up image pool
		}
		return image;
	}

	protected Image frameImageToSize(Image image) {
		return frameImageToSize(image, FillMode.FIT);
	}

	protected Image frameImageToSize(Image image, FillMode fillMode) {
		Color bg = getBackgroundColor();
		Dimension targetSize = getImageSize();
		Dimension sourceSize = ImageUtils.getSize(image);
		if (sourceSize.equals(targetSize)) {
			if (bg == null) {
				return image;
			} else if (image instanceof BufferedImage && ImageUtils.isFullyOpaque((BufferedImage) image)) {
				return image;
			}
		}
		InnerRegionLayout layout = new InnerRegionLayout(targetSize, sourceSize);
		layout.setHorizontalAlignment(HorizontalAlignment.CENTER);
		layout.setVerticalAlignment(VerticalAlignment.CENTER);
		layout.setFillMode(fillMode);
		Rectangle bounds = layout.getInnerRegionLayoutBounds();
		BufferedImage framedImage = ImageUtils.createImage(targetSize, bg);
		Graphics2D g2 = framedImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
		g2.dispose();
		return framedImage;
	}

	public Dimension getImageSize() {
		return imageSize;
	}

	public void setImageSize(Dimension imageSize) {
		this.imageSize = imageSize;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

}