package org.maia.amstrad.pc.impl.joystick;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEventListener;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;

public class AmstradJoystickGamingController extends AmstradJoystickEventTranslator
		implements AmstradJoystickEventListener {

	public AmstradJoystickGamingController() {
	}

	@Override
	public void amstradJoystickEventDispatched(AmstradJoystickEvent event) {
		if (event.isConsumed())
			return;
		if (isGamingMode(event)) {
			KeyEvent keyEvent = translateToKeyEvent(event);
			if (keyEvent != null) {
				AmstradKeyboard keyboard = event.getJoystick().getAmstradPc().getKeyboard();
				dispatchKeyEvent(keyEvent, keyboard);
				event.consume();
			}
		}
	}

	@Override
	protected KeyEvent translateToKeyEvent(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		if (isPrimaryJoystick(event)) {
			keyEvent = translateToKeyEventForPrimaryJoystick(event);
		} else if (isSecondaryJoystick(event)) {
			keyEvent = translateToKeyEventForSecondaryJoystick(event);
		}
		return keyEvent;
	}

	private KeyEvent translateToKeyEventForPrimaryJoystick(AmstradJoystickEvent event) {
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

	private KeyEvent translateToKeyEventForSecondaryJoystick(AmstradJoystickEvent event) {
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

	private void dispatchKeyEvent(KeyEvent keyEvent, AmstradKeyboard keyboard) {
		if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
			keyboard.pressKey(keyEvent);
		} else {
			keyboard.releaseKey(keyEvent);
		}
	}

}