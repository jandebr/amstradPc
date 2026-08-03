package org.maia.amstrad.gui.browser.classic.controlkeys;

import org.maia.amstrad.gui.overlay.controlkeys.ControlKey;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.ProgramInfoAction;

public class ProgramInfoControlKey extends ControlKey {

	public ProgramInfoControlKey(AmstradPc amstradPc) {
		super(amstradPc, ProgramInfoAction.KEY_TRIGGER_TEXT, "Info");
	}

	@Override
	public boolean isAvailable() {
		return isProgramBrowserShowing();
	}

}