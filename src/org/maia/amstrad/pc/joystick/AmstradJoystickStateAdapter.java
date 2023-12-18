package org.maia.amstrad.pc.joystick;

public abstract class AmstradJoystickStateAdapter implements AmstradJoystickStateListener {

	protected AmstradJoystickStateAdapter() {
	}

	@Override
	public void amstradJoystickConnected(AmstradJoystick joystick) {
		// Subclasses can override this
	}

	@Override
	public void amstradJoystickDisconnected(AmstradJoystick joystick) {
		// Subclasses can override this
	}

	@Override
	public void amstradJoystickActivated(AmstradJoystick joystick) {
		// Subclasses can override this
	}

	@Override
	public void amstradJoystickDeactivated(AmstradJoystick joystick) {
		// Subclasses can override this
	}

	@Override
	public void amstradJoystickChangedMode(AmstradJoystick joystick, AmstradJoystickMode mode) {
		// Subclasses can override this
	}

}