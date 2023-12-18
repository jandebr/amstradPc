package org.maia.amstrad.pc.joystick;

public enum AmstradJoystickID {

	JOYSTICK0("Joystick 0", "JOY0"),

	JOYSTICK1("Joystick 1", "JOY1");

	private String displayName;

	private String identifier;

	private AmstradJoystickID(String displayName, String identifier) {
		this.displayName = displayName;
		this.identifier = identifier;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getIdentifier() {
		return identifier;
	}

}