package org.maia.amstrad.pc.impl.joystick;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitorPopupMenuListener;

public class AmstradJoystickManager extends AmstradMonitorAdapter implements AmstradMonitorPopupMenuListener {

	private AmstradJoystick joystick;

	public AmstradJoystickManager(AmstradJoystick joystick) {
		this.joystick = joystick;
		init();
	}

	protected void init() {
		// Auto-repeat
		restoreAutoRepeatEnabled();
		getMonitor().addMonitorListener(this);
		getMonitor().addPopupMenuListener(this);
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
	public void popupMenuWillBecomeVisible(AmstradPopupMenu popupMenu) {
		setAutoRepeatEnabled(true);
	}

	@Override
	public void popupMenuWillBecomeInvisible(AmstradPopupMenu popupMenu) {
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

	protected AmstradPc getAmstradPc() {
		return getJoystick().getAmstradPc();
	}

	protected AmstradJoystick getJoystick() {
		return joystick;
	}

}