package org.maia.amstrad.jemu.menu;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.maia.amstrad.jemu.AmstradPc;

public abstract class AmstradPcAction extends AbstractAction {

	private AmstradPc amstradPc;

	protected AmstradPcAction(AmstradPc amstradPc, String name) {
		this(amstradPc, name, null);
	}

	protected AmstradPcAction(AmstradPc amstradPc, String name, Icon icon) {
		super(name, icon);
		this.amstradPc = amstradPc;
	}

	protected void setToolTipText(String text) {
		putValue(Action.SHORT_DESCRIPTION, text);
	}

	protected void changeName(String name) {
		putValue(Action.NAME, name);
	}

	protected void changeSmallIcon(Icon icon) {
		putValue(Action.SMALL_ICON, icon);
	}

	protected String getName() {
		return getValue(Action.NAME).toString();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}