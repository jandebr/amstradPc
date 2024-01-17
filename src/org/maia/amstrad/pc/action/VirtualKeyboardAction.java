package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboardStateListener;

public class VirtualKeyboardAction extends AmstradPcAction implements AmstradVirtualKeyboardStateListener {

	private static String NAME_DEACTIVATE = "Hide onscreen keyboard";

	private static String NAME_ACTIVATE = "Onscreen keyboard";

	public VirtualKeyboardAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		getVirtualKeyboard().addKeyboardStateListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getVirtualKeyboard().toggleActiveState();
	}

	@Override
	public void amstradVirtualKeyboardActivated(AmstradVirtualKeyboard keyboard) {
		updateName();
	}

	@Override
	public void amstradVirtualKeyboardDeactivated(AmstradVirtualKeyboard keyboard) {
		updateName();
	}

	private void updateName() {
		if (isActive()) {
			changeName(NAME_DEACTIVATE);
		} else {
			changeName(NAME_ACTIVATE);
		}
	}

	public boolean isActive() {
		return getVirtualKeyboard().isActive();
	}

	protected AmstradVirtualKeyboard getVirtualKeyboard() {
		return getAmstradPc().getVirtualKeyboard();
	}

}