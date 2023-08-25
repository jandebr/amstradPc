package org.maia.amstrad.pc.impl.jemu;

import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardAdapter;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

import jemu.ui.Switches;

public abstract class JemuKeyboardController extends AmstradKeyboardAdapter
		implements AmstradKeyboardController {

	private int lastKeyModifiers;

	private boolean blockKeyboardPending;

	protected JemuKeyboardController(JemuKeyboard keyboard) {
		keyboard.addKeyboardListener(this);
	}

	@Override
	public synchronized void sendKeyboardEventsToComputer(boolean sendToComputer) {
		if (sendToComputer) {
			Switches.blockKeyboard = false;
			blockKeyboardPending = false;
			resetKeyModifiers(); // essential to sync modifiers with JEMU's computer
		} else {
			if (lastKeyModifiers == 0) {
				Switches.blockKeyboard = true;
				blockKeyboardPending = false;
			} else {
				blockKeyboardPending = true;
			}
		}
	}

	@Override
	public synchronized void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		lastKeyModifiers = event.getKey().getModifiersEx();
		if (blockKeyboardPending && lastKeyModifiers == 0) {
			Switches.blockKeyboard = true;
			blockKeyboardPending = false;
		}
	}

	public synchronized final void resetKeyModifiers() {
		lastKeyModifiers = 0;
		doResetKeyModifiers();
	}

	protected abstract void doResetKeyModifiers();

}