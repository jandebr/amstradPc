package org.maia.amstrad.pc.action;

import javax.swing.JComponent;

import org.maia.amstrad.gui.components.AboutPanel;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.swing.dialog.ActionableDialog;

public class AboutAction extends ActionableDialogAction {

	public AboutAction(AmstradPc amstradPc) {
		this(amstradPc, "About...");
	}

	public AboutAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	protected ActionableDialog createDialog() {
		JComponent comp = new AboutPanel(getAmstradPc().getMonitor().getGraphicsContext(),
				getAmstradContext().getVersionString());
		return ActionableDialog.createOkModalDialog(getAmstradPc().getFrame(), "Amstrad PC", comp);
	}

}