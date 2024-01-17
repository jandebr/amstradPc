package org.maia.amstrad.pc.keyboard.virtual;

public class AmstradVirtualKeyboardStateAdapter implements AmstradVirtualKeyboardStateListener {

	protected AmstradVirtualKeyboardStateAdapter() {
	}

	@Override
	public void amstradVirtualKeyboardActivated(AmstradVirtualKeyboard keyboard) {
		// Subclasses can override this
	}

	@Override
	public void amstradVirtualKeyboardDeactivated(AmstradVirtualKeyboard keyboard) {
		// Subclasses can override this
	}

}