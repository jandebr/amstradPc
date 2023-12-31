package org.maia.amstrad.pc.joystick;

public enum AmstradJoystickCommand {

	UP("Up", "UP"),

	DOWN("Down", "DOWN"),

	LEFT("Left", "LEFT"),

	RIGHT("Right", "RIGHT"),

	FIRE2("Fire 2", "FIRE2"),

	FIRE1("Fire 1", "FIRE1"),

	MENU("Menu", "MENU"),

	CONFIRM("Confirm", "CONFIRM"),

	CANCEL("Cancel", "CANCEL");

	private String displayName;

	private String identifier;

	private String description;

	private AmstradJoystickCommand(String displayName, String identifier) {
		this(displayName, identifier, null);
	}

	private AmstradJoystickCommand(String displayName, String identifier, String description) {
		this.displayName = displayName;
		this.identifier = identifier;
		this.description = description;
	}

	public static AmstradJoystickCommand withIdentifier(String identifier) {
		for (AmstradJoystickCommand command : AmstradJoystickCommand.values()) {
			if (command.getIdentifier().equals(identifier))
				return command;
		}
		return null;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getDescription() {
		return description;
	}

}