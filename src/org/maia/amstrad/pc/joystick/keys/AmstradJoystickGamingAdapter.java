package org.maia.amstrad.pc.joystick.keys;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;

public class AmstradJoystickGamingAdapter extends AmstradJoystickKeyEventAdapter {

	public AmstradJoystickGamingAdapter() {
	}

	@Override
	public AmstradJoystickKeyEvent translateToKeyEvent(AmstradJoystickEvent event) {
		AmstradJoystickKeyEvent keyEvent = null;
		if (isPrimaryJoystick(event)) {
			keyEvent = translateToKeyEventForPrimaryJoystick(event);
		} else if (isSecondaryJoystick(event)) {
			keyEvent = translateToKeyEventForSecondaryJoystick(event);
		}
		return keyEvent;
	}

	protected AmstradJoystickKeyEvent translateToKeyEventForPrimaryJoystick(AmstradJoystickEvent event) {
		AmstradJoystickKeyEvent keyEvent = null;
		AmstradJoystickCommand command = event.getCommand();
		AmstradJoystick joystick = event.getJoystick();
		int type = toKeyEventType(event);
		if (AmstradJoystickCommand.UP.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_UP, type, 0,
					KeyEvent.VK_NUMPAD8, '8');
		} else if (AmstradJoystickCommand.DOWN.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_DOWN, type, 0,
					KeyEvent.VK_NUMPAD2, '2');
		} else if (AmstradJoystickCommand.LEFT.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_LEFT, type, 0,
					KeyEvent.VK_NUMPAD4, '4');
		} else if (AmstradJoystickCommand.RIGHT.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_RIGHT, type, 0,
					KeyEvent.VK_NUMPAD6, '6');
		} else if (AmstradJoystickCommand.FIRE2.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_FIRE2, type, 0,
					KeyEvent.VK_NUMPAD5, '5');
		} else if (AmstradJoystickCommand.FIRE1.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_FIRE1, type, 0,
					KeyEvent.VK_NUMPAD0, '0');
		}
		return keyEvent;
	}

	protected AmstradJoystickKeyEvent translateToKeyEventForSecondaryJoystick(AmstradJoystickEvent event) {
		AmstradJoystickKeyEvent keyEvent = null;
		AmstradJoystickCommand command = event.getCommand();
		AmstradJoystick joystick = event.getJoystick();
		int type = toKeyEventType(event);
		if (AmstradJoystickCommand.UP.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_UP, type, 0, KeyEvent.VK_6,
					'6');
		} else if (AmstradJoystickCommand.DOWN.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_DOWN, type, 0, KeyEvent.VK_5,
					'5');
		} else if (AmstradJoystickCommand.LEFT.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_LEFT, type,
					InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_R, 'R');
		} else if (AmstradJoystickCommand.RIGHT.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_RIGHT, type,
					InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_T, 'T');
		} else if (AmstradJoystickCommand.FIRE2.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_FIRE2, type,
					InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_G, 'G');
		} else if (AmstradJoystickCommand.FIRE1.equals(command)) {
			keyEvent = new AmstradJoystickKeyEvent(joystick, AmstradJoystickKeyEvent.VALUE_FIRE1, type,
					InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_F, 'F');
		}
		return keyEvent;
	}

}