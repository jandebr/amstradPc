package org.maia.amstrad.pc.joystick.keys;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;

public class AmstradJoystickMenuAdapter extends AmstradJoystickKeyEventAdapter {

	public AmstradJoystickMenuAdapter() {
	}

	@Override
	public AmstradJoystickKeyEvent translateToKeyEvent(AmstradJoystickEvent event) {
		AmstradJoystickKeyEvent keyEvent = null;
		AmstradJoystickCommand command = event.getCommand();
		AmstradJoystick joystick = event.getJoystick();
		int type = toKeyEventType(event);
		char cUnd = KeyEvent.CHAR_UNDEFINED;
		if (AmstradJoystickCommand.UP.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_UP, type, 0, KeyEvent.VK_UP,
					cUnd);
		} else if (AmstradJoystickCommand.DOWN.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_DOWN, type, 0,
					KeyEvent.VK_DOWN, cUnd);
		} else if (AmstradJoystickCommand.LEFT.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_LEFT, type, 0,
					KeyEvent.VK_LEFT, cUnd);
		} else if (AmstradJoystickCommand.RIGHT.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_RIGHT, type, 0,
					KeyEvent.VK_RIGHT, cUnd);
		} else if (AmstradJoystickCommand.CONFIRM.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_FIRE2, type, 0,
					KeyEvent.VK_ENTER, cUnd);
		} else if (AmstradJoystickCommand.CANCEL.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_FIRE1, type, 0,
					KeyEvent.VK_ESCAPE, cUnd);
		}
		return keyEvent;
	}

}