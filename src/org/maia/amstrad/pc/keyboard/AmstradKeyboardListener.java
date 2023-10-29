package org.maia.amstrad.pc.keyboard;

import org.maia.util.GenericListener;

public interface AmstradKeyboardListener extends GenericListener {

	void amstradKeyboardEventDispatched(AmstradKeyboardEvent event);

	void amstradKeyboardBreakEscaped(AmstradKeyboard keyboard);

}