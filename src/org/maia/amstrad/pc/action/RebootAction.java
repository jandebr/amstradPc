package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public class RebootAction extends AmstradPcAction {

	public RebootAction(AmstradPc amstradPc) {
		this(amstradPc, "Reboot");
	}

	public RebootAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		amstradPc.getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		reboot();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_R && event.isControlDown()
				&& event.isShiftDown()) {
			reboot();
		}
	}

	private void reboot() {
		getAmstradPc().reboot(false, false);
	}

}