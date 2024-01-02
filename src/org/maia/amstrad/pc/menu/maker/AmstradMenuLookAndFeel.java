package org.maia.amstrad.pc.menu.maker;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.maia.amstrad.pc.menu.AmstradMenuBar;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;

public abstract class AmstradMenuLookAndFeel {

	protected AmstradMenuLookAndFeel() {
	}

	public abstract void applySystemWide();

	public abstract void applyToMenuBar(AmstradMenuBar menuBar);

	public abstract void applyToPopupMenu(AmstradPopupMenu popupMenu);

	public void applyToMenu(JMenu menu) {
		applyToMenu(menu, null);
	}

	public void applyToMenu(JMenu menu, Icon icon) {
		applyToMenuItem(menu, icon);
	}

	public void applyToMenuItem(JMenuItem item) {
		applyToMenuItem(item, null);
	}

	public abstract void applyToMenuItem(JMenuItem item, Icon icon);

}