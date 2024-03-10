package org.maia.amstrad.pc.joystick.keys;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;

public abstract class AmstradJoystickKeyEventAdapter {

	protected AmstradJoystickKeyEventAdapter() {
	}

	public abstract AmstradJoystickKeyEvent translateToKeyEvent(AmstradJoystickEvent event);

	protected int toKeyEventType(AmstradJoystickEvent event) {
		if (event.isFired()) {
			return KeyEvent.KEY_PRESSED;
		} else {
			return KeyEvent.KEY_RELEASED;
		}
	}

	protected boolean isPrimaryJoystick(AmstradJoystickEvent event) {
		return event.getJoystick().isPrimaryJoystick();
	}

	protected boolean isSecondaryJoystick(AmstradJoystickEvent event) {
		return event.getJoystick().isSecondaryJoystick();
	}

}