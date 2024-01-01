package org.maia.amstrad.pc.action;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.joystick.AmstradJoystickStateListener;

public abstract class JoystickAction extends AmstradPcAction implements AmstradJoystickStateListener {

	private AmstradJoystickID joystickId;

	protected JoystickAction(AmstradPc amstradPc, AmstradJoystickID joystickId, String name) {
		super(amstradPc, name);
		this.joystickId = joystickId;
	}

	@Override
	public void amstradJoystickConnected(AmstradJoystick joystick) {
		// Subclasses may override after registering with joystick
	}

	@Override
	public void amstradJoystickDisconnected(AmstradJoystick joystick) {
		// Subclasses may override after registering with joystick
	}

	@Override
	public void amstradJoystickActivated(AmstradJoystick joystick) {
		// Subclasses may override after registering with joystick
	}

	@Override
	public void amstradJoystickDeactivated(AmstradJoystick joystick) {
		// Subclasses may override after registering with joystick
	}

	public AmstradJoystick getJoystick() {
		return getAmstradPc().getJoystick(getJoystickId());
	}

	public AmstradJoystickID getJoystickId() {
		return joystickId;
	}

}