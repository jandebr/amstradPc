package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public class PowerOffAction extends AmstradPcAction {

	public PowerOffAction(AmstradPc amstradPc) {
		this(amstradPc, "Power Off");
	}

	public PowerOffAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		amstradPc.getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		powerOff();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (event.isKeyPressed() && !isTriggeredByMenuKeyBindings()) {
			if (event.getKeyCode() == KeyEvent.VK_Q && event.isControlDown() && event.isShiftDown()) {
				powerOff();
			}
		}
	}

	public void powerOff() {
		if (isEnabled()) {
			setEnabled(false);
			getAmstradContext().powerOff(getAmstradPc());
		}
	}

}