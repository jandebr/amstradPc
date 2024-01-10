package org.maia.amstrad.pc.joystick.keys;

import java.awt.Component;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;

public abstract class AmstradJoystickKeyEventAdapter {

	protected AmstradJoystickKeyEventAdapter() {
	}

	public abstract KeyEvent translateToKeyEvent(AmstradJoystickEvent event);

	protected int toKeyEventType(AmstradJoystickEvent event) {
		if (event.isFired()) {
			return KeyEvent.KEY_PRESSED;
		} else {
			return KeyEvent.KEY_RELEASED;
		}
	}

	protected Component getKeyEventSource(AmstradJoystickEvent event) {
		return event.getJoystick().getAmstradPc().getMonitor().getDisplayComponent();
	}

	protected boolean isPrimaryJoystick(AmstradJoystickEvent event) {
		return event.getJoystick().isPrimaryJoystick();
	}

	protected boolean isSecondaryJoystick(AmstradJoystickEvent event) {
		return event.getJoystick().isSecondaryJoystick();
	}

}