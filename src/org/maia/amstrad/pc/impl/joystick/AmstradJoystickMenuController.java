package org.maia.amstrad.pc.impl.joystick;

import java.awt.Component;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent.EventType;

public abstract class AmstradJoystickMenuController extends AmstradJoystickEventTranslator {

	protected AmstradJoystickMenuController() {
	}

	@Override
	protected KeyEvent translateToKeyEvent(AmstradJoystickEvent event) {
		KeyEvent keyEvent = null;
		Component source = getKeyEventSource();
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

	protected boolean isAutoRepeatSafe(AmstradJoystickEvent event) {
		if (EventType.FIRED_AUTO_REPEAT.equals(event.getEventType())) {
			return isAutoRepeatSafe(event.getCommand());
		} else {
			return true;
		}
	}

	protected boolean isAutoRepeatSafe(AmstradJoystickCommand command) {
		return AmstradJoystickCommand.UP.equals(command) || AmstradJoystickCommand.DOWN.equals(command);
	}

	protected boolean isPopupMenuShowing() {
		return getAmstradPc().getFrame().isPopupMenuShowing();
	}

	protected Component getKeyEventSource() {
		return getAmstradPc().getFrame().getRootPane();
	}

	protected abstract AmstradPc getAmstradPc();

}