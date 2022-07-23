package org.maia.amstrad.pc.display;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradEmulatedDisplaySource extends MouseAdapter implements AmstradAlternativeDisplaySource,
		KeyListener {

	private AmstradPc amstradPc;

	private AmstradEmulatedDisplayCanvas displayCanvas;

	private BufferedImage offscreenImage;

	protected AmstradEmulatedDisplaySource(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		setDisplayCanvas(new AmstradEmulatedDisplayCanvas(graphicsContext));
		displayComponent.addMouseListener(this);
		displayComponent.addMouseMotionListener(this);
		displayComponent.addKeyListener(this);
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		Insets borderInsets = deriveBorderInsets(displayBounds);
		renderBorder(display, displayBounds, borderInsets);
		Graphics2D drawingSurface = deriveDrawingSurface(display, displayBounds, borderInsets, graphicsContext);
		AmstradEmulatedDisplayCanvas canvas = getDisplayCanvas();
		canvas.updateDrawingSurface(drawingSurface);
		canvas.cls();
		renderContent(canvas);
		drawingSurface.dispose();
		if (getOffscreenImage() != null) {
			display.drawImage(getOffscreenImage(), displayBounds.x + borderInsets.left, displayBounds.y
					+ borderInsets.top, displayBounds.width - borderInsets.left - borderInsets.right,
					displayBounds.height - borderInsets.top - borderInsets.bottom, null);
		}
	}

	private Insets deriveBorderInsets(Rectangle displayBounds) {
		double sy = displayBounds.height / 272.0;
		double sx = displayBounds.width / 384.0;
		int top = (int) Math.floor(sy * 40.0);
		int left = (int) Math.floor(sx * 32.0);
		int bottom = displayBounds.height - top - (int) Math.round(sy * 200.0);
		int right = displayBounds.width - left - (int) Math.round(sx * 320.0);
		return new Insets(top, left, bottom, right);
	}

	private void renderBorder(Graphics2D display, Rectangle displayBounds, Insets borderInsets) {
		int w = displayBounds.width;
		int h = displayBounds.height;
		int hmid = h - borderInsets.top - borderInsets.bottom;
		int x0 = displayBounds.x;
		int y0 = displayBounds.y;
		int x1 = x0 + w - 1;
		int y1 = y0 + h - 1;
		display.setColor(getDisplayCanvas().getBorderColor());
		display.fillRect(x0, y0, w, borderInsets.top); // top
		display.fillRect(x0, y1 - borderInsets.bottom + 1, w, borderInsets.bottom); // bottom
		display.fillRect(x0, y0 + borderInsets.top, borderInsets.left, hmid); // left
		display.fillRect(x1 - borderInsets.right + 1, y0 + borderInsets.top, borderInsets.right, hmid); // right
	}

	private Graphics2D deriveDrawingSurface(Graphics2D display, Rectangle displayBounds, Insets borderInsets,
			AmstradGraphicsContext graphicsContext) {
		if (followPrimaryDisplaySourceResolution()
				&& !isSamePrimaryDisplaySourceResolution(displayBounds, graphicsContext)) {
			return deriveImageDrawingSurface(display, displayBounds, borderInsets, graphicsContext);
		} else {
			clearOffscreenImage();
			return deriveDirectDrawingSurface(display, displayBounds, borderInsets, graphicsContext);
		}
	}

	private Graphics2D deriveImageDrawingSurface(Graphics2D display, Rectangle displayBounds, Insets borderInsets,
			AmstradGraphicsContext graphicsContext) {
		Dimension targetResolution = graphicsContext.getDisplayCanvasResolution();
		Dimension imageResolution = graphicsContext.getPrimaryDisplaySourceResolution();
		BufferedImage image = deriveOffscreenImage(imageResolution);
		Graphics2D drawingSurface = image.createGraphics();
		double scaleX = imageResolution.getWidth() / targetResolution.getWidth();
		double scaleY = imageResolution.getHeight() / targetResolution.getHeight();
		drawingSurface.scale(scaleX, scaleY);
		return drawingSurface;
	}

	private BufferedImage deriveOffscreenImage(Dimension resolution) {
		BufferedImage image = getOffscreenImage();
		if (image != null && image.getWidth() == resolution.width && image.getHeight() == resolution.height) {
			return image;
		} else {
			image = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_INT_ARGB);
			setOffscreenImage(image);
			return image;
		}
	}

	private void clearOffscreenImage() {
		setOffscreenImage(null);
	}

	private Graphics2D deriveDirectDrawingSurface(Graphics2D display, Rectangle displayBounds, Insets borderInsets,
			AmstradGraphicsContext graphicsContext) {
		Dimension targetResolution = graphicsContext.getDisplayCanvasResolution();
		int paperWidth = displayBounds.width - borderInsets.left - borderInsets.right;
		int paperHeight = displayBounds.height - borderInsets.top - borderInsets.bottom;
		double scaleX = paperWidth / targetResolution.getWidth();
		double scaleY = paperHeight / targetResolution.getHeight();
		Graphics2D drawingSurface = (Graphics2D) display.create();
		drawingSurface.scale(scaleX, scaleY);
		drawingSurface.translate(borderInsets.left, borderInsets.top);
		return drawingSurface;
	}

	private boolean isSamePrimaryDisplaySourceResolution(Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		Dimension primary = graphicsContext.getPrimaryDisplaySourceResolution();
		return primary.width == displayBounds.width && primary.height == displayBounds.height;
	}

	protected boolean followPrimaryDisplaySourceResolution() {
		return true; // subclasses may override this
	}

	protected abstract void renderContent(AmstradDisplayCanvas canvas);

	public void close() {
		getAmstradPc().resetDisplaySource(); // will invoke dispose()
	}

	@Override
	public void dispose(JComponent displayComponent) {
		displayComponent.removeMouseListener(this);
		displayComponent.removeMouseMotionListener(this);
		displayComponent.removeKeyListener(this);
		clearOffscreenImage(); // release memory on garbage collection
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO convert to display canvas coordinates
		// TODO let subclasses handle it
		close(); // for testing only
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// System.out.println("typed '" + e.getKeyChar() + "'");
	}

	protected AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private AmstradEmulatedDisplayCanvas getDisplayCanvas() {
		return displayCanvas;
	}

	private void setDisplayCanvas(AmstradEmulatedDisplayCanvas displayCanvas) {
		this.displayCanvas = displayCanvas;
	}

	private BufferedImage getOffscreenImage() {
		return offscreenImage;
	}

	private void setOffscreenImage(BufferedImage offscreenImage) {
		this.offscreenImage = offscreenImage;
	}

	private static class AmstradEmulatedDisplayCanvas extends AmstradDisplayCanvas {

		private Graphics2D drawingSurface;

		public AmstradEmulatedDisplayCanvas(AmstradGraphicsContext graphicsContext) {
			super(graphicsContext);
		}

		public void updateDrawingSurface(Graphics2D drawingSurface) {
			this.drawingSurface = drawingSurface;
		}

		public Graphics2D getDrawingSurface() {
			return drawingSurface;
		}

		@Override
		protected Graphics2D getGraphics2D() {
			return getDrawingSurface();
		}

	}

}