package org.maia.amstrad.pc.monitor;

import org.maia.amstrad.pc.menu.AmstradPopupMenu;

public abstract class AmstradMonitorPopupMenuAdapter implements AmstradMonitorPopupMenuListener {

	protected AmstradMonitorPopupMenuAdapter() {
	}

	@Override
	public void popupMenuWillBecomeVisible(AmstradPopupMenu popupMenu) {
		// Subclasses can override this
	}

	@Override
	public void popupMenuWillBecomeInvisible(AmstradPopupMenu popupMenu) {
		// Subclasses can override this
	}

}