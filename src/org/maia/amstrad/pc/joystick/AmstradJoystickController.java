package org.maia.amstrad.pc.joystick;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickEvent.EventType;
import org.maia.amstrad.pc.joystick.keys.AmstradJoystickGamingAdapter;
import org.maia.amstrad.pc.joystick.keys.AmstradJoystickMenuAdapter;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.keyboard.KeyEventTarget;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitorPopupMenuListener;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public class AmstradJoystickController extends AmstradMonitorAdapter
		implements AmstradMonitorPopupMenuListener, AmstradJoystickEventListener {

	private AmstradJoystick joystick;

	private AmstradJoystickGamingAdapter gamingAdapter;

	private AmstradJoystickMenuAdapter menuAdapter;

	public AmstradJoystickController(AmstradJoystick joystick) {
		this.joystick = joystick;
		this.gamingAdapter = new AmstradJoystickGamingAdapter();
		this.menuAdapter = new AmstradJoystickMenuAdapter();
		configureAutoRepeatEnabled();
		getMonitor().addMonitorListener(this);
		getMonitor().addPopupMenuListener(this);
		joystick.addJoystickEventListener(this);
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		configureAutoRepeatEnabled();
	}

	@Override
	public void popupMenuWillBecomeVisible(AmstradPopupMenu popupMenu) {
		setAutoRepeatEnabled(true);
	}

	@Override
	public void popupMenuWillBecomeInvisible(AmstradPopupMenu popupMenu) {
		configureAutoRepeatEnabled();
	}

	@Override
	public void amstradJoystickEventDispatched(AmstradJoystickEvent event) {
		// Gaming
		if (isGamingMode()) {
			KeyEvent keyEvent = getGamingAdapter().translateToKeyEvent(event);
			if (keyEvent != null) {
				dispatchKeyEvent(keyEvent, getKeyboard());
				return;
			}
		}
		if (getJoystick().isPrimaryJoystick() && isAutoRepeatSafe(event)) {
			// Popup menu
			AmstradPopupMenu popupMenu = getMonitor().getInstalledPopupMenu();
			if (popupMenu != null) {
				if (!isPopupMenuShowing() && isPopupMenuTrigger(event)) {
					popupMenu.showPopupMenu();
					return;
				} else if (isPopupMenuShowing()) {
					KeyEvent keyEvent = null;
					if (isPopupMenuTrigger(event)) {
						keyEvent = getMenuAdapter().translateToKeyEvent(new AmstradJoystickEvent(getJoystick(),
								AmstradJoystickCommand.CANCEL, event.getEventType())); // Cancel the popup menu
					} else {
						keyEvent = getMenuAdapter().translateToKeyEvent(event);
					}
					if (keyEvent != null) {
						popupMenu.handleKeyEvent(keyEvent);
						return;
					}
				}
			}
			// Alternative display source
			AmstradAlternativeDisplaySource displaySource = getMonitor().getCurrentAlternativeDisplaySource();
			if (displaySource != null && !isPopupMenuShowing()) {
				KeyEvent keyEvent = getMenuAdapter().translateToKeyEvent(event);
				if (keyEvent != null) {
					dispatchKeyEvent(keyEvent, displaySource);
					return;
				}
			}
		}
	}

	protected void dispatchKeyEvent(KeyEvent keyEvent, KeyEventTarget target) {
		if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
			target.pressKey(keyEvent);
		} else {
			target.releaseKey(keyEvent);
		}
	}

	protected boolean isGamingMode() {
		return getMonitor().isPrimaryDisplaySourceShowing() && !isPopupMenuShowing();
	}

	protected boolean isPopupMenuShowing() {
		return getMonitor().isPopupMenuShowing();
	}

	protected boolean isPopupMenuTrigger(AmstradJoystickEvent event) {
		return AmstradJoystickCommand.MENU.equals(event.getCommand()) && event.isFired();
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

	protected void configureAutoRepeatEnabled() {
		boolean autoRepeat = getMonitor().isAlternativeDisplaySourceShowing();
		setAutoRepeatEnabled(autoRepeat);
	}

	protected void setAutoRepeatEnabled(boolean autoRepeat) {
		getJoystick().switchAutoRepeatEnabled(autoRepeat);
	}

	protected AmstradKeyboard getKeyboard() {
		return getJoystick().getAmstradPc().getKeyboard();
	}

	protected AmstradMonitor getMonitor() {
		return getJoystick().getAmstradPc().getMonitor();
	}

	public AmstradJoystick getJoystick() {
		return joystick;
	}

	private AmstradJoystickGamingAdapter getGamingAdapter() {
		return gamingAdapter;
	}

	private AmstradJoystickMenuAdapter getMenuAdapter() {
		return menuAdapter;
	}

}