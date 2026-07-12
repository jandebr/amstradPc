package org.maia.amstrad.pc.impl.jemu;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.keyboard.AmstradKeyboardAdapter;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEventFilter;

import jemu.ui.KeyDispatcher;
import jemu.ui.KeyDispatcherFilter;
import jemu.ui.Switches;

public class JemuKeyboardController extends AmstradKeyboardAdapter implements AmstradKeyboardController {

	private JemuKeyboard keyboard;

	private KeyDispatcher keyDispatcher;

	private int lastKeyModifiers;

	private boolean blockKeyboardPending;

	public JemuKeyboardController(JemuKeyboard keyboard, KeyDispatcher keyDispatcher) {
		this.keyboard = keyboard;
		this.keyDispatcher = keyDispatcher;
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

	@Override
	public void installKeyboardEventToComputerFilter(AmstradKeyboardEventFilter filter) {
		getKeyDispatcher().setKeyFilter(new KeyDispatcherFilter() {

			@Override
			public boolean accept(KeyEvent event) {
				return filter.accept(new AmstradKeyboardEvent(getKeyboard(), event));
			}
		});
	}

	@Override
	public void uninstallKeyboardEventToComputerFilter() {
		getKeyDispatcher().setKeyFilter(null);
	}

	@Override
	public synchronized final void resetKeyModifiers() {
		lastKeyModifiers = 0;
		getKeyDispatcher().resetKeyModifiers();
	}

	private JemuKeyboard getKeyboard() {
		return keyboard;
	}

	private KeyDispatcher getKeyDispatcher() {
		return keyDispatcher;
	}

}