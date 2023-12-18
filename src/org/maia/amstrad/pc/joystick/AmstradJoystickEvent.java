package org.maia.amstrad.pc.joystick;

public class AmstradJoystickEvent {

	private AmstradJoystick joystick;

	private AmstradJoystickCommand command;

	private EventType eventType;

	private boolean consumed;

	public AmstradJoystickEvent(AmstradJoystick joystick, AmstradJoystickCommand command, EventType eventType) {
		this.joystick = joystick;
		this.command = command;
		this.eventType = eventType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AmstradJoystickEvent [joystick=");
		builder.append(getJoystick().getJoystickId().getIdentifier());
		builder.append(", command=");
		builder.append(getCommand().getIdentifier());
		builder.append(", eventType=");
		builder.append(getEventType());
		builder.append("]");
		return builder.toString();
	}

	public void consume() {
		setConsumed(true);
	}

	public boolean isFired() {
		return EventType.FIRED.equals(getEventType()) || EventType.FIRED_AUTO_REPEAT.equals(getEventType());
	}

	public boolean isReleased() {
		return EventType.RELEASED.equals(getEventType());
	}

	public AmstradJoystick getJoystick() {
		return joystick;
	}

	public AmstradJoystickCommand getCommand() {
		return command;
	}

	public boolean isConsumed() {
		return consumed;
	}

	private void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}

	public EventType getEventType() {
		return eventType;
	}

	public static enum EventType {

		FIRED,

		FIRED_AUTO_REPEAT,

		RELEASED;

	}

}