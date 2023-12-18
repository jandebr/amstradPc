package org.maia.amstrad.pc.joystick;

import org.maia.util.GenericListener;

public interface AmstradJoystickEventListener extends GenericListener {

	void amstradJoystickEventDispatched(AmstradJoystickEvent event);

}