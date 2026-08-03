package org.maia.amstrad.gui.overlay.controlkeys;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;

public class PopupMenuControlKey extends ControlKey {

	public PopupMenuControlKey(AmstradPc amstradPc) {
		super(amstradPc, AmstradPopupMenu.KEY_TRIGGER_TEXT, "Menu");
	}

	@Override
	public boolean isAvailable() {
		return getAmstradPc().getMonitor().isPopupMenuInstalled();
	}

}