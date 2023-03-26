package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public abstract class AmstradEmulatedDisplaySource extends KeyAdapter
		implements AmstradAlternativeDisplaySource, MouseListener, MouseMotionListener {

	private AmstradPc amstradPc;

	private AmstradEmulatedDisplayCanvas displayCanvas;

	private BufferedImage offscreenImage;

	private JComponent displayComponent;

	private Cursor displayComponentInitialCursor;

	private Point mousePositionOnCanvas;

	private AmstradKeyboardController keyboardController;

	private boolean catchKeyboardEvents;

	private boolean restoreMonitorSettingsOnDispose;

	private boolean followPrimaryDisplaySourceResolution;

	protected AmstradEmulatedDisplaySource(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
		setRestoreMonitorSettingsOnDispose(true);
		setFollowPrimaryDisplaySourceResolution(true);
	}

	/**
	 * Closes this display source
	 * <p>
	 * The default behavior is to turn back to the primary display source. This display source will then get disposed
	 * until it is swapped back in.
	 * </p>
	 * 
	 * @see AmstradMonitor#resetDisplaySource()
	 * @see AmstradMonitor#swapDisplaySource(AmstradAlternativeDisplaySource)
	 */
	public void close() {
		getAmstradPc().getMonitor().resetDisplaySource(); // will invoke dispose()
	}

	@Override
	public final void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext,
			AmstradKeyboardController keyboardController) {
		setDisplayCanvas(new AmstradEmulatedDisplayCanvas(graphicsContext));
		setDisplayComponent(displayComponent);
		setDisplayComponentInitialCursor(displayComponent.getCursor());
		setKeyboardController(keyboardController);
		resetCursor();
		displayComponent.addMouseListener(this);
		displayComponent.addMouseMotionListener(this);
		displayComponent.addKeyListener(this);
		acquireKeyboard();
		init(getDisplayCanvas());
	}

	@Override
	public final void dispose(JComponent displayComponent) {
		dispose();
		displayComponent.removeMouseListener(this);
		displayComponent.removeMouseMotionListener(this);
		displayComponent.removeKeyListener(this);
		releaseKeyboard();
		setCursor(getDisplayComponentInitialCursor());
		releaseOffscreenImage();
	}

	@Override
	public final void renderOntoDisplay(Graphics2D display, Rectangle displayBounds,
			AmstradGraphicsContext graphicsContext) {
		AmstradEmulatedDisplayCanvas canvas = getDisplayCanvas();
		Insets borderInsets = graphicsContext.getBorderInsetsForDisplaySize(displayBounds.getSize());
		updateDisplayCanvasBounds(displayBounds, borderInsets);
		Graphics2D drawingSurface = deriveDrawingSurface(display, displayBounds, borderInsets, graphicsContext);
		canvas.updateDrawingSurface(drawingSurface);
		canvas.rememberColors();
		canvas.cls();
		renderContent(canvas);
		if (getOffscreenImage() != null) {
			display.drawImage(getOffscreenImage(), displayBounds.x, displayBounds.y, displayBounds.width,
					displayBounds.height, null);
		} else {
			renderBorder(display, displayBounds, borderInsets);
		}
		drawingSurface.dispose();
		canvas.restoreColors();
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
		if (isFollowPrimaryDisplaySourceResolution() && primaryDisplaySourceIsScaled(displayBounds, graphicsContext)) {
			return deriveImageDrawingSurface(graphicsContext);
		} else {
			releaseOffscreenImage();
			return deriveDirectDrawingSurface(display, displayBounds, borderInsets, graphicsContext);
		}
	}

	private Graphics2D deriveImageDrawingSurface(AmstradGraphicsContext graphicsContext) {
		Dimension targetResolution = graphicsContext.getDisplayCanvasSize();
		Dimension imageResolution = graphicsContext.getPrimaryDisplaySourceResolution();
		BufferedImage image = provisionOffscreenImage(imageResolution);
		// Clear (paint border)
		Graphics2D g2 = image.createGraphics();
		g2.setColor(getDisplayCanvas().getBorderColor());
		g2.fillRect(0, 0, image.getWidth(), image.getHeight());
		// Transform for canvas
		Insets imageInsets = graphicsContext.getBorderInsetsForDisplaySize(imageResolution);
		Dimension canvasResolution = new Dimension(imageResolution.width - imageInsets.left - imageInsets.right,
				imageResolution.height - imageInsets.top - imageInsets.bottom);
		Graphics2D drawingSurface = (Graphics2D) g2.create(imageInsets.left, imageInsets.top, canvasResolution.width,
				canvasResolution.height);
		double scaleX = canvasResolution.getWidth() / targetResolution.getWidth();
		double scaleY = canvasResolution.getHeight() / targetResolution.getHeight();
		drawingSurface.scale(scaleX, scaleY);
		return drawingSurface;
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
		Graphics2D drawingSurface = (Graphics2D) display.create(displayBounds.x + borderInsets.left,
				displayBounds.y + borderInsets.top, paperWidth, paperHeight);
		drawingSurface.scale(scaleX, scaleY);
		return drawingSurface;
	}

	private boolean primaryDisplaySourceIsScaled(Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		Dimension primary = graphicsContext.getPrimaryDisplaySourceResolution();
		return primary.width != displayBounds.width || primary.height != displayBounds.height;
	}

	protected void init(AmstradDisplayCanvas canvas) {
		// Subclasses may override this method
	}

	protected void dispose() {
		// Subclasses may override this method
	}

	protected abstract void renderContent(AmstradDisplayCanvas canvas);

	protected Cursor getDefaultCursor() {
		// Subclasses may override this method
		if (getDisplayComponentInitialCursor() != null) {
			return getDisplayComponentInitialCursor();
		} else {
			return Cursor.getDefaultCursor();
		}
	}

	protected void resetCursor() {
		setCursor(getDefaultCursor());
	}

	protected void setCursor(Cursor cursor) {
		if (getDisplayComponent() != null) {
			if (cursor == null)
				cursor = getDefaultCursor();
			if (cursor != getDisplayComponent().getCursor()) {
				getDisplayComponent().setCursor(cursor);
			}
		}
	}

	@Override
	public final void mousePressed(MouseEvent e) {
		Point canvasPosition = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPosition != null) {
			mousePressedOnCanvas(getDisplayCanvas(), canvasPosition);
		}
	}

	@Override
	public final void mouseReleased(MouseEvent e) {
		Point canvasPosition = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPosition != null) {
			mouseReleasedOnCanvas(getDisplayCanvas(), canvasPosition);
		}
	}

	@Override
	public final void mouseClicked(MouseEvent e) {
		Point canvasPosition = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPosition != null) {
			mouseClickedOnCanvas(getDisplayCanvas(), canvasPosition);
		}
	}

	@Override
	public synchronized final void mouseMoved(MouseEvent e) {
		Point canvasPosition = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPosition != null) {
			if (!isMouseOnCanvas()) {
				setMousePositionOnCanvas(canvasPosition);
				mouseEnteredCanvas(getDisplayCanvas(), canvasPosition);
			} else {
				getMousePositionOnCanvas().setLocation(canvasPosition);
			}
			mouseMovedOnCanvas(getDisplayCanvas(), canvasPosition);
		} else {
			if (isMouseOnCanvas()) {
				setMousePositionOnCanvas(null);
				mouseExitedCanvas(getDisplayCanvas());
			}
		}
	}

	@Override
	public synchronized final void mouseDragged(MouseEvent e) {
		Point canvasPosition = mapDisplayToCanvasCoordinates(e.getPoint());
		if (canvasPosition != null) {
			if (!isMouseOnCanvas()) {
				setMousePositionOnCanvas(canvasPosition);
				mouseEnteredCanvas(getDisplayCanvas(), canvasPosition);
			} else {
				getMousePositionOnCanvas().setLocation(canvasPosition);
			}
			mouseDraggedOnCanvas(getDisplayCanvas(), canvasPosition);
		} else {
			if (isMouseOnCanvas()) {
				setMousePositionOnCanvas(null);
				mouseExitedCanvas(getDisplayCanvas());
			}
		}
	}

	@Override
	public synchronized final void mouseEntered(MouseEvent e) {
		if (!isMouseOnCanvas()) {
			Point canvasPosition = mapDisplayToCanvasCoordinates(e.getPoint());
			if (canvasPosition != null) {
				setMousePositionOnCanvas(canvasPosition);
				mouseEnteredCanvas(getDisplayCanvas(), canvasPosition);
			}
		}
	}

	@Override
	public synchronized final void mouseExited(MouseEvent e) {
		if (isMouseOnCanvas()) {
			setMousePositionOnCanvas(null);
			mouseExitedCanvas(getDisplayCanvas());
		}
	}

	protected void mousePressedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		// Subclasses may override this method
	}

	protected void mouseReleasedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		// Subclasses may override this method
	}

	protected void mouseClickedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		// Subclasses may override this method
	}

	protected void mouseMovedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		// Subclasses may override this method
	}

	protected void mouseDraggedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		// Subclasses may override this method
	}

	protected void mouseEnteredCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		// Subclasses may override this method
	}

	protected void mouseExitedCanvas(AmstradDisplayCanvas canvas) {
		// Subclasses may override this method
	}

	protected boolean isMouseOnCanvas() {
		return getMousePositionOnCanvas() != null;
	}

	protected boolean isMouseInCanvasBounds(Rectangle canvasBounds) {
		if (!isMouseOnCanvas())
			return false;
		Point p = getMousePositionOnCanvas();
		return p.x >= canvasBounds.x && p.x <= canvasBounds.x + canvasBounds.width - 1
				&& p.y >= canvasBounds.y - canvasBounds.height + 1 && p.y <= canvasBounds.y;
	}

	private Point mapDisplayToCanvasCoordinates(Point displayPosition) {
		Point canvasPosition = null;
		AmstradEmulatedDisplayCanvas canvas = getDisplayCanvas();
		if (canvas != null) {
			Rectangle bounds = canvas.getBoundsOnDisplayComponent();
			if (bounds != null) {
				int x = (int) Math.round((displayPosition.x - bounds.x) / bounds.getWidth() * canvas.getWidth());
				int y = (int) Math.round((displayPosition.y - bounds.y) / bounds.getHeight() * canvas.getHeight());
				if (x >= 0 && x < canvas.getWidth() && y >= 0 && y < canvas.getHeight()) {
					canvasPosition = new Point(x, canvas.getHeight() - 1 - y);
				}
			}
		}
		return canvasPosition;
	}

	public final synchronized void acquireKeyboard() {
		setCatchKeyboardEvents(true);
		getKeyboardController().sendKeyboardEventsToComputer(false);
	}

	public final synchronized void releaseKeyboard() {
		setCatchKeyboardEvents(false);
		getKeyboardController().sendKeyboardEventsToComputer(true);
	}

	@Override
	public final synchronized void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if (isCatchKeyboardEvents()) {
			keyboardKeyPressed(e);
		}
	}

	@Override
	public final synchronized void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		if (isCatchKeyboardEvents()) {
			keyboardKeyReleased(e);
		}
	}

	@Override
	public final synchronized void keyTyped(KeyEvent e) {
		super.keyTyped(e);
		if (isCatchKeyboardEvents()) {
			keyboardKeyTyped(e);
		}
	}

	protected void keyboardKeyPressed(KeyEvent e) {
		// Subclasses may override this method
	}

	protected void keyboardKeyReleased(KeyEvent e) {
		// Subclasses may override this method
	}

	protected void keyboardKeyTyped(KeyEvent e) {
		// Subclasses may override this method
	}

	public AmstradPc getAmstradPc() {
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

	private Cursor getDisplayComponentInitialCursor() {
		return displayComponentInitialCursor;
	}

	private void setDisplayComponentInitialCursor(Cursor cursor) {
		this.displayComponentInitialCursor = cursor;
	}

	protected Point getMousePositionOnCanvas() {
		return mousePositionOnCanvas;
	}

	private void setMousePositionOnCanvas(Point mousePositionOnCanvas) {
		this.mousePositionOnCanvas = mousePositionOnCanvas;
	}

	private AmstradKeyboardController getKeyboardController() {
		return keyboardController;
	}

	private void setKeyboardController(AmstradKeyboardController keyboardController) {
		this.keyboardController = keyboardController;
	}

	private boolean isCatchKeyboardEvents() {
		return catchKeyboardEvents;
	}

	private void setCatchKeyboardEvents(boolean catchKeyboardEvents) {
		this.catchKeyboardEvents = catchKeyboardEvents;
	}

	@Override
	public boolean isRestoreMonitorSettingsOnDispose() {
		return restoreMonitorSettingsOnDispose;
	}

	public void setRestoreMonitorSettingsOnDispose(boolean restore) {
		this.restoreMonitorSettingsOnDispose = restore;
	}

	/**
	 * Tells whether to follow the resolution of the primary display source
	 * <p>
	 * When <code>true</code> (the default), the resolution of this alternative display source matches the exact
	 * resolution of the <code>AmstradPc</code>'s primary display source
	 * </p>
	 * <p>
	 * When <code>false</code>, the resolution of this alternative display source matches the display window native
	 * resolution, which may be higher and therefore leading to a more accurate and sharper presentation
	 * </p>
	 * <p>
	 * Subclasses may override this method to choose the desired resolution
	 * </p>
	 * 
	 * @return <code>true</code> when this display source follows the primary display source's resolution
	 */
	public boolean isFollowPrimaryDisplaySourceResolution() {
		return followPrimaryDisplaySourceResolution;
	}

	public void setFollowPrimaryDisplaySourceResolution(boolean follow) {
		this.followPrimaryDisplaySourceResolution = follow;
	}

	private static class AmstradEmulatedDisplayCanvas extends AmstradDisplayCanvas {

		private Graphics2D drawingSurface;

		private Rectangle boundsOnDisplayComponent;

		private int rememberedBorderColorIndex;

		private int rememberedPaperColorIndex;

		private int rememberedPenColorIndex;

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

		@Override
		protected int projectY(int y) {
			return getHeight() - 1 - super.projectY(y);
		}

		@Override
		protected Rectangle getTextCursorBoundsOnGraphics2D(int cursorX, int cursorY) {
			int charWidth = getWidth() / getGraphicsContext().getTextColumns();
			int charHeight = getHeight() / getGraphicsContext().getTextRows();
			int xLeft = (cursorX - 1) * charWidth;
			int yTop = (cursorY - 1) * charHeight;
			return new Rectangle(xLeft, yTop, charWidth, charHeight);
		}

		public void rememberColors() {
			rememberedBorderColorIndex = getBorderColorIndex();
			rememberedPaperColorIndex = getPaperColorIndex();
			rememberedPenColorIndex = getPenColorIndex();
		}

		public void restoreColors() {
			border(rememberedBorderColorIndex);
			paper(rememberedPaperColorIndex);
			pen(rememberedPenColorIndex);
		}

	}

}