package org.maia.amstrad.pc.menu.maker;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.menu.AmstradMenuBar;

public abstract class AmstradMenuBarMaker extends AmstradMenuMaker {

	protected AmstradMenuBarMaker(AmstradPc amstradPc, AmstradMenuLookAndFeel lookAndFeel) {
		super(amstradPc, lookAndFeel);
	}

	public AmstradMenuBar createMenuBar() {
		return (AmstradMenuBar) createMenu();
	}

	@Override
	protected abstract AmstradMenuBar doCreateMenu();

	protected AmstradMenuBar updateMenuBarLookAndFeel(AmstradMenuBar menuBar) {
		getLookAndFeel().applyToMenuBar(menuBar);
		return menuBar;
	}

}