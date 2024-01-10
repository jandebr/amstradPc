package org.maia.amstrad.pc.joystick.keys;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;

public class AmstradJoystickGamingAdapter extends AmstradJoystickKeyEventAdapter {

	public AmstradJoystickGamingAdapter() {
	}

	@Override
	public KeyEvent translateToKeyEvent(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		if (isPrimaryJoystick(event)) {
			keyEvent = translateToKeyEventForPrimaryJoystick(event);
		} else if (isSecondaryJoystick(event)) {
			keyEvent = translateToKeyEventForSecondaryJoystick(event);
		}
		return keyEvent;
	}

	protected KeyEvent translateToKeyEventForPrimaryJoystick(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		Component source = getKeyEventSource(event);
		int type = toKeyEventType(event);
		long when = System.currentTimeMillis();
		AmstradJoystickCommand command = event.getCommand();
		if (AmstradJoystickCommand.UP.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_NUMPAD8, '8');
		} else if (AmstradJoystickCommand.DOWN.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_NUMPAD2, '2');
		} else if (AmstradJoystickCommand.LEFT.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_NUMPAD4, '4');
		} else if (AmstradJoystickCommand.RIGHT.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_NUMPAD6, '6');
		} else if (AmstradJoystickCommand.FIRE2.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_NUMPAD5, '5');
		} else if (AmstradJoystickCommand.FIRE1.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_NUMPAD0, '0');
		}
		return keyEvent;
	}

	protected KeyEvent translateToKeyEventForSecondaryJoystick(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		Component source = getKeyEventSource(event);
		int type = toKeyEventType(event);
		long when = System.currentTimeMillis();
		AmstradJoystickCommand command = event.getCommand();
		if (AmstradJoystickCommand.UP.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_6, '6');
		} else if (AmstradJoystickCommand.DOWN.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, 0, KeyEvent.VK_5, '5');
		} else if (AmstradJoystickCommand.LEFT.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_R, 'R');
		} else if (AmstradJoystickCommand.RIGHT.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_T, 'T');
		} else if (AmstradJoystickCommand.FIRE2.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_G, 'G');
		} else if (AmstradJoystickCommand.FIRE1.equals(command)) {
			keyEvent = new KeyEvent(source, type, when, InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_F, 'F');
		}
		return keyEvent;
	}

}