package org.maia.amstrad.pc.monitor.display;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.util.StringUtils;

public abstract class AmstradWindowDisplaySource extends AmstradEmulatedDisplaySource {

	private String windowTitle;

	private boolean mouseOverButton;

	private boolean modalWindowOpen;

	private Rectangle modalWindowCloseButtonBounds;

	protected AmstradWindowDisplaySource(AmstradPc amstradPc, String windowTitle) {
		super(amstradPc);
		setWindowTitle(windowTitle);
	}

	@Override
	protected final void renderContent(AmstradDisplayCanvas canvas) {
		setMouseOverButton(false);
		renderWindowTitleBar(canvas);
		renderWindowContent(canvas);
		updateCursor();
	}

	protected void renderWindowTitleBar(AmstradDisplayCanvas canvas) {
		renderWindowTitle(canvas);
		renderWindowCloseButton(canvas);
	}

	private void renderWindowTitle(AmstradDisplayCanvas canvas) {
		canvas.pen(23);
		canvas.locate(1, 1).print(StringUtils.fitWidthCenterAlign(getWindowTitle(), 40));
		canvas.locate(1, 2);
		for (int i = 0; i < 40; i++)
			canvas.printChr(216);
	}

	private void renderWindowCloseButton(AmstradDisplayCanvas canvas) {
		if (isFocusOnWindowCloseButton(canvas)) {
			setMouseOverButton(true);
			canvas.paper(6).pen(24);
		} else if (isModalWindowOpen()) {
			canvas.paper(3).pen(13);
		} else {
			canvas.paper(3).pen(26);
		}
		canvas.locate(39, 1).print("  ");
		canvas.move(616, 399).drawChrMonospaced('x');
	}

	private boolean isFocusOnWindowCloseButton(AmstradDisplayCanvas canvas) {
		return !isModalWindowOpen() && isMouseOverWindowCloseButton(canvas);
	}

	private boolean isMouseOverWindowCloseButton(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(39, 1, 40, 1));
	}

	protected abstract void renderWindowContent(AmstradDisplayCanvas canvas);

	protected void renderModalWindow(int tx1, int ty1, int tx2, int ty2, String modalWindowTitle,
			int backgroundColorIndex, AmstradDisplayCanvas canvas) {
		setModalWindowOpen(true);
		canvas.paper(backgroundColorIndex);
		canvas.clearRect(canvas.getTextAreaBoundsOnCanvas(tx1, ty1, tx2, ty2));
		renderModalWindowTitle(tx1, ty1, tx2, ty2, modalWindowTitle, canvas);
		renderModalWindowBorder(tx1, ty1, tx2, ty2, canvas);
		renderModalWindowCloseButton(tx1, ty1, tx2, ty2, canvas);
		canvas.paper(backgroundColorIndex);
	}

	private void renderModalWindowTitle(int tx1, int ty1, int tx2, int ty2, String modalWindowTitle,
			AmstradDisplayCanvas canvas) {
		int maxTitleWidth = tx2 - tx1 - 1;
		canvas.pen(23);
		canvas.locate(tx1 + 1, ty1 + 1).print(StringUtils.fitWidth(modalWindowTitle, maxTitleWidth));
		canvas.locate(tx1 + 1, ty1 + 2);
		for (int i = 0; i < maxTitleWidth; i++)
			canvas.printChr(216);
	}

	private void renderModalWindowBorder(int tx1, int ty1, int tx2, int ty2, AmstradDisplayCanvas canvas) {
		canvas.pen(14);
		for (int i = tx1 + 1; i <= tx2 - 1; i++) {
			canvas.locate(i, ty1).printChr(154).locate(i, ty2).printChr(154);
		}
		for (int i = ty1 + 1; i <= ty2 - 1; i++) {
			canvas.locate(tx1, i).printChr(149).locate(tx2, i).printChr(149);
		}
		canvas.locate(tx1, ty1).printChr(150);
		canvas.locate(tx2, ty1).printChr(156);
		canvas.locate(tx1, ty2).printChr(147);
		canvas.locate(tx2, ty2).printChr(153);
	}

	private void renderModalWindowCloseButton(int tx1, int ty1, int tx2, int ty2, AmstradDisplayCanvas canvas) {
		Rectangle closeBounds = canvas.getTextAreaBoundsOnCanvas(tx2 - 1, ty1, tx2, ty1);
		setModalWindowCloseButtonBounds(closeBounds);
		if (isFocusOnModalWindowCloseButton()) {
			setMouseOverButton(true);
			canvas.paper(6).pen(24);
		} else {
			canvas.paper(3).pen(26);
		}
		canvas.locate(tx2 - 1, ty1).print("  ");
		canvas.move(closeBounds.x + 8, closeBounds.y).drawChrMonospaced('x');
	}

	private boolean isFocusOnModalWindowCloseButton() {
		return isModalWindowOpen() && isMouseOverModalWindowCloseButton();
	}

	private boolean isMouseOverModalWindowCloseButton() {
		Rectangle bounds = getModalWindowCloseButtonBounds();
		if (bounds != null) {
			return isMouseInCanvasBounds(bounds);
		} else {
			return false;
		}
	}

	@Override
	protected void mouseClickedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		super.mouseClickedOnCanvas(canvas, canvasPosition);
		if (isFocusOnModalWindowCloseButton()) {
			closeModalWindow();
		} else if (isFocusOnWindowCloseButton(canvas)) {
			closeMainWindow();
		}
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		super.keyboardKeyPressed(e);
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ESCAPE) {
			if (isModalWindowOpen()) {
				closeModalWindow();
			} else {
				closeMainWindow();
			}
		}
	}

	public void closeModalWindow() {
		setModalWindowOpen(false);
		setModalWindowCloseButtonBounds(null);
	}

	public void closeMainWindow() {
		close();
	}

	private void updateCursor() {
		if (isMouseOverButton()) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			resetCursor();
		}
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	protected boolean isMouseOverButton() {
		return mouseOverButton;
	}

	protected void setMouseOverButton(boolean mouseOverButton) {
		this.mouseOverButton = mouseOverButton;
	}

	public boolean isModalWindowOpen() {
		return modalWindowOpen;
	}

	private void setModalWindowOpen(boolean modalWindowOpen) {
		this.modalWindowOpen = modalWindowOpen;
	}

	private Rectangle getModalWindowCloseButtonBounds() {
		return modalWindowCloseButtonBounds;
	}

	private void setModalWindowCloseButtonBounds(Rectangle bounds) {
		this.modalWindowCloseButtonBounds = bounds;
	}

}