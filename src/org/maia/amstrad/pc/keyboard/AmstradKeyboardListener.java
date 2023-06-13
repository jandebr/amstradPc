package org.maia.amstrad.pc.keyboard;

import org.maia.amstrad.util.AmstradListener;

public interface AmstradKeyboardListener extends AmstradListener {

	void amstradKeyboardEventDispatched(AmstradKeyboardEvent event);

	void amstradKeyboardBreakEscaped(AmstradKeyboard keyboard);

}