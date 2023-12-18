package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;

public class JoystickActivationAction extends JoystickAction {

	public JoystickActivationAction(AmstradPc amstradPc, AmstradJoystickID joystickId) {
		this(amstradPc, joystickId, joystickId.getDisplayName());
	}

	public JoystickActivationAction(AmstradPc amstradPc, AmstradJoystickID joystickId, String name) {
		super(amstradPc, joystickId, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getJoystick().switchActiveState(state);
	}

}