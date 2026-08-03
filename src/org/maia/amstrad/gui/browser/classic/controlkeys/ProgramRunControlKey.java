package org.maia.amstrad.gui.browser.classic.controlkeys;

import org.maia.amstrad.gui.overlay.controlkeys.ControlKey;
import org.maia.amstrad.pc.AmstradPc;

public class ProgramRunControlKey extends ControlKey {

	public ProgramRunControlKey(AmstradPc amstradPc) {
		super(amstradPc, "SPACE", "Run");
	}

	@Override
	public boolean isAvailable() {
		return isProgramBrowserShowing();
	}

}