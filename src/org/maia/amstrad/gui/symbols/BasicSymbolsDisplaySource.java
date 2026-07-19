package org.maia.amstrad.gui.symbols;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSymbol;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.pc.monitor.display.source.AmstradWindowDisplaySource;
import org.maia.util.StringUtils;

public class BasicSymbolsDisplaySource extends AmstradWindowDisplaySource {

	private BasicSymbol[] systemSymbols;

	private BasicSymbol[] customizableSymbols;

	private int customizableSymbolsCount;

	private int gridCursorRow;

	private int gridCursorColumn;

	private boolean showSystemSymbols;

	private boolean showCustomSymbols;

	private static int SYMBOL_RANGE_FROM = 32;

	private static int SYMBOL_RANGE_TO = 255;

	private static int SYMBOLS_PER_GRIDROW = 16;

	private static int COLOR_BORDER = 1;

	private static int COLOR_PAPER = 1;

	private static int COLOR_WINDOW_BORDER = 14;

	private static int COLOR_SYMBOL_SYSTEM = 11;

	private static int COLOR_SYMBOL_CUSTOM = 10;

	private static int COLOR_CURSOR = 24;

	private static int COLOR_CURSOR_DARK = 12;

	public BasicSymbolsDisplaySource(AmstradPc amstradPc) {
		super(amstradPc, "Basic Symbols");
		setRestoreMonitorSettingsOnDispose(true); // as this source switches to COLOR
		setShowSystemSymbols(true);
		setShowCustomSymbols(true);
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		getAmstradPc().getMonitor().setMode(AmstradMonitorMode.COLOR);
		canvas.border(COLOR_BORDER).paper(COLOR_PAPER);
		setupRefreshButton(canvas);
		refresh();
	}

	@Override
	protected synchronized void renderWindowContent(AmstradDisplayCanvas canvas) {
		renderMenu(canvas);
		renderSymbolsGrid(canvas);
		renderCurrentSymbolIdentifier(canvas);
		renderCurrentSymbolMagnified(canvas);
		renderSymbolAfter(canvas);
	}

	private void renderMenu(AmstradDisplayCanvas canvas) {
		renderMenuSystemToggle(canvas);
		renderMenuCustomToggle(canvas);
		if (isMouseOverMenuSystemToggle(canvas) || isMouseOverMenuCustomToggle(canvas))
			setMouseOverButton(true);
	}

	private void renderMenuSystemToggle(AmstradDisplayCanvas canvas) {
		if (isShowSystemSymbols()) {
			canvas.paper(COLOR_SYMBOL_SYSTEM).pen(26);
		} else {
			canvas.paper(COLOR_PAPER).pen(COLOR_SYMBOL_SYSTEM);
		}
		canvas.locate(4, 4).print(" System ");
		canvas.paper(COLOR_SYMBOL_SYSTEM);
		canvas.pen(isMouseOverMenuSystemToggle(canvas) ? 24 : 26);
		canvas.locate(12, 4).print("  ");
		canvas.move(184, 351).drawChrMonospaced(isShowSystemSymbols() ? 231 : 230);
	}

	private void renderMenuCustomToggle(AmstradDisplayCanvas canvas) {
		if (isShowCustomSymbols()) {
			canvas.paper(COLOR_SYMBOL_CUSTOM).pen(26);
		} else {
			canvas.paper(COLOR_PAPER).pen(COLOR_SYMBOL_CUSTOM);
		}
		canvas.locate(17, 4).print(" Custom ");
		canvas.paper(COLOR_SYMBOL_CUSTOM);
		canvas.pen(isMouseOverMenuCustomToggle(canvas) ? 24 : 26);
		canvas.locate(25, 4).print("  ");
		canvas.move(392, 351).drawChrMonospaced(isShowCustomSymbols() ? 231 : 230);
	}

	private void renderSymbolsGrid(AmstradDisplayCanvas canvas) {
		canvas.paper(COLOR_PAPER);
		renderWindowBorder(4, 5, 26, 24, COLOR_WINDOW_BORDER, canvas);
		int cursorRow = getGridCursorRow();
		int cursorCol = getGridCursorColumn();
		for (int i = SYMBOL_RANGE_FROM; i <= SYMBOL_RANGE_TO; i++) {
			int gi = i - SYMBOL_RANGE_FROM;
			int row = Math.floorDiv(gi, SYMBOLS_PER_GRIDROW);
			int col = gi - row * SYMBOLS_PER_GRIDROW;
			int y0 = 314 - row * 20;
			int x0 = 74 + col * 20;
			BasicSymbol symbol = getSymbol(i);
			if (symbol != null) {
				canvas.pen(isSystemSymbol(i) ? COLOR_SYMBOL_SYSTEM : COLOR_SYMBOL_CUSTOM);
				renderSymbol(canvas, symbol, x0, y0);
			} else {
				canvas.pen(0);
				canvas.move(x0, y0).drawr(15, -15).mover(0, 15).drawr(-15, -15);
			}
			if (isItemListCursorBlinkOn() && row == cursorRow && col == cursorCol) {
				canvas.pen(symbol != null ? COLOR_CURSOR : COLOR_CURSOR_DARK);
				canvas.fillRect(x0 - 3, y0 + 3, 22, 2);
				canvas.fillRect(x0 - 3, y0 - 17, 22, 2);
				canvas.fillRect(x0 - 3, y0 + 3, 2, 22);
				canvas.fillRect(x0 + 17, y0 + 3, 2, 22);
			}
			if (i % SYMBOLS_PER_GRIDROW == 0) {
				String str = StringUtils.fitWidthRightAlign(String.valueOf(i), 3);
				canvas.paper(COLOR_PAPER).pen(COLOR_WINDOW_BORDER);
				canvas.move(0, y0).drawStrMonospaced(str);
			}
		}
	}

	private void renderCurrentSymbolIdentifier(AmstradDisplayCanvas canvas) {
		canvas.paper(COLOR_PAPER);
		renderWindowBorder(29, 5, 38, 9, COLOR_WINDOW_BORDER, canvas);
		canvas.locate(30, 6);
		int number = getGridCursorNumber();
		if (isSystemSymbol(number)) {
			canvas.pen(COLOR_SYMBOL_SYSTEM).print("System", true);
		} else if (isCustomSymbol(number)) {
			canvas.pen(COLOR_SYMBOL_CUSTOM).print("Custom", true);
		}
		canvas.pen(COLOR_CURSOR).locate(30, 7).print(String.valueOf(number), true);
		canvas.pen(COLOR_CURSOR_DARK).locate(30, 8).print("&" + Integer.toHexString(number).toUpperCase(), true);
	}

	private void renderCurrentSymbolMagnified(AmstradDisplayCanvas canvas) {
		canvas.paper(COLOR_PAPER);
		renderWindowBorder(29, 10, 38, 17, COLOR_WINDOW_BORDER, canvas);
		BasicSymbol symbol = getSymbol(getGridCursorNumber());
		if (symbol != null) {
			canvas.paper(COLOR_CURSOR_DARK).pen(COLOR_CURSOR);
			renderSymbol(canvas, symbol, 464, 239, 6, true);
			canvas.pen(COLOR_CURSOR_DARK);
			for (int i = 0; i < symbol.getValues().length; i++) {
				canvas.move(568, 236 - i * 12);
				canvas.drawStrProportional(String.valueOf(symbol.getValue(i)), 0.5f);
			}
		}
	}

	private void renderSymbolAfter(AmstradDisplayCanvas canvas) {
		canvas.pen(COLOR_SYMBOL_CUSTOM);
		canvas.locate(8, 25).print("SYMBOL AFTER " + (256 - getCustomizableSymbolsCount()), true);
	}

	private void renderSymbol(AmstradDisplayCanvas canvas, BasicSymbol symbol, int xLeft, int yTop) {
		renderSymbol(canvas, symbol, xLeft, yTop, 1, false);
	}

	private void renderSymbol(AmstradDisplayCanvas canvas, BasicSymbol symbol, int xLeft, int yTop, int scale,
			boolean raster) {
		int y = yTop;
		int d = 2 * scale;
		int[] values = symbol.getValues();
		for (int i = 0; i < values.length; i++) {
			int value = values[i];
			int bit = 128;
			int x = xLeft;
			for (int j = 0; j < 8; j++) {
				if ((value & bit) > 0) {
					canvas.fillRect(x, y, d, d);
				}
				x += d;
				bit = bit >>> 1;
			}
			y -= d;
		}
		if (raster) {
			int d8 = d * 8;
			canvas.pen(canvas.getPaperColorIndex());
			canvas.drawRect(xLeft, yTop, d8, d8);
			for (int i = 1; i < 8; i++) {
				canvas.move(xLeft, yTop - d * i).drawr(d8, 0);
				canvas.move(xLeft + d * i, yTop).drawr(0, -d8);
			}
		}
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		super.keyboardKeyPressed(e);
		handleKeyboardKeyInMenu(e);
		handleKeyboardKeyInSymbolsGrid(e);
	}

	private void handleKeyboardKeyInMenu(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_S) {
			toggleShowSystemSymbols();
		} else if (keyCode == KeyEvent.VK_C) {
			toggleShowCustomSymbols();
		}
	}

	private void handleKeyboardKeyInSymbolsGrid(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_NUMPAD2) {
			setGridCursorRow(getGridCursorRow() == getGridCursorRowLimit() ? 0 : getGridCursorRow() + 1);
		} else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_NUMPAD8) {
			setGridCursorRow(getGridCursorRow() == 0 ? getGridCursorRowLimit() : getGridCursorRow() - 1);
		} else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_NUMPAD6) {
			setGridCursorColumn(getGridCursorColumn() == getGridCursorColumnLimit() ? 0 : getGridCursorColumn() + 1);
		} else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_NUMPAD4) {
			setGridCursorColumn(getGridCursorColumn() == 0 ? getGridCursorColumnLimit() : getGridCursorColumn() - 1);
		} else if (keyCode == KeyEvent.VK_HOME) {
			setGridCursorRow(0);
			setGridCursorColumn(0);
		} else if (keyCode == KeyEvent.VK_END) {
			setGridCursorRow(getGridCursorRowLimit());
			setGridCursorColumn(getGridCursorColumnLimit());
		}
	}

	@Override
	protected void mouseClickedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		super.mouseClickedOnCanvas(canvas, canvasPosition);
		if (isMouseOverMenuSystemToggle(canvas)) {
			toggleShowSystemSymbols();
		} else if (isMouseOverMenuCustomToggle(canvas)) {
			toggleShowCustomSymbols();
		}
	}

	public void toggleShowSystemSymbols() {
		setShowSystemSymbols(!isShowSystemSymbols());
	}

	public void toggleShowCustomSymbols() {
		setShowCustomSymbols(!isShowCustomSymbols());
	}

	@Override
	public synchronized void refresh() {
		super.refresh();
		BasicRuntime rt = getAmstradPc().getBasicRuntime();
		setSystemSymbols(indexSymbols(rt.getSystemSymbols()));
		setCustomizableSymbols(indexSymbols(rt.getCustomizableSymbols()));
		setCustomizableSymbolsCount(rt.getCustomizableSymbolsCount());
	}

	private BasicSymbol[] indexSymbols(List<BasicSymbol> symbols) {
		BasicSymbol[] index = new BasicSymbol[SYMBOL_RANGE_TO + 1];
		for (BasicSymbol symbol : symbols) {
			index[symbol.getNumber()] = symbol;
		}
		return index;
	}

	private boolean isMouseOverMenuSystemToggle(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(12, 4, 13, 4));
	}

	private boolean isMouseOverMenuCustomToggle(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(25, 4, 26, 4));
	}

	@Override
	public AmstradAlternativeDisplaySourceType getType() {
		return AmstradAlternativeDisplaySourceType.OTHER;
	}

	private boolean isSystemSymbol(int number) {
		if (!isShowSystemSymbols())
			return false;
		if (number < SYMBOL_RANGE_FROM || number > SYMBOL_RANGE_TO)
			return false;
		if (isCustomSymbol(number))
			return false; // Custom takes precedence
		BasicSymbol symbol = getSystemSymbols()[number];
		if (symbol == null)
			return false;
		return true;
	}

	private boolean isCustomSymbol(int number) {
		if (!isShowCustomSymbols())
			return false;
		if (number < SYMBOL_RANGE_FROM || number > SYMBOL_RANGE_TO)
			return false;
		BasicSymbol symbol = getCustomizableSymbols()[number];
		if (symbol == null)
			return false;
		if (symbol.equals(getSystemSymbols()[number]))
			return false;
		return true;
	}

	private BasicSymbol getSymbol(int number) {
		BasicSymbol symbol = null;
		if (isSystemSymbol(number)) {
			symbol = getSystemSymbols()[number];
		} else if (isCustomSymbol(number)) {
			symbol = getCustomizableSymbols()[number];
		}
		return symbol;
	}

	private BasicSymbol[] getSystemSymbols() {
		return systemSymbols;
	}

	private void setSystemSymbols(BasicSymbol[] symbols) {
		this.systemSymbols = symbols;
	}

	private BasicSymbol[] getCustomizableSymbols() {
		return customizableSymbols;
	}

	private void setCustomizableSymbols(BasicSymbol[] symbols) {
		this.customizableSymbols = symbols;
	}

	private int getCustomizableSymbolsCount() {
		return customizableSymbolsCount;
	}

	private void setCustomizableSymbolsCount(int count) {
		this.customizableSymbolsCount = count;
	}

	private int getGridCursorNumber() {
		return SYMBOL_RANGE_FROM + getGridCursorRow() * SYMBOLS_PER_GRIDROW + getGridCursorColumn();
	}

	private int getGridCursorRowLimit() {
		return Math.floorDiv(SYMBOL_RANGE_TO - SYMBOL_RANGE_FROM, SYMBOLS_PER_GRIDROW);
	}

	private int getGridCursorRow() {
		return gridCursorRow;
	}

	private void setGridCursorRow(int row) {
		this.gridCursorRow = row;
	}

	private int getGridCursorColumnLimit() {
		return SYMBOLS_PER_GRIDROW - 1;
	}

	private int getGridCursorColumn() {
		return gridCursorColumn;
	}

	private void setGridCursorColumn(int column) {
		this.gridCursorColumn = column;
	}

	private boolean isShowSystemSymbols() {
		return showSystemSymbols;
	}

	private void setShowSystemSymbols(boolean show) {
		this.showSystemSymbols = show;
	}

	private boolean isShowCustomSymbols() {
		return showCustomSymbols;
	}

	private void setShowCustomSymbols(boolean show) {
		this.showCustomSymbols = show;
	}

}