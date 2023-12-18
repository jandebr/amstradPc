package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;

public class JoystickSetupAction extends JoystickAction {

	public JoystickSetupAction(AmstradPc amstradPc, AmstradJoystickID joystickId) {
		this(amstradPc, joystickId, "Setup " + joystickId.getDisplayName() + "...");
	}

	public JoystickSetupAction(AmstradPc amstradPc, AmstradJoystickID joystickId, String name) {
		super(amstradPc, joystickId, name);
		getJoystick().addJoystickStateListener(this);
		updateEnabled();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getJoystick().showSetupDialog();
	}

	@Override
	public void amstradJoystickActivated(AmstradJoystick joystick) {
		super.amstradJoystickActivated(joystick);
		updateEnabled();
	}

	@Override
	public void amstradJoystickDeactivated(AmstradJoystick joystick) {
		super.amstradJoystickDeactivated(joystick);
		updateEnabled();
	}

	private void updateEnabled() {
		setEnabled(getJoystick().isActive());
	}

}