package org.maia.amstrad.pc.impl.joystick;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.frame.AmstradPcPopupMenu;
import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEventListener;

public class AmstradJoystickPopupMenuController extends AmstradJoystickMenuController
		implements AmstradJoystickEventListener {

	public AmstradJoystickPopupMenuController() {
	}

	@Override
	public void amstradJoystickEventDispatched(AmstradJoystickEvent event) {
		AmstradPcPopupMenu popupMenu = event.getJoystick().getAmstradPc().getFrame().getInstalledPopupMenu();
		if (popupMenu == null)
			return;
		if (event.isConsumed())
			return;
		if (!isAutoRepeatSafe(event))
			return;
		if (!isPopupMenuShowing(event) && isPopupMenuTrigger(event)) {
			// Bring up the popup menu
			popupMenu.showPopupMenu();
			event.consume();
		} else if (isPopupMenuShowing(event)) {
			KeyEvent keyEvent = null;
			if (isPopupMenuTrigger(event)) {
				// Cancel the popup menu
				keyEvent = translateToKeyEvent(new AmstradJoystickEvent(event.getJoystick(),
						AmstradJoystickCommand.CANCEL, event.getEventType()));
			} else {
				// Control the popup menu
				keyEvent = translateToKeyEvent(event);
			}
			if (keyEvent != null) {
				popupMenu.handleKeyEvent(keyEvent);
				event.consume();
			}
		}
	}

	protected boolean isPopupMenuTrigger(AmstradJoystickEvent event) {
		return AmstradJoystickCommand.MENU.equals(event.getCommand()) && event.isFired();
	}

}