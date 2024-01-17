package org.maia.amstrad.pc.monitor.display;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class AmstradDisplayCanvasOverImage extends AmstradDisplayCanvas {

	private BufferedImage image;

	private Graphics2D drawingSurface;

	public AmstradDisplayCanvasOverImage(AmstradGraphicsContext graphicsContext) {
		super(graphicsContext);
		provisionImage();
	}

	private void provisionImage() {
		Dimension imageSize = getGraphicsContext().getDisplayCanvasSize();
		BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);
		setImage(image);
		setDrawingSurface(image.createGraphics());
		erase(); // make image fully transparent
	}

	@Override
	public void dispose() {
		super.dispose();
		getDrawingSurface().dispose();
		getImage().flush();
	}

	@Override
	protected Graphics2D getGraphics2D() {
		return getDrawingSurface();
	}

	@Override
	protected int projectY(int y) {
		return getHeight() - 1 - super.projectY(y);
	}

	public BufferedImage getImage() {
		return image;
	}

	private void setImage(BufferedImage image) {
		this.image = image;
	}

	private Graphics2D getDrawingSurface() {
		return drawingSurface;
	}

	private void setDrawingSurface(Graphics2D drawingSurface) {
		this.drawingSurface = drawingSurface;
	}

}