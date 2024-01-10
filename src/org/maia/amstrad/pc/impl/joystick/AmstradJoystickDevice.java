package org.maia.amstrad.pc.impl.joystick;

import java.util.HashMap;
import java.util.Map;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent.EventType;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.io.inputdevice.joystick.Joystick;
import org.maia.io.inputdevice.joystick.JoystickCommand;
import org.maia.io.inputdevice.joystick.JoystickListener;

public class AmstradJoystickDevice extends AmstradJoystick implements JoystickListener {

	private Joystick joystickDelegate;

	private Map<JoystickCommand, AmstradJoystickCommand> delegateCommandMap;

	private AmstradJoystickDeviceConfigurator configurator;

	public AmstradJoystickDevice(AmstradPc amstradPc, AmstradJoystickID joystickId) {
		super(amstradPc, joystickId);
		this.delegateCommandMap = new HashMap<JoystickCommand, AmstradJoystickCommand>();
		this.configurator = new AmstradJoystickDeviceConfigurator(this);
	}

	@Override
	public void showSetupDialog() {
		getConfigurator().showSetupDialog();
	}

	@Override
	public boolean isConnected() {
		return getJoystickDelegate() != null;
	}

	@Override
	protected void doActivate() {
		Joystick joy = getJoystickDelegate();
		if (joy != null) {
			joy.setActive(true);
		}
	}

	@Override
	protected void doDeactivate() {
		Joystick joy = getJoystickDelegate();
		if (joy != null) {
			joy.setActive(false);
		}
	}

	@Override
	protected void doSwitchAutoRepeatEnabled(boolean autoRepeatEnabled) {
		Joystick joy = getJoystickDelegate();
		if (joy != null) {
			joy.setAutoRepeatEnabled(autoRepeatEnabled);
		}
	}

	@Override
	public void joystickCommandFired(Joystick delegate, JoystickCommand delegateCommand, boolean autoRepeat) {
		AmstradJoystickCommand command = mapDelegateCommand(delegateCommand);
		if (command != null) {
			EventType eventType = autoRepeat ? EventType.FIRED_AUTO_REPEAT : EventType.FIRED;
			fireJoystickEvent(new AmstradJoystickEvent(this, command, eventType));
		}
	}

	@Override
	public void joystickCommandReleased(Joystick delegate, JoystickCommand delegateCommand) {
		AmstradJoystickCommand command = mapDelegateCommand(delegateCommand);
		if (command != null) {
			fireJoystickEvent(new AmstradJoystickEvent(this, command, EventType.RELEASED));
		}
	}

	private AmstradJoystickCommand mapDelegateCommand(JoystickCommand delegateCommand) {
		AmstradJoystickCommand command = getDelegateCommandMap().get(delegateCommand);
		if (command == null) {
			command = AmstradJoystickCommand.withIdentifier(delegateCommand.getIdentifier());
			getDelegateCommandMap().put(delegateCommand, command);
		}
		return command;
	}

	synchronized void disposeJoystickDelegate() {
		Joystick joy = getJoystickDelegate();
		if (joy != null) {
			System.out.println("Disposing " + getJoystickId().getDisplayName());
			joy.removeJoystickListener(this);
			joy.dispose();
			setJoystickDelegate(null);
			fireJoystickDisconnected();
		}
	}

	synchronized void installJoystickDelegate(Joystick newDelegate) {
		if (newDelegate != null) {
			System.out.println("Activating " + getJoystickId().getDisplayName());
			setJoystickDelegate(newDelegate);
			if (isActive()) {
				doActivate();
			} else {
				doDeactivate();
			}
			doSwitchAutoRepeatEnabled(isAutoRepeatEnabled());
			fireJoystickConnected();
			newDelegate.addJoystickListener(this);
		}
	}

	private Joystick getJoystickDelegate() {
		return joystickDelegate;
	}

	private void setJoystickDelegate(Joystick delegate) {
		this.joystickDelegate = delegate;
	}

	private Map<JoystickCommand, AmstradJoystickCommand> getDelegateCommandMap() {
		return delegateCommandMap;
	}

	private AmstradJoystickDeviceConfigurator getConfigurator() {
		return configurator;
	}

}