package org.maia.amstrad.pc.keyboard.virtual;

import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard.Key;

public interface AmstradVirtualKeyboardLayout {

	Key getKeyMovingLeft(AmstradVirtualKeyboard keyboard);

	Key getKeyMovingRight(AmstradVirtualKeyboard keyboard);

	Key getKeyMovingUp(AmstradVirtualKeyboard keyboard);

	Key getKeyMovingDown(AmstradVirtualKeyboard keyboard);

}