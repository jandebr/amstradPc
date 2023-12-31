package org.maia.amstrad.pc.impl.joystick;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.frame.AmstradPcFrame;
import org.maia.amstrad.pc.frame.AmstradPcFrameListener;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.joystick.AmstradJoystickMode;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;

public class AmstradJoystickManager extends AmstradMonitorAdapter implements AmstradPcFrameListener {

	private AmstradJoystick joystick;

	public AmstradJoystickManager(AmstradJoystick joystick) {
		this.joystick = joystick;
		init();
	}

	protected void init() {
		// Mode switching
		updateMode();
		getMonitor().addMonitorListener(this);
		getFrame().addFrameListener(this);
		// Gaming mode
		getJoystick().addJoystickEventListener(new AmstradJoystickGamingController());
		// Menu mode
		if (isPrimaryJoystick()) {
			getJoystick().addJoystickEventListener(new AmstradJoystickPopupMenuController());
			getJoystick().addJoystickEventListener(new AmstradJoystickAlternativeDisplaySourceController());
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		updateMode();
	}

	@Override
	public void popupMenuWillBecomeVisible(AmstradPcFrame frame) {
		switchMode(AmstradJoystickMode.MENU);
	}

	@Override
	public void popupMenuWillBecomeInvisible(AmstradPcFrame frame) {
		updateMode();
	}

	protected void updateMode() {
		switchMode(getTargetMode());
	}

	protected void switchMode(AmstradJoystickMode mode) {
		getJoystick().switchMode(mode);
		getJoystick().switchAutoRepeatEnabled(isAutoRepeatEnabled(mode));
	}

	protected AmstradJoystickMode getTargetMode() {
		if (getMonitor().isPrimaryDisplaySourceShowing()) {
			return AmstradJoystickMode.GAMING;
		} else {
			return AmstradJoystickMode.MENU;
		}
	}

	protected boolean isAutoRepeatEnabled(AmstradJoystickMode mode) {
		return AmstradJoystickMode.MENU.equals(mode);
	}

	protected boolean isPrimaryJoystick() {
		return AmstradJoystickID.JOYSTICK0.equals(getJoystick().getJoystickId());
	}

	protected AmstradMonitor getMonitor() {
		return getAmstradPc().getMonitor();
	}

	protected AmstradPcFrame getFrame() {
		return getAmstradPc().getFrame();
	}

	protected AmstradPc getAmstradPc() {
		return getJoystick().getAmstradPc();
	}

	protected AmstradJoystick getJoystick() {
		return joystick;
	}

}