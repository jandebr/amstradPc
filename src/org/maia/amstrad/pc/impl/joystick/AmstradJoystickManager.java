package org.maia.amstrad.pc.impl.joystick;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.frame.AmstradPcFrame;
import org.maia.amstrad.pc.frame.AmstradPcFrameListener;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;

public class AmstradJoystickManager extends AmstradMonitorAdapter implements AmstradPcFrameListener {

	private AmstradJoystick joystick;

	public AmstradJoystickManager(AmstradJoystick joystick) {
		this.joystick = joystick;
		init();
	}

	protected void init() {
		// Auto-repeat
		restoreAutoRepeatEnabled();
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
		restoreAutoRepeatEnabled();
	}

	@Override
	public void popupMenuWillBecomeVisible(AmstradPcFrame frame) {
		setAutoRepeatEnabled(true);
	}

	@Override
	public void popupMenuWillBecomeInvisible(AmstradPcFrame frame) {
		restoreAutoRepeatEnabled();
	}

	protected void restoreAutoRepeatEnabled() {
		boolean autoRepeat = getMonitor().isAlternativeDisplaySourceShowing();
		setAutoRepeatEnabled(autoRepeat);
	}

	protected void setAutoRepeatEnabled(boolean autoRepeat) {
		getJoystick().switchAutoRepeatEnabled(autoRepeat);
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