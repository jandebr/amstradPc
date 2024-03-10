package org.maia.amstrad.pc.joystick;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickEvent.EventType;
import org.maia.amstrad.pc.joystick.keys.AmstradJoystickGamingAdapter;
import org.maia.amstrad.pc.joystick.keys.AmstradJoystickKeyEvent;
import org.maia.amstrad.pc.joystick.keys.AmstradJoystickMenuAdapter;
import org.maia.amstrad.pc.keyboard.KeyEventTarget;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboardStateListener;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitorPopupMenuListener;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public class AmstradJoystickController extends AmstradMonitorAdapter
		implements AmstradMonitorPopupMenuListener, AmstradVirtualKeyboardStateListener, AmstradJoystickEventListener {

	private AmstradJoystick joystick;

	private AmstradJoystickMenuAdapter menuAdapter;

	private AmstradJoystickGamingAdapter gamingAdapter;

	private KeyEventTarget gamingKeyEventTarget;

	public AmstradJoystickController(AmstradJoystick joystick) {
		this.joystick = joystick;
		this.menuAdapter = new AmstradJoystickMenuAdapter();
		this.gamingAdapter = new AmstradJoystickGamingAdapter();
		this.gamingKeyEventTarget = joystick.getAmstradPc().getKeyboard();
		setAutoRepeatEnabled(determineAutoRepeatEnabled());
		getMonitor().addMonitorListener(this);
		getMonitor().addPopupMenuListener(this);
		getVirtualKeyboard().addKeyboardStateListener(this);
		joystick.addJoystickEventListener(this);
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		setAutoRepeatEnabled(determineAutoRepeatEnabled());
	}

	@Override
	public void popupMenuWillBecomeVisible(AmstradPopupMenu popupMenu) {
		setAutoRepeatEnabled(true);
	}

	@Override
	public void popupMenuWillBecomeInvisible(AmstradPopupMenu popupMenu) {
		setAutoRepeatEnabled(determineAutoRepeatEnabled(true));
	}

	@Override
	public void amstradVirtualKeyboardActivated(AmstradVirtualKeyboard keyboard) {
		setAutoRepeatEnabled(true);
	}

	@Override
	public void amstradVirtualKeyboardDeactivated(AmstradVirtualKeyboard keyboard) {
		setAutoRepeatEnabled(determineAutoRepeatEnabled());
	}

	@Override
	public void amstradJoystickEventDispatched(AmstradJoystickEvent event) {
		// Gaming
		handleEventForGaming(event);
		if (event.isConsumed())
			return;
		if (getJoystick().isPrimaryJoystick()) {
			// Popup menu
			handleEventForPopupMenu(event);
			if (event.isConsumed())
				return;
			// Virtual keyboard
			handleEventForVirtualKeyboard(event);
			if (event.isConsumed())
				return;
			// Alternative display source
			handleEventForAlternativeDisplaySource(event);
		}
	}

	protected void handleEventForGaming(AmstradJoystickEvent event) {
		if (isGamingMode()) {
			AmstradJoystickKeyEvent keyEvent = getGamingAdapter().translateToKeyEvent(event);
			if (keyEvent != null) {
				dispatchKeyEvent(keyEvent, getGamingKeyEventTarget());
				event.consume();
			}
		}
	}

	protected void handleEventForPopupMenu(AmstradJoystickEvent event) {
		AmstradPopupMenu popupMenu = getMonitor().getInstalledPopupMenu();
		if (popupMenu != null) {
			if (isAutoRepeatSafeInMenus(event)) {
				if (!isPopupMenuShowing() && isPopupMenuTrigger(event)) {
					popupMenu.showPopupMenu();
					event.consume();
				} else if (isPopupMenuShowing()) {
					AmstradJoystickKeyEvent keyEvent = null;
					if (isPopupMenuTrigger(event)) {
						keyEvent = getMenuAdapter().translateToKeyEvent(new AmstradJoystickEvent(getJoystick(),
								AmstradJoystickCommand.CANCEL, event.getEventType()));
					} else {
						keyEvent = getMenuAdapter().translateToKeyEvent(event);
					}
					if (keyEvent != null) {
						popupMenu.handleKeyEvent(keyEvent);
						event.consume();
					}
				}
			}
		}
	}

	protected void handleEventForVirtualKeyboard(AmstradJoystickEvent event) {
		AmstradVirtualKeyboard keyboard = getVirtualKeyboard();
		if (keyboard != null) {
			if (!keyboard.isActive() && isVirtualKeyboardTrigger(event)) {
				keyboard.activate();
				event.consume();
			} else if (keyboard.isActive()) {
				if (isVirtualKeyboardTrigger(event)) {
					keyboard.deactivate();
					event.consume();
				} else {
					boolean fire = event.isFired();
					AmstradJoystickCommand command = event.getCommand();
					if (AmstradJoystickCommand.LEFT.equals(command)) {
						if (fire)
							keyboard.moveCursorLeft();
						event.consume();
					} else if (AmstradJoystickCommand.RIGHT.equals(command)) {
						if (fire)
							keyboard.moveCursorRight();
						event.consume();
					} else if (AmstradJoystickCommand.UP.equals(command)) {
						if (fire)
							keyboard.moveCursorUp();
						event.consume();
					} else if (AmstradJoystickCommand.DOWN.equals(command)) {
						if (fire)
							keyboard.moveCursorDown();
						event.consume();
					} else if (AmstradJoystickCommand.CONFIRM.equals(command)) {
						if (fire) {
							keyboard.pressKeyAtCursor();
						} else {
							keyboard.releaseKeyBeingPressed();
						}
						event.consume();
					} else if (AmstradJoystickCommand.CANCEL.equals(command)) {
						if (fire)
							keyboard.deactivate();
						event.consume();
					}
				}
			}
		}
	}

	protected void handleEventForAlternativeDisplaySource(AmstradJoystickEvent event) {
		AmstradAlternativeDisplaySource displaySource = getMonitor().getCurrentAlternativeDisplaySource();
		if (displaySource != null) {
			if (isAutoRepeatSafeInMenus(event)) {
				AmstradJoystickKeyEvent keyEvent = getMenuAdapter().translateToKeyEvent(event);
				if (keyEvent != null) {
					dispatchKeyEvent(keyEvent, displaySource);
					event.consume();
				}
			}
		}
	}

	protected void dispatchKeyEvent(AmstradJoystickKeyEvent keyEvent, KeyEventTarget target) {
		if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
			target.pressKey(keyEvent);
		} else {
			target.releaseKey(keyEvent);
		}
	}

	protected boolean isGamingMode() {
		return getMonitor().isPrimaryDisplaySourceShowing() && !isPopupMenuShowing() && !isVirtualKeyboardShowing();
	}

	protected boolean isPopupMenuShowing() {
		return getMonitor().isPopupMenuShowing();
	}

	protected boolean isPopupMenuTrigger(AmstradJoystickEvent event) {
		return AmstradJoystickCommand.MENU.equals(event.getCommand()) && event.isFired();
	}

	protected boolean isVirtualKeyboardShowing() {
		return getVirtualKeyboard() != null && getVirtualKeyboard().isActive();
	}

	protected boolean isVirtualKeyboardTrigger(AmstradJoystickEvent event) {
		return AmstradJoystickCommand.KEYBOARD.equals(event.getCommand()) && event.isFired();
	}

	protected boolean isAutoRepeatSafeInMenus(AmstradJoystickEvent event) {
		if (EventType.FIRED_AUTO_REPEAT.equals(event.getEventType())) {
			AmstradJoystickCommand command = event.getCommand();
			return AmstradJoystickCommand.UP.equals(command) || AmstradJoystickCommand.DOWN.equals(command);
		} else {
			return true;
		}
	}

	protected boolean determineAutoRepeatEnabled() {
		return determineAutoRepeatEnabled(false);
	}

	protected boolean determineAutoRepeatEnabled(boolean popupMenuWillBecomeInvisible) {
		if (isPopupMenuShowing() && !popupMenuWillBecomeInvisible)
			return true;
		if (isVirtualKeyboardShowing())
			return true;
		if (getMonitor().isAlternativeDisplaySourceShowing())
			return true;
		return false;
	}

	protected void setAutoRepeatEnabled(boolean autoRepeat) {
		getJoystick().switchAutoRepeatEnabled(autoRepeat);
	}

	protected AmstradVirtualKeyboard getVirtualKeyboard() {
		return getJoystick().getAmstradPc().getVirtualKeyboard();
	}

	protected AmstradMonitor getMonitor() {
		return getJoystick().getAmstradPc().getMonitor();
	}

	public AmstradJoystick getJoystick() {
		return joystick;
	}

	private AmstradJoystickMenuAdapter getMenuAdapter() {
		return menuAdapter;
	}

	private AmstradJoystickGamingAdapter getGamingAdapter() {
		return gamingAdapter;
	}

	public KeyEventTarget getGamingKeyEventTarget() {
		return gamingKeyEventTarget;
	}

	public void setGamingKeyEventTarget(KeyEventTarget target) {
		this.gamingKeyEventTarget = target;
	}

}