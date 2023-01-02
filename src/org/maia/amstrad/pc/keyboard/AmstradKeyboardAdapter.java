package org.maia.amstrad.pc.keyboard;

public abstract class AmstradKeyboardAdapter implements AmstradKeyboardListener {

	protected AmstradKeyboardAdapter() {
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		// Subclasses can override this
	}

	@Override
	public void amstradDoubleEscapeKeyPressed(AmstradKeyboard keyboard) {
		// Subclasses can override this
	}

}