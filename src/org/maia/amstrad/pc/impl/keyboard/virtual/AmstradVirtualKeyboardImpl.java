package org.maia.amstrad.pc.impl.keyboard.virtual;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboardGridLayout;

public class AmstradVirtualKeyboardImpl extends AmstradVirtualKeyboard {

	private static final int ctrl = KeyEvent.CTRL_DOWN_MASK;

	private static final int alt = KeyEvent.ALT_DOWN_MASK;

	private static final int shift = KeyEvent.SHIFT_DOWN_MASK;

	public AmstradVirtualKeyboardImpl(AmstradPc amstradPc) {
		super(amstradPc, new GridLayoutImpl());
		populateKeys();
	}

	protected void populateKeys() {
		addKeyGroup(createKeyGroupAlphabetUppercase());
		addKeyGroup(createKeyGroupAlphabetLowercase());
		addKeyGroup(createKeyGroupLetterSymbols());
		addKeyGroup(createKeyGroupArrows());
		addKeyGroup(createKeyGroupMathSymbols());
		addKeyGroup(createKeyGroupDecimalDigits());
		addKeyGroup(createKeyGroupControl());
	}

	protected KeyGroup createKeyGroupAlphabetUppercase() {
		int y = 0;
		KeyGroupOnGrid group = new KeyGroupOnGrid();
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_A, 'A', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_B, 'B', shift), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_C, 'C', shift), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_D, 'D', shift), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_E, 'E', shift), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_F, 'F', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_G, 'G', shift), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_H, 'H', shift), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_I, 'I', shift), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_J, 'J', shift), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_K, 'K', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_L, 'L', shift), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_M, 'M', shift), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_N, 'N', shift), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_O, 'O', shift), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_P, 'P', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_Q, 'Q', shift), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_R, 'R', shift), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_S, 'S', shift), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_T, 'T', shift), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_U, 'U', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_V, 'V', shift), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_W, 'W', shift), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_X, 'X', shift), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_Y, 'Y', shift), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_Z, 'Z', shift), 0, y));
		return group;
	}

	protected KeyGroup createKeyGroupAlphabetLowercase() {
		int y = 6;
		KeyGroupOnGrid group = new KeyGroupOnGrid();
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_A, 'a'), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_B, 'b'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_C, 'c'), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_D, 'd'), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_E, 'e'), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_F, 'f'), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_G, 'g'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_H, 'h'), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_I, 'i'), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_J, 'j'), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_K, 'k'), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_L, 'l'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_M, 'm'), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_N, 'n'), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_O, 'o'), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_P, 'p'), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_Q, 'q'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_R, 'r'), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_S, 's'), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_T, 't'), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_U, 'u'), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_V, 'v'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_W, 'w'), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_X, 'x'), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_Y, 'y'), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_Z, 'z'), 0, y));
		return group;
	}

	protected KeyGroup createKeyGroupLetterSymbols() {
		int y = 12;
		KeyGroupOnGrid group = new KeyGroupOnGrid();
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_EXCLAMATION_MARK, '!'), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_QUOTE, '"'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_NUMBER_SIGN, '#', ctrl | alt), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_DOLLAR, '$'), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_UNDEFINED, '£', shift), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_UNDEFINED, '%', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_AMPERSAND, '&'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_4, '\''), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_UNDERSCORE, '_', shift), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_MINUS, '-'), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_COMMA, '?', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_AT, '@', ctrl | alt), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_1, '|', ctrl | alt), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_BRACELEFT, '{', ctrl | alt), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_BRACERIGHT, '}', ctrl | alt), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_PERIOD, '.', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_COMMA, ','), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_COLON, ':'), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_SEMICOLON, ';'), 3, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_LEFT_PARENTHESIS, '('), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_RIGHT_PARENTHESIS, ')'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_DEAD_CIRCUMFLEX, '[', ctrl | alt), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_CLOSE_BRACKET, ']', ctrl | alt), 3, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_LESS, '<'), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_EQUALS, '='), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_GREATER, '>', shift), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_BACK_SLASH, '\\', ctrl | alt), 3, y));
		return group;
	}

	protected KeyGroup createKeyGroupArrows() {
		int y = 18;
		char cUnd = KeyEvent.CHAR_UNDEFINED;
		KeyGroupOnGrid group = new KeyGroupOnGrid();
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_UP, cUnd), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_DOWN, cUnd), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_LEFT, cUnd), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_RIGHT, cUnd), 3, y));
		return group;
	}

	protected KeyGroup createKeyGroupMathSymbols() {
		int y = 19;
		KeyGroupOnGrid group = new KeyGroupOnGrid();
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_ADD, '+'), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_SUBTRACT, '-'), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_MULTIPLY, '*'), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_DIVIDE, '/'), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_CIRCUMFLEX, '^', ctrl | alt), 4, y));
		return group;
	}

	protected KeyGroup createKeyGroupDecimalDigits() {
		int y = 20;
		KeyGroupOnGrid group = new KeyGroupOnGrid();
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_0, '0', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_1, '1', shift), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_2, '2', shift), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_3, '3', shift), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_4, '4', shift), 4, y++));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_5, '5', shift), 0, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_6, '6', shift), 1, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_7, '7', shift), 2, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_8, '8', shift), 3, y));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_9, '9', shift), 4, y));
		return group;
	}

	protected KeyGroup createKeyGroupControl() {
		char cUnd = KeyEvent.CHAR_UNDEFINED;
		KeyGroupOnGrid group = new KeyGroupOnGrid();
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_ESCAPE, cUnd), 1, 5, 2, 1));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_BACK_SPACE, cUnd), 3, 5, 2, 1));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_SPACE, cUnd), 1, 11, 4, 1));
		group.addKey(new KeyOnGrid(createPrototypeKeyEvent(KeyEvent.VK_ENTER, cUnd), 0, 22, 5, 1));
		return group;
	}

	protected KeyEvent createPrototypeKeyEvent(int keyCode, char keyChar) {
		return createPrototypeKeyEvent(keyCode, keyChar, 0);
	}

	protected KeyEvent createPrototypeKeyEvent(int keyCode, char keyChar, int keyModifiers) {
		Component source = getAmstradPc().getMonitor().getDisplayComponent();
		return new KeyEvent(source, 0, 0L, keyModifiers, keyCode, keyChar);
	}

	@Override
	protected boolean shouldDeactivateOnKeyRelease(Key key) {
		if (key.getKeyCode() != KeyEvent.VK_ENTER)
			return false;
		if (getAmstradPc().getBasicRuntime().isDirectModus())
			return false;
		return getAmstradPc().getMonitor().isPrimaryDisplaySourceShowing();
	}

	@Override
	public synchronized void addKeyGroup(KeyGroup group) {
		super.addKeyGroup(group);
		invalidateLayout();
	}

	protected void invalidateLayout() {
		getGridLayout().notifyKeysChanged(this);
	}

	@Override
	public AmstradVirtualKeyboardGridLayout getLayout() {
		return getGridLayout();
	}

	private GridLayoutImpl getGridLayout() {
		return (GridLayoutImpl) super.getLayout();
	}

	private class KeyOnGrid extends Key {

		private Rectangle cellBounds;

		public KeyOnGrid(KeyEvent prototypeEvent, int x, int y) {
			this(prototypeEvent, x, y, 1, 1);
		}

		public KeyOnGrid(KeyEvent prototypeEvent, int x, int y, int width, int height) {
			this(prototypeEvent, new Rectangle(x, y, width, height));
		}

		public KeyOnGrid(KeyEvent prototypeEvent, Rectangle cellBounds) {
			super(prototypeEvent);
			this.cellBounds = cellBounds;
		}

		public int getMinX() {
			return getCellBounds().x;
		}

		public int getMaxX() {
			return getCellBounds().x + getCellBounds().width - 1;
		}

		public int getMinY() {
			return getCellBounds().y;
		}

		public int getMaxY() {
			return getCellBounds().y + getCellBounds().height - 1;
		}

		public Rectangle getCellBounds() {
			return cellBounds;
		}

	}

	private class KeyGroupOnGrid extends KeyGroup {

		public KeyGroupOnGrid() {
		}

		@Override
		public void addKey(Key key) {
			super.addKey(key);
			invalidateLayout();
		}

	}

	private static class GridLayoutImpl implements AmstradVirtualKeyboardGridLayout {

		private boolean outdatedState = true;

		private KeyOnGrid[][] keysOnGrid;

		private int xTargetMovingVertically = NO_TARGET_VALUE;

		private static final int NO_TARGET_VALUE = -1;

		public GridLayoutImpl() {
		}

		public void notifyKeysChanged(AmstradVirtualKeyboard keyboard) {
			setOutdatedState(true);
		}

		@Override
		public Key getKeyMovingLeft(AmstradVirtualKeyboard keyboard) {
			ensureUpdatedState(keyboard);
			Key neighbor = null;
			Key key = keyboard.getKeyAtCursor();
			if (key != null && key instanceof KeyOnGrid) {
				KeyOnGrid kog = (KeyOnGrid) key;
				int y = kog.getMinY();
				int x = kog.getMinX();
				do {
					if (--x < 0)
						x = getGridColumns(keyboard) - 1; // wrap around
					neighbor = getKeyInCell(y, x, keyboard);
				} while (neighbor == null);
				setXtargetMovingVertically(x);
			}
			return neighbor;
		}

		@Override
		public Key getKeyMovingRight(AmstradVirtualKeyboard keyboard) {
			ensureUpdatedState(keyboard);
			Key neighbor = null;
			Key key = keyboard.getKeyAtCursor();
			if (key != null && key instanceof KeyOnGrid) {
				KeyOnGrid kog = (KeyOnGrid) key;
				int y = kog.getMinY();
				int x = kog.getMaxX();
				do {
					if (++x >= getGridColumns(keyboard))
						x = 0; // wrap around
					neighbor = getKeyInCell(y, x, keyboard);
				} while (neighbor == null);
				setXtargetMovingVertically(x);
			}
			return neighbor;
		}

		@Override
		public Key getKeyMovingUp(AmstradVirtualKeyboard keyboard) {
			ensureUpdatedState(keyboard);
			Key neighbor = null;
			Key key = keyboard.getKeyAtCursor();
			if (key != null && key instanceof KeyOnGrid) {
				KeyOnGrid kog = (KeyOnGrid) key;
				int y = kog.getMinY();
				int x = getXtargetMovingVertically();
				if (x == NO_TARGET_VALUE)
					x = kog.getMinX();
				do {
					if (--y < 0)
						y = getGridRows(keyboard) - 1; // wrap around
				} while (isEmptyRow(y));
				neighbor = getKeyInCell(y, x, keyboard);
				while (neighbor == null) {
					if (--x < 0)
						x = getGridColumns(keyboard) - 1; // wrap around
					neighbor = getKeyInCell(y, x, keyboard);
				}
			}
			return neighbor;
		}

		@Override
		public Key getKeyMovingDown(AmstradVirtualKeyboard keyboard) {
			ensureUpdatedState(keyboard);
			Key neighbor = null;
			Key key = keyboard.getKeyAtCursor();
			if (key != null && key instanceof KeyOnGrid) {
				KeyOnGrid kog = (KeyOnGrid) key;
				int y = kog.getMaxY();
				int x = getXtargetMovingVertically();
				if (x == NO_TARGET_VALUE)
					x = kog.getMinX();
				do {
					if (++y >= getGridRows(keyboard))
						y = 0; // wrap around
				} while (isEmptyRow(y));
				neighbor = getKeyInCell(y, x, keyboard);
				while (neighbor == null) {
					if (--x < 0)
						x = getGridColumns(keyboard) - 1; // wrap around
					neighbor = getKeyInCell(y, x, keyboard);
				}
			}
			return neighbor;
		}

		@Override
		public int getGridRows(AmstradVirtualKeyboard keyboard) {
			ensureUpdatedState(keyboard);
			return getKeysOnGrid().length;
		}

		@Override
		public int getGridColumns(AmstradVirtualKeyboard keyboard) {
			ensureUpdatedState(keyboard);
			if (getGridRows(keyboard) > 0) {
				return getKeysOnGrid()[0].length;
			} else {
				return 0;
			}
		}

		@Override
		public Key getKeyInCell(int rowIndex, int columnIndex, AmstradVirtualKeyboard keyboard) {
			ensureUpdatedState(keyboard);
			return getKeysOnGrid()[rowIndex][columnIndex];
		}

		@Override
		public Rectangle getCellBoundsOfKey(Key key, AmstradVirtualKeyboard keyboard) {
			ensureUpdatedState(keyboard);
			if (key instanceof KeyOnGrid) {
				return ((KeyOnGrid) key).getCellBounds();
			} else {
				return new Rectangle();
			}
		}

		private boolean isEmptyRow(int rowIndex) {
			KeyOnGrid[] rowCells = getKeysOnGrid()[rowIndex];
			for (int i = 0; i < rowCells.length; i++) {
				if (rowCells[i] != null)
					return false;
			}
			return true;
		}

		private void ensureUpdatedState(AmstradVirtualKeyboard keyboard) {
			if (isOutdatedState()) {
				updateState(keyboard);
				setXtargetMovingVertically(NO_TARGET_VALUE);
				setOutdatedState(false);
			}
		}

		private void updateState(AmstradVirtualKeyboard keyboard) {
			Dimension gridSize = computeGridSize(keyboard);
			setKeysOnGrid(new KeyOnGrid[gridSize.height][gridSize.width]);
			indexKeys(keyboard);
		}

		private Dimension computeGridSize(AmstradVirtualKeyboard keyboard) {
			Rectangle bounds = new Rectangle();
			for (KeyGroup group : keyboard.getKeyGroups()) {
				for (Key key : group.getKeys()) {
					if (key instanceof KeyOnGrid) {
						Rectangle keyBounds = ((KeyOnGrid) key).getCellBounds();
						bounds.add(keyBounds);
					}
				}
			}
			return bounds.getSize();
		}

		private void indexKeys(AmstradVirtualKeyboard keyboard) {
			for (KeyGroup group : keyboard.getKeyGroups()) {
				for (Key key : group.getKeys()) {
					if (key instanceof KeyOnGrid) {
						indexKey((KeyOnGrid) key);
					}
				}
			}
		}

		private void indexKey(KeyOnGrid key) {
			KeyOnGrid[][] grid = getKeysOnGrid();
			for (int y = key.getMinY(); y <= key.getMaxY(); y++) {
				for (int x = key.getMinX(); x <= key.getMaxX(); x++) {
					grid[y][x] = key;
				}
			}
		}

		private boolean isOutdatedState() {
			return outdatedState;
		}

		private void setOutdatedState(boolean outdated) {
			this.outdatedState = outdated;
		}

		private KeyOnGrid[][] getKeysOnGrid() {
			return keysOnGrid;
		}

		private void setKeysOnGrid(KeyOnGrid[][] keysOnGrid) {
			this.keysOnGrid = keysOnGrid;
		}

		private int getXtargetMovingVertically() {
			return xTargetMovingVertically;
		}

		private void setXtargetMovingVertically(int x) {
			this.xTargetMovingVertically = x;
		}

	}

}