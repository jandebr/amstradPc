package org.maia.amstrad.pc.menu.maker;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;

public abstract class AmstradPopupMenuMaker extends AmstradMenuMaker {

	protected AmstradPopupMenuMaker(AmstradPc amstradPc) {
		super(amstradPc);
	}

	protected AmstradPopupMenuMaker(AmstradPc amstradPc, AmstradMenuLookAndFeel lookAndFeel) {
		super(amstradPc, lookAndFeel);
	}

	public AmstradPopupMenu createPopupMenu() {
		return (AmstradPopupMenu) createMenu();
	}

	@Override
	protected abstract AmstradPopupMenu doCreateMenu();

	protected AmstradPopupMenu updatePopupMenuLookAndFeel(AmstradPopupMenu popupMenu) {
		getLookAndFeel().applyToPopupMenu(popupMenu);
		return popupMenu;
	}

}