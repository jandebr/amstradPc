package org.maia.amstrad.pc.keyboard;

public interface AmstradKeyboardListener {

	void amstradKeyboardEventDispatched(AmstradKeyboardEvent event);

	void amstradKeyboardBreakEscaped(AmstradKeyboard keyboard);

}