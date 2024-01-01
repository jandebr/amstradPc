package org.maia.amstrad.pc.impl.joystick;

import java.awt.Component;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent.EventType;

public abstract class AmstradJoystickEventTranslator {

	protected AmstradJoystickEventTranslator() {
	}

	protected abstract KeyEvent translateToKeyEvent(AmstradJoystickEvent event);

	protected int toKeyEventType(AmstradJoystickEvent event) {
		if (event.isFired()) {
			return KeyEvent.KEY_PRESSED;
		} else {
			return KeyEvent.KEY_RELEASED;
		}
	}

	protected Component getKeyEventSource(AmstradJoystickEvent event) {
		return event.getJoystick().getAmstradPc().getMonitor().getDisplayComponent();
	}

	protected boolean isGamingMode(AmstradJoystickEvent event) {
		return event.getJoystick().getAmstradPc().getMonitor().isPrimaryDisplaySourceShowing()
				&& !isPopupMenuShowing(event);
	}

	protected boolean isPrimaryJoystick(AmstradJoystickEvent event) {
		return AmstradJoystickID.JOYSTICK0.equals(event.getJoystick().getJoystickId());
	}

	protected boolean isSecondaryJoystick(AmstradJoystickEvent event) {
		return AmstradJoystickID.JOYSTICK1.equals(event.getJoystick().getJoystickId());
	}

	protected boolean isAutoRepeatSafe(AmstradJoystickEvent event) {
		if (EventType.FIRED_AUTO_REPEAT.equals(event.getEventType())) {
			return isAutoRepeatSafe(event.getCommand());
		} else {
			return true;
		}
	}

	protected boolean isAutoRepeatSafe(AmstradJoystickCommand command) {
		return AmstradJoystickCommand.UP.equals(command) || AmstradJoystickCommand.DOWN.equals(command);
	}

	protected boolean isPopupMenuShowing(AmstradJoystickEvent event) {
		return event.getJoystick().getAmstradPc().getFrame().isPopupMenuShowing();
	}

}