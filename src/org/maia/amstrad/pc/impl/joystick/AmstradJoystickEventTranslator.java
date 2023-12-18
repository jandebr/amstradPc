package org.maia.amstrad.pc.impl.joystick;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEventListener;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.joystick.AmstradJoystickMode;

public abstract class AmstradJoystickEventTranslator {

	protected AmstradJoystickEventTranslator() {
	}

	protected abstract KeyEvent translateToKeyEvent(AmstradJoystickEvent event);

	protected int toKeyEventType(AmstradJoystickEvent event) {
		if (event.isFired()) {
			return KeyEvent.KEY_PRESSED;
		} else {
			return KeyEvent.KEY_RELEASED;
		}
	}

	protected boolean isGamingMode(AmstradJoystickEvent event) {
		return AmstradJoystickMode.GAMING.equals(event.getJoystick().getMode());
	}

	protected boolean isMenuMode(AmstradJoystickEvent event) {
		return AmstradJoystickMode.MENU.equals(event.getJoystick().getMode());
	}

	protected boolean isJoystick0(AmstradJoystickEvent event) {
		return AmstradJoystickID.JOYSTICK0.equals(event.getJoystick().getJoystickId());
	}

	protected boolean isJoystick1(AmstradJoystickEvent event) {
		return AmstradJoystickID.JOYSTICK1.equals(event.getJoystick().getJoystickId());
	}

}