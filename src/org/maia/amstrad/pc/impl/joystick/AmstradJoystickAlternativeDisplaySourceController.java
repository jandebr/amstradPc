package org.maia.amstrad.pc.impl.joystick;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEventListener;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public class AmstradJoystickAlternativeDisplaySourceController extends AmstradJoystickMenuController
		implements AmstradJoystickEventListener {

	public AmstradJoystickAlternativeDisplaySourceController() {
	}

	@Override
	public void amstradJoystickEventDispatched(AmstradJoystickEvent event) {
		AmstradAlternativeDisplaySource displaySource = event.getJoystick().getAmstradPc().getMonitor()
				.getCurrentAlternativeDisplaySource();
		if (displaySource == null)
			return;
		if (event.isConsumed())
			return;
		if (!isAutoRepeatSafe(event))
			return;
		if (!isPopupMenuShowing(event) && isMenuMode(event)) {
			KeyEvent keyEvent = translateToKeyEvent(event);
			if (keyEvent != null) {
				dispatchKeyEvent(keyEvent, displaySource);
				event.consume();
			}
		}
	}

	private void dispatchKeyEvent(KeyEvent keyEvent, AmstradAlternativeDisplaySource displaySource) {
		if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
			displaySource.pressKey(keyEvent);
		} else {
			displaySource.releaseKey(keyEvent);
		}
	}

}