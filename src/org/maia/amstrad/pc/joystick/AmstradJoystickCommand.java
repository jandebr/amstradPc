package org.maia.amstrad.pc.joystick;

public enum AmstradJoystickCommand {

	UP("Up", "UP", "Move UP"),

	DOWN("Down", "DOWN", "Move DOWN"),

	LEFT("Left", "LEFT", "Move LEFT"),

	RIGHT("Right", "RIGHT", "Move RIGHT"),

	FIRE2("Fire 2", "FIRE2", "The FIRE2 button. Also used to CONFIRM"),

	FIRE1("Fire 1", "FIRE1", "The FIRE1 button. Also used to CANCEL"),

	MENU("Menu", "MENU", "Brings up the menu"),

	KEYBOARD("Keyboard", "KEYBOARD", "Brings up the on-screen keyboard");

	public static final AmstradJoystickCommand CONFIRM = FIRE2;

	public static final AmstradJoystickCommand CANCEL = FIRE1;

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