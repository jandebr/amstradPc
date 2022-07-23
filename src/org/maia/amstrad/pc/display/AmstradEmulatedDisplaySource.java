package org.maia.amstrad.pc.display;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradEmulatedDisplaySource extends KeyAdapter implements AmstradAlternativeDisplaySource,
		MouseListener, MouseMotionListener {

	private AmstradPc amstradPc;

	private AmstradEmulatedDisplayCanvas displayCanvas;

	private BufferedImage offscreenImage;

	private JComponent displayComponent;

	private Cursor displayComponentCursor;

	private boolean mouseOnCanvas;

	protected AmstradEmulatedDisplaySource(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		setDisplayCanvas(new AmstradEmulatedDisplayCanvas(graphicsContext));
		setDisplayComponent(displayComponent);
		setDisplayComponentCursor(displayComponent.getCursor());
		changeCursor(getDefaultCursor());
		displayComponent.addMouseListener(this);
		displayComponent.addMouseMotionListener(this);
		displayComponent.addKeyListener(this);
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		Insets borderInsets = deriveBorderInsets(displayBounds.getSize());
		updateDisplayCanvasBounds(displayBounds, borderInsets);
		renderBorder(display, displayBounds, borderInsets);
		Graphics2D drawingSurface = deriveDrawingSurface(display, displayBounds, borderInsets, graphicsContext);
		AmstradEmulatedDisplayCanvas canvas = getDisplayCanvas();
		canvas.updateDrawingSurface(drawingSurface);
		canvas.cls();
		renderContent(canvas);
		drawingSurface.dispose();
		if (getOffscreenImage() != null) {
			Rectangle rect = getDisplayCanvas().getBoundsOnDisplayComponent();
			display.drawImage(getOffscreenImage(), rect.x, rect.y, rect.width, rect.height, null);
		}
	}

	private Insets deriveBorderInsets(Dimension size) {
		double sy = size.height / 272.0;
		double sx = size.width / 384.0;
		int top = (int) Math.floor(sy * 40.0);
		int left = (int) Math.floor(sx * 32.0);
		int bottom = size.height - top - (int) Math.ceil(sy * 200.0);
		int right = size.width - left - (int) Math.ceil(sx * 320.0);
		return new Insets(top, left, bottom, right);
	}

	private void updateDisplayCanvasBounds(Rectangle displayBounds, Insets borderInsets) {
		int x = displayBounds.x + borderInsets.left;
		int y = displayBounds.y + borderInsets.top;
		int width = displayBounds.width - borderInsets.left - borderInsets.right;
		int height = displayBounds.height - borderInsets.top - borderInsets.bottom;
		getDisplayCanvas().updateBoundsOnDisplayComponent(new Rectangle(x, y, width, height));
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
		if (followPrimaryDisplaySourceResolution() && primaryDisplaySourceIsScaled(displayBounds, graphicsContext)) {
			return deriveImageDrawingSurface(graphicsContext);
		} else {
			releaseOffscreenImage();
			return deriveDirectDrawingSurface(display, displayBounds, borderInsets, graphicsContext);
		}
	}

	private Graphics2D deriveImageDrawingSurface(AmstradGraphicsContext graphicsContext) {
		Dimension targetResolution = graphicsContext.getDisplayCanvasSize();
		Dimension imageResolution = deriveOffscreenImageResolution(graphicsContext);
		BufferedImage image = provisionOffscreenImage(imageResolution);
		Graphics2D drawingSurface = image.createGraphics();
		double scaleX = imageResolution.getWidth() / targetResolution.getWidth();
		double scaleY = imageResolution.getHeight() / targetResolution.getHeight();
		drawingSurface.scale(scaleX, scaleY);
		return drawingSurface;
	}

	private Dimension deriveOffscreenImageResolution(AmstradGraphicsContext graphicsContext) {
		Dimension primaryResolution = graphicsContext.getPrimaryDisplaySourceResolution();
		Insets primaryInsets = deriveBorderInsets(primaryResolution);
		int width = primaryResolution.width - primaryInsets.left - primaryInsets.right;
		int height = primaryResolution.height - primaryInsets.top - primaryInsets.bottom;
		return new Dimension(width, height);
	}

	private BufferedImage provisionOffscreenImage(Dimension resolution) {
		BufferedImage image = getOffscreenImage();
		if (image != null && image.getWidth() == resolution.width && image.getHeight() == resolution.height) {
			return image;
		} else {
			// System.out.println("Create offscreen image with resolution: " + resolution);
			image = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_INT_ARGB);
			setOffscreenImage(image);
			return image;
		}
	}

	private void releaseOffscreenImage() {
		setOffscreenImage(null);
	}

	private Graphics2D deriveDirectDrawingSurface(Graphics2D display, Rectangle displayBounds, Insets borderInsets,
			AmstradGraphicsContext graphicsContext) {
		Dimension targetResolution = graphicsContext.getDisplayCanvasSize();
		int paperWidth = displayBounds.width - borderInsets.left - borderInsets.right;
		int paperHeight = displayBounds.height - borderInsets.top - borderInsets.bottom;
		double scaleX = paperWidth / targetResolution.getWidth();
		double scaleY = paperHeight / targetResolution.getHeight();
		Graphics2D drawingSurface = (Graphics2D) display.create();
		drawingSurface.scale(scaleX, scaleY);
		drawingSurface.translate(displayBounds.x + borderInsets.left, displayBounds.y + borderInsets.top);
		return drawingSurface;
	}

	private boolean primaryDisplaySourceIsScaled(Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		Dimension primary = graphicsContext.getPrimaryDisplaySourceResolution();
		return primary.width != displayBounds.width || primary.height != displayBounds.height;
	}

	/**
	 * Returns whether this alternative display source follows the resolution of the primary display source
	 * <p>
	 * By default, this is the case, however subclasses may override this
	 * </p>
	 * 
	 * @return <code>true</code> when this display source follows the primary display source's resolution
	 */
	protected boolean followPrimaryDisplaySourceResolution() {
		// Subclasses may override this
		return true;
	}

	protected abstract void renderContent(AmstradDisplayCanvas canvas);

	protected Cursor getDefaultCursor() {
		return getDisplayComponentCursor() != null ? getDisplayComponentCursor() : Cursor.getDefaultCursor();
	}

	protected void changeCursor(Cursor cursor) {
		if (getDisplayComponent() != null) {
			getDisplayComponent().setCursor(cursor);
		}
	}

	@Override
	public final void mousePressed(MouseEvent e) {
		Point canvasPoint = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPoint != null) {
			mousePressedOnCanvas(canvasPoint);
		}
	}

	@Override
	public final void mouseReleased(MouseEvent e) {
		Point canvasPoint = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPoint != null) {
			mouseReleasedOnCanvas(canvasPoint);
		}
	}

	@Override
	public final void mouseClicked(MouseEvent e) {
		Point canvasPoint = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPoint != null) {
			mouseClickedOnCanvas(canvasPoint);
		}
	}

	@Override
	public synchronized final void mouseMoved(MouseEvent e) {
		Point canvasPoint = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPoint != null) {
			if (!isMouseOnCanvas()) {
				setMouseOnCanvas(true);
				mouseEnteredCanvas(canvasPoint);
			}
			mouseMovedOnCanvas(canvasPoint);
		} else {
			if (isMouseOnCanvas()) {
				setMouseOnCanvas(false);
				mouseExitedCanvas();
			}
		}
	}

	@Override
	public synchronized final void mouseDragged(MouseEvent e) {
		Point canvasPoint = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPoint != null) {
			if (!isMouseOnCanvas()) {
				setMouseOnCanvas(true);
				mouseEnteredCanvas(canvasPoint);
			}
			mouseDraggedOnCanvas(canvasPoint);
		} else {
			if (isMouseOnCanvas()) {
				setMouseOnCanvas(false);
				mouseExitedCanvas();
			}
		}
	}

	@Override
	public synchronized final void mouseEntered(MouseEvent e) {
		if (!isMouseOnCanvas()) {
			Point canvasPoint = mapDisplayToCanvasCoordinates(e.getPoint());
			if (canvasPoint != null) {
				setMouseOnCanvas(true);
				mouseEnteredCanvas(canvasPoint);
			}
		}
	}

	@Override
	public synchronized final void mouseExited(MouseEvent e) {
		if (isMouseOnCanvas()) {
			setMouseOnCanvas(false);
			mouseExitedCanvas();
		}
	}

	protected void mousePressedOnCanvas(Point canvasPoint) {
		// Subclasses may override this
	}

	protected void mouseReleasedOnCanvas(Point canvasPoint) {
		// Subclasses may override this
	}

	protected void mouseClickedOnCanvas(Point canvasPoint) {
		// Subclasses may override this
	}

	protected void mouseMovedOnCanvas(Point canvasPoint) {
		// Subclasses may override this
	}

	protected void mouseDraggedOnCanvas(Point canvasPoint) {
		// Subclasses may override this
	}

	protected void mouseEnteredCanvas(Point canvasPoint) {
		// Subclasses may override this
	}

	protected void mouseExitedCanvas() {
		// Subclasses may override this
	}

	private Point mapDisplayToCanvasCoordinates(Point displayPoint) {
		Point canvasPoint = null;
		AmstradEmulatedDisplayCanvas canvas = getDisplayCanvas();
		if (canvas != null) {
			Rectangle bounds = canvas.getBoundsOnDisplayComponent();
			if (bounds != null) {
				int x = (int) Math.round((displayPoint.x - bounds.x) / bounds.getWidth() * canvas.getWidth());
				int y = (int) Math.round((displayPoint.y - bounds.y) / bounds.getHeight() * canvas.getHeight());
				if (x >= 0 && x < canvas.getWidth() && y >= 0 && y < canvas.getHeight()) {
					canvasPoint = new Point(x, canvas.getHeight() - 1 - y);
				}
			}
		}
		return canvasPoint;
	}

	/**
	 * Closes this display source
	 * <p>
	 * The default behavior is to turn back to the primary display source. This display source will then get disposed
	 * until it is swapped back in.
	 * </p>
	 * 
	 * @see AmstradPc#resetDisplaySource()
	 * @see AmstradPc#swapDisplaySource(AmstradAlternativeDisplaySource)
	 */
	public void close() {
		getAmstradPc().resetDisplaySource(); // will invoke dispose()
	}

	@Override
	public void dispose(JComponent displayComponent) {
		displayComponent.removeMouseListener(this);
		displayComponent.removeMouseMotionListener(this);
		displayComponent.removeKeyListener(this);
		changeCursor(getDisplayComponentCursor());
		releaseOffscreenImage();
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

	private JComponent getDisplayComponent() {
		return displayComponent;
	}

	private void setDisplayComponent(JComponent displayComponent) {
		this.displayComponent = displayComponent;
	}

	private Cursor getDisplayComponentCursor() {
		return displayComponentCursor;
	}

	private void setDisplayComponentCursor(Cursor displayComponentCursor) {
		this.displayComponentCursor = displayComponentCursor;
	}

	private boolean isMouseOnCanvas() {
		return mouseOnCanvas;
	}

	private void setMouseOnCanvas(boolean mouseOnCanvas) {
		this.mouseOnCanvas = mouseOnCanvas;
	}

	private static class AmstradEmulatedDisplayCanvas extends AmstradDisplayCanvas {

		private Graphics2D drawingSurface;

		private Rectangle boundsOnDisplayComponent;

		public AmstradEmulatedDisplayCanvas(AmstradGraphicsContext graphicsContext) {
			super(graphicsContext);
		}

		public void updateDrawingSurface(Graphics2D drawingSurface) {
			this.drawingSurface = drawingSurface;
		}

		public void updateBoundsOnDisplayComponent(Rectangle bounds) {
			this.boundsOnDisplayComponent = bounds;
		}

		public Rectangle getBoundsOnDisplayComponent() {
			return boundsOnDisplayComponent;
		}

		@Override
		protected Graphics2D getGraphics2D() {
			return drawingSurface;
		}

	}

}