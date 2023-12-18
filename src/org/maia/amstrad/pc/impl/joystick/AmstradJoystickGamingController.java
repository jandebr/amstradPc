package org.maia.amstrad.pc.impl.joystick;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEventListener;

import jemu.ui.KeyDispatcher;

public class AmstradJoystickGamingController extends AmstradJoystickEventTranslator
		implements AmstradJoystickEventListener {

	private KeyDispatcher keyDispatcher;

	public AmstradJoystickGamingController(KeyDispatcher keyDispatcher) {
		this.keyDispatcher = keyDispatcher;
	}

	@Override
	public void amstradJoystickEventDispatched(AmstradJoystickEvent event) {
		if (event.isConsumed())
			return;
		if (isGamingMode(event)) {
			KeyEvent keyEvent = translateToKeyEvent(event);
			if (keyEvent != null) {
				dispatchKeyEvent(keyEvent);
				event.consume();
			}
		}
	}

	@Override
	protected KeyEvent translateToKeyEvent(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		if (isJoystick0(event)) {
			keyEvent = translateToKeyEventForJoystick0(event);
		} else if (isJoystick1(event)) {
			keyEvent = translateToKeyEventForJoystick1(event);
		}
		return keyEvent;
	}

	private KeyEvent translateToKeyEventForJoystick0(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		Component source = getKeyEventSource();
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

	private KeyEvent translateToKeyEventForJoystick1(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		Component source = getKeyEventSource();
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

	private void dispatchKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
			getKeyDispatcher().keyPressed(keyEvent);
		} else {
			getKeyDispatcher().keyReleased(keyEvent);
		}
	}

	protected Component getKeyEventSource() {
		return getKeyDispatcher().getSource();
	}

	protected KeyDispatcher getKeyDispatcher() {
		return keyDispatcher;
	}

}