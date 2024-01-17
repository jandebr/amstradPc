package org.maia.amstrad.pc.keyboard.virtual;

import java.awt.Rectangle;

import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard.Key;

public interface AmstradVirtualKeyboardGridLayout extends AmstradVirtualKeyboardLayout {

	int getGridRows(AmstradVirtualKeyboard keyboard);

	int getGridColumns(AmstradVirtualKeyboard keyboard);

	Key getKeyInCell(int rowIndex, int columnIndex, AmstradVirtualKeyboard keyboard);

	Rectangle getCellBoundsOfKey(Key key, AmstradVirtualKeyboard keyboard);

}