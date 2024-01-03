package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvasOverImage;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public abstract class AmstradEmulatedDisplaySource extends AmstradAbstractDisplaySource {

	private AmstradDisplayCanvasOverImage displayCanvas;

	private BufferedImage backdropImage;

	private int backgroundColorIndex;

	private Rectangle boundsOnDisplayComponent;

	private Point mousePositionOnCanvas;

	protected AmstradEmulatedDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public final void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		super.init(displayComponent, graphicsContext);
		setDisplayCanvas(new AmstradDisplayCanvasOverImage(graphicsContext));
		init(getDisplayCanvas());
		setBackgroundColorIndex(getDisplayCanvas().getPaperColorIndex());
	}

	protected void init(AmstradDisplayCanvas canvas) {
		// Subclasses may override this method
	}

	@Override
	public final void dispose(JComponent displayComponent) {
		dispose();
		getDisplayCanvas().dispose();
		super.dispose(displayComponent);
	}

	protected void dispose() {
		// Subclasses may override this method
	}

	@Override
	public final void renderOntoDisplay(Graphics2D display, Rectangle displayBounds,
			AmstradGraphicsContext graphicsContext) {
		AmstradDisplayCanvasOverImage canvas = getDisplayCanvas();
		Insets borderInsets = graphicsContext.getBorderInsetsForDisplaySize(displayBounds.getSize());
		Rectangle canvasBounds = updateDisplayCanvasBounds(displayBounds, borderInsets);
		// Opaque bottom layer
		display.setColor(canvas.getBorderColor());
		display.fillRect(displayBounds.x, displayBounds.y, displayBounds.width, displayBounds.height);
		// Backdrop
		if (hasBackdropImage()) {
			display.drawImage(getBackdropImage(), displayBounds.x, displayBounds.y, displayBounds.width,
					displayBounds.height, null);
		} else {
			canvas.paper(getBackgroundColorIndex()).cls();
		}
		// Content
		renderContent(canvas);
		BufferedImage canvasImage = canvas.getImage();
		display.drawImage(canvasImage, canvasBounds.x, canvasBounds.y, canvasBounds.width, canvasBounds.height, null);
	}

	protected abstract void renderContent(AmstradDisplayCanvas canvas);

	private Rectangle updateDisplayCanvasBounds(Rectangle displayBounds, Insets borderInsets) {
		int x = displayBounds.x + borderInsets.left;
		int y = displayBounds.y + borderInsets.top;
		int width = displayBounds.width - borderInsets.left - borderInsets.right;
		int height = displayBounds.height - borderInsets.top - borderInsets.bottom;
		Rectangle rect = new Rectangle(x, y, width, height);
		setBoundsOnDisplayComponent(rect);
		return rect;
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
		AmstradDisplayCanvas canvas = getDisplayCanvas();
		if (canvas != null) {
			Rectangle bounds = getBoundsOnDisplayComponent();
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

	private AmstradDisplayCanvasOverImage getDisplayCanvas() {
		return displayCanvas;
	}

	private void setDisplayCanvas(AmstradDisplayCanvasOverImage displayCanvas) {
		this.displayCanvas = displayCanvas;
	}

	public boolean hasBackdropImage() {
		return getBackdropImage() != null;
	}

	public BufferedImage getBackdropImage() {
		return backdropImage;
	}

	public void setBackdropImage(BufferedImage backdropImage) {
		this.backdropImage = backdropImage;
	}

	public int getBackgroundColorIndex() {
		return backgroundColorIndex;
	}

	public void setBackgroundColorIndex(int colorIndex) {
		this.backgroundColorIndex = colorIndex;
	}

	private Rectangle getBoundsOnDisplayComponent() {
		return boundsOnDisplayComponent;
	}

	private void setBoundsOnDisplayComponent(Rectangle bounds) {
		this.boundsOnDisplayComponent = bounds;
	}

	protected Point getMousePositionOnCanvas() {
		return mousePositionOnCanvas;
	}

	private void setMousePositionOnCanvas(Point mousePositionOnCanvas) {
		this.mousePositionOnCanvas = mousePositionOnCanvas;
	}

}