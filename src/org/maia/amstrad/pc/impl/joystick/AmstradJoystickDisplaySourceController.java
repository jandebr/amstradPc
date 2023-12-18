package org.maia.amstrad.pc.impl.joystick;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.joystick.AmstradJoystickEventListener;
import org.maia.amstrad.pc.monitor.display.source.AmstradEmulatedDisplaySource;

public class AmstradJoystickDisplaySourceController extends AmstradJoystickMenuController
		implements AmstradJoystickEventListener {

	private AmstradEmulatedDisplaySource displaySource;

	public AmstradJoystickDisplaySourceController(AmstradEmulatedDisplaySource displaySource) {
		this.displaySource = displaySource;
	}

	@Override
	public void amstradJoystickEventDispatched(AmstradJoystickEvent event) {
		if (event.isConsumed())
			return;
		if (!isAutoRepeatSafe(event))
			return;
		if (!isPopupMenuShowing() && isMenuMode(event)) {
			KeyEvent keyEvent = translateToKeyEvent(event);
			if (keyEvent != null) {
				dispatchKeyEvent(keyEvent);
				event.consume();
			}
		}
	}

	private void dispatchKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
			getDisplaySource().keyPressed(keyEvent);
		} else {
			getDisplaySource().keyReleased(keyEvent);
		}
	}

	@Override
	protected AmstradPc getAmstradPc() {
		return getDisplaySource().getAmstradPc();
	}

	protected AmstradEmulatedDisplaySource getDisplaySource() {
		return displaySource;
	}

}