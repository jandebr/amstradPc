package org.maia.amstrad.pc.monitor;

import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.util.GenericListener;

public interface AmstradMonitorPopupMenuListener extends GenericListener {

	void popupMenuWillBecomeVisible(AmstradPopupMenu popupMenu);

	void popupMenuWillBecomeInvisible(AmstradPopupMenu popupMenu);

}