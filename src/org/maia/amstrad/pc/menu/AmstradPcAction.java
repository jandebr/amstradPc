package org.maia.amstrad.pc.menu;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradPcAction extends AbstractAction {

	private AmstradPc amstradPc;

	protected AmstradPcAction(AmstradPc amstradPc, String name) {
		super(name);
		this.amstradPc = amstradPc;
	}

	protected void showInfoMessageDialog(String dialogMessage) {
		JOptionPane.showMessageDialog(getAmstradPc().getDisplayPane(), dialogMessage);
	}

	protected void showErrorMessageDialog(String dialogTitle, String dialogMessage) {
		JOptionPane.showMessageDialog(getAmstradPc().getDisplayPane(), dialogMessage, dialogTitle,
				JOptionPane.ERROR_MESSAGE);
	}

	protected void showErrorMessageDialog(String dialogTitle, String dialogMessage, Exception error) {
		showErrorMessageDialog(dialogTitle, dialogMessage + "\n" + error.getMessage());
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