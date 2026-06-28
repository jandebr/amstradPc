package org.maia.amstrad.pc.menu;

import org.maia.util.GenericListener;

public interface AmstradMenuBarListener extends GenericListener {

	void menuBarSelectionChanged(AmstradMenuBar menuBar);

}