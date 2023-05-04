package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import org.maia.amstrad.gui.components.ColoredTextArea;
import org.maia.amstrad.gui.components.ColoredTextLine;
import org.maia.amstrad.gui.components.ColoredTextSpan;
import org.maia.amstrad.gui.components.ScrollableItemList;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.util.StringUtils;

public abstract class AmstradWindowDisplaySource extends AmstradEmulatedDisplaySource {

	private String windowTitle;

	private boolean mouseOverButton;

	private boolean modalWindowOpen;

	private Rectangle modalWindowCloseButtonBounds;

	private long itemListCursorBlinkOffsetTime;

	private static long itemListCursorBlinkTimeInterval = 500L;

	protected AmstradWindowDisplaySource(AmstradPc amstradPc, String windowTitle) {
		super(amstradPc);
		setWindowTitle(windowTitle);
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		resetItemListCursorBlinkOffsetTime();
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
		canvas.pen(getWindowTitleColorIndex());
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
		canvas.paper(backgroundColorIndex);
		setModalWindowOpen(true);
		fillTextAreaWithSolidColor(tx1, ty1, tx2, ty2, backgroundColorIndex, canvas);
		renderModalWindowTitle(tx1, ty1, tx2, ty2, modalWindowTitle, canvas);
		renderWindowBorder(tx1, ty1, tx2, ty2, getModalWindowBorderColorIndex(), canvas);
		renderModalWindowCloseButton(tx1, ty1, tx2, ty2, canvas);
		canvas.paper(backgroundColorIndex);
	}

	private void renderModalWindowTitle(int tx1, int ty1, int tx2, int ty2, String modalWindowTitle,
			AmstradDisplayCanvas canvas) {
		int maxTitleWidth = tx2 - tx1 - 1;
		canvas.pen(getModalWindowTitleColorIndex());
		canvas.locate(tx1 + 1, ty1 + 1).print(StringUtils.fitWidth(modalWindowTitle, maxTitleWidth));
		canvas.locate(tx1 + 1, ty1 + 2);
		for (int i = 0; i < maxTitleWidth; i++)
			canvas.printChr(216);
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

	protected void renderWindowBorder(int tx1, int ty1, int tx2, int ty2, int colorIndex, AmstradDisplayCanvas canvas) {
		renderWindowBorder(tx1, ty1, tx2, ty2, colorIndex, null, canvas);
	}

	protected void renderWindowBorder(int tx1, int ty1, int tx2, int ty2, int colorIndex, String title,
			AmstradDisplayCanvas canvas) {
		int cpen = canvas.getPenColorIndex();
		canvas.pen(colorIndex);
		// Edges
		for (int i = tx1 + 1; i <= tx2 - 1; i++) {
			canvas.locate(i, ty1).printChr(154).locate(i, ty2).printChr(154);
		}
		for (int i = ty1 + 1; i <= ty2 - 1; i++) {
			canvas.locate(tx1, i).printChr(149).locate(tx2, i).printChr(149);
		}
		// Corners
		canvas.locate(tx1, ty1).printChr(150);
		canvas.locate(tx2, ty1).printChr(156);
		canvas.locate(tx1, ty2).printChr(147);
		canvas.locate(tx2, ty2).printChr(153);
		// Title
		if (title != null) {
			int maxTitleLength = Math.max(tx2 - tx1 - 3, 0);
			canvas.locate(tx1 + 2, ty1).print(StringUtils.truncate(title, maxTitleLength));
		}
		canvas.pen(cpen);
	}

	protected void fillTextAreaWithSolidColor(int textAreaX1, int textAreaY1, int textAreaX2, int textAreaY2,
			int colorIndex, AmstradDisplayCanvas canvas) {
		int cpaper = canvas.getPaperColorIndex();
		canvas.paper(colorIndex)
				.clearRect(canvas.getTextAreaBoundsOnCanvas(textAreaX1, textAreaY1, textAreaX2, textAreaY2));
		canvas.paper(cpaper);
	}

	protected void renderColoredTextArea(ColoredTextArea textArea, int tx0, int ty0, int maxWidth,
			AmstradDisplayCanvas canvas) {
		renderColoredTextArea(textArea, tx0, ty0, maxWidth, 13, 10, 24, true, canvas);
	}

	protected void renderColoredTextArea(ColoredTextArea textArea, int tx0, int ty0, int maxWidth,
			int rangeBarColorIndex, int rangeThumbColorIndex, int cursorColorIndex, boolean blinkingCursor,
			AmstradDisplayCanvas canvas) {
		int cpaper = canvas.getPaperColorIndex();
		int cpen = canvas.getPenColorIndex();
		// text area
		int ty = ty0;
		int i = textArea.getIndexOfFirstItemShowing();
		while (i < textArea.size() && ty < ty0 + textArea.getMaxItemsShowing()) {
			int txRemaining = maxWidth;
			canvas.locate(tx0, ty);
			ColoredTextLine line = textArea.getItem(i);
			for (ColoredTextSpan span : line.getTextSpans()) {
				String text = span.getText();
				if (text.length() > txRemaining)
					text = text.substring(0, txRemaining);
				canvas.paper(span.getPaperColorIndex()).pen(span.getPenColorIndex());
				canvas.print(text);
				txRemaining -= text.length();
			}
			// cursor
			if (textArea.getIndexOfSelectedItem() == i) {
				if (!blinkingCursor || isItemListCursorBlinkOn()) {
					canvas.move(canvas.getTextCursorBoundsOnCanvas(tx0 - 1, ty).getLocation());
					canvas.pen(cursorColorIndex).drawChrMonospaced(133);
				}
			}
			ty++;
			i++;
		}
		// range bar
		int theight = ty - ty0;
		if (textArea.size() > theight) {
			Rectangle rect = canvas.getTextCursorBoundsOnCanvas(tx0 + maxWidth, ty0);
			int rx0 = rect.x + 1;
			int ry0 = rect.y;
			int ry1 = canvas.getTextCursorBoundsOnCanvas(tx0, ty0 + theight).getLocation().y + 1;
			int rwidth = rect.width - 2;
			int rheight = ry0 - ry1 + 1;
			canvas.paper(rangeBarColorIndex).clearRect(rx0, ry0, rwidth, rheight);
			// range thumb
			double r = rheight / (double) textArea.size();
			int vy0 = ry0 - (int) Math.floor(r * textArea.getIndexOfFirstItemShowing());
			int vheight = (int) Math.ceil(r * theight);
			canvas.paper(rangeThumbColorIndex).clearRect(rx0, vy0, rwidth, vheight);
		}
		canvas.paper(cpaper).pen(cpen);
	}

	protected int getWindowTitleColorIndex() {
		return 23;
	}

	protected int getModalWindowTitleColorIndex() {
		return 23;
	}

	protected int getModalWindowBorderColorIndex() {
		return 14;
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
		resetItemListCursorBlinkOffsetTime();
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

	protected void handleKeyboardKeyInItemList(KeyEvent e, ScrollableItemList itemList) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_DOWN) {
			itemList.browseOneItemDown();
		} else if (keyCode == KeyEvent.VK_UP) {
			itemList.browseOneItemUp();
		} else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
			itemList.browseOnePageDown();
		} else if (keyCode == KeyEvent.VK_PAGE_UP) {
			itemList.browseOnePageUp();
		} else if (keyCode == KeyEvent.VK_HOME) {
			itemList.browseHome();
		} else if (keyCode == KeyEvent.VK_END) {
			itemList.browseEnd();
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

	protected boolean isItemListCursorBlinkOn() {
		long t = (System.currentTimeMillis() - itemListCursorBlinkOffsetTime) / itemListCursorBlinkTimeInterval;
		return t % 2 == 0;
	}

	protected void resetItemListCursorBlinkOffsetTime() {
		this.itemListCursorBlinkOffsetTime = System.currentTimeMillis();
	}

}