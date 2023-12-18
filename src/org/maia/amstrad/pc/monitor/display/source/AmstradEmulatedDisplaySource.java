package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Cursor;
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
import org.maia.amstrad.pc.impl.joystick.AmstradJoystickDisplaySourceController;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvasOverImage;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public abstract class AmstradEmulatedDisplaySource extends KeyAdapter
		implements AmstradAlternativeDisplaySource, MouseListener, MouseMotionListener {

	private AmstradPc amstradPc;

	private AmstradJoystickDisplaySourceController joystickController;

	private AmstradDisplayCanvasOverImage displayCanvas;

	private BufferedImage backdropImage;

	private int backgroundColorIndex;

	private Rectangle boundsOnDisplayComponent;

	private JComponent displayComponent;

	private Cursor displayComponentInitialCursor;

	private Point mousePositionOnCanvas;

	private AmstradKeyboardController keyboardController;

	private boolean catchKeyboardEvents;

	private boolean restoreMonitorSettingsOnDispose;

	protected AmstradEmulatedDisplaySource(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
		this.joystickController = new AmstradJoystickDisplaySourceController(this);
		setRestoreMonitorSettingsOnDispose(false);
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
		setDisplayCanvas(new AmstradDisplayCanvasOverImage(graphicsContext));
		setDisplayComponent(displayComponent);
		setDisplayComponentInitialCursor(displayComponent.getCursor());
		setKeyboardController(keyboardController);
		resetCursor();
		displayComponent.addMouseListener(this);
		displayComponent.addMouseMotionListener(this);
		displayComponent.addKeyListener(this);
		getAmstradPc().getJoystick(AmstradJoystickID.JOYSTICK0).addJoystickEventListener(getJoystickController());
		acquireKeyboard();
		init(getDisplayCanvas());
		setBackgroundColorIndex(getDisplayCanvas().getPaperColorIndex());
	}

	protected void init(AmstradDisplayCanvas canvas) {
		// Subclasses may override this method
	}

	@Override
	public final void dispose(JComponent displayComponent) {
		dispose();
		displayComponent.removeMouseListener(this);
		displayComponent.removeMouseMotionListener(this);
		displayComponent.removeKeyListener(this);
		getAmstradPc().getJoystick(AmstradJoystickID.JOYSTICK0).removeJoystickEventListener(getJoystickController());
		releaseKeyboard();
		setCursor(getDisplayComponentInitialCursor());
		getDisplayCanvas().dispose();
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

	private AmstradJoystickDisplaySourceController getJoystickController() {
		return joystickController;
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

}