package org.maia.amstrad.pc.keyboard.virtual;

import org.maia.util.GenericListener;

public interface AmstradVirtualKeyboardStateListener extends GenericListener {

	void amstradVirtualKeyboardActivated(AmstradVirtualKeyboard keyboard);

	void amstradVirtualKeyboardDeactivated(AmstradVirtualKeyboard keyboard);

}