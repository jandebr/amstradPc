package org.maia.amstrad.gui.browser.classic.controlkeys;

import org.maia.amstrad.gui.overlay.controlkeys.ControlKey;
import org.maia.amstrad.pc.AmstradPc;

public class ProgramMenuControlKey extends ControlKey {

	public ProgramMenuControlKey(AmstradPc amstradPc) {
		super(amstradPc, "ENTER", "Select");
	}

	@Override
	public boolean isAvailable() {
		return isProgramBrowserShowing();
	}

}