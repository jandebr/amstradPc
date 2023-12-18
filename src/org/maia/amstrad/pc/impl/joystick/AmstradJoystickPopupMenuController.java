package org.maia.amstrad.pc.impl.joystick;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.frame.AmstradPcPopupMenu;
import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEventListener;

public class AmstradJoystickPopupMenuController extends AmstradJoystickMenuController
		implements AmstradJoystickEventListener {

	private AmstradPcPopupMenu popupMenu;

	public AmstradJoystickPopupMenuController(AmstradPcPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	@Override
	public void amstradJoystickEventDispatched(AmstradJoystickEvent event) {
		if (event.isConsumed())
			return;
		if (!isAutoRepeatSafe(event))
			return;
		if (!isPopupMenuShowing() && isPopupMenuTrigger(event)) {
			getPopupMenu().showPopupMenu();
			event.consume();
		} else if (isPopupMenuShowing()) {
			KeyEvent keyEvent = translateToKeyEvent(event);
			if (keyEvent != null) {
				getPopupMenu().handleKeyEvent(keyEvent);
				event.consume();
			}
		}
	}

	protected boolean isPopupMenuTrigger(AmstradJoystickEvent event) {
		return AmstradJoystickCommand.OPTIONS.equals(event.getCommand()) && event.isFired();
	}

	@Override
	protected AmstradPc getAmstradPc() {
		return getPopupMenu().getAmstradPc();
	}

	protected AmstradPcPopupMenu getPopupMenu() {
		return popupMenu;
	}

}