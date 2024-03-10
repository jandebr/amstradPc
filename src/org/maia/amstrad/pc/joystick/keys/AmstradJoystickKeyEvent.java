package org.maia.amstrad.pc.joystick.keys;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystick;

public class AmstradJoystickKeyEvent extends KeyEvent {

	private AmstradJoystick joystick;

	private int joystickValue;

	public static final int VALUE_UP = 1;

	public static final int VALUE_DOWN = 2;

	public static final int VALUE_LEFT = 4;

	public static final int VALUE_RIGHT = 8;

	public static final int VALUE_FIRE2 = 16;

	public static final int VALUE_FIRE1 = 32;

	public AmstradJoystickKeyEvent(AmstradJoystick joystick, int joystickValue, int type, int modifiers, int keyCode,
			char keyChar) {
		super(joystick.getAmstradPc().getMonitor().getDisplayComponent(), type, System.currentTimeMillis(), modifiers,
				keyCode, keyChar);
		this.joystick = joystick;
		this.joystickValue = joystickValue;
	}

	public int getJoystickNumber() {
		if (getJoystick().isPrimaryJoystick()) {
			return 0;
		} else if (getJoystick().isSecondaryJoystick()) {
			return 1;
		} else {
			return -1;
		}
	}

	public AmstradJoystick getJoystick() {
		return joystick;
	}

	public int getJoystickValue() {
		return joystickValue;
	}

}