package org.maia.amstrad.pc.monitor.display;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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

	@Override
	protected Rectangle getTextCursorBoundsOnGraphics2D(int cursorX, int cursorY, Rectangle returnValue) {
		int charWidth = getWidth() / getGraphicsContext().getTextColumns();
		int charHeight = getHeight() / getGraphicsContext().getTextRows();
		int xLeft = (cursorX - 1) * charWidth;
		int yTop = (cursorY - 1) * charHeight;
		if (returnValue == null) {
			return new Rectangle(xLeft, yTop, charWidth, charHeight);
		} else {
			returnValue.x = xLeft;
			returnValue.y = yTop;
			returnValue.width = charWidth;
			returnValue.height = charHeight;
			return returnValue;
		}
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