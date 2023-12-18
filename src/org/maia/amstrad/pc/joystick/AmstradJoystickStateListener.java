package org.maia.amstrad.pc.joystick;

import org.maia.util.GenericListener;

public interface AmstradJoystickStateListener extends GenericListener {

	void amstradJoystickConnected(AmstradJoystick joystick);

	void amstradJoystickDisconnected(AmstradJoystick joystick);

	void amstradJoystickActivated(AmstradJoystick joystick);

	void amstradJoystickDeactivated(AmstradJoystick joystick);

	void amstradJoystickChangedMode(AmstradJoystick joystick, AmstradJoystickMode mode);

}