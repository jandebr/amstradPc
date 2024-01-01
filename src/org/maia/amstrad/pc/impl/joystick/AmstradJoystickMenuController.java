package org.maia.amstrad.pc.impl.joystick;

import java.awt.Component;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;

public abstract class AmstradJoystickMenuController extends AmstradJoystickEventTranslator {

	protected AmstradJoystickMenuController() {
	}

	@Override
	protected KeyEvent translateToKeyEvent(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		Component source = getKeyEventSource(event);
		int type = toKeyEventType(event);
		long when = System.currentTimeMillis();
		char cUnd = KeyEvent.CHAR_UNDEFINED;
		AmstradJoystickCommand command = event.getCommand();
		if (AmstradJoystickCommand.UP.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_UP, cUnd);
		} else if (AmstradJoystickCommand.DOWN.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_DOWN, cUnd);
		} else if (AmstradJoystickCommand.LEFT.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_LEFT, cUnd);
		} else if (AmstradJoystickCommand.RIGHT.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_RIGHT, cUnd);
		} else if (AmstradJoystickCommand.CONFIRM.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_ENTER, cUnd);
		} else if (AmstradJoystickCommand.CANCEL.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_ESCAPE, cUnd);
		}
		return keyEvent;
	}

}