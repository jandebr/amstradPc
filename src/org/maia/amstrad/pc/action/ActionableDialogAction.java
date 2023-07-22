package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.dialog.ActionableDialog.ActionableDialogButton;
import org.maia.swing.dialog.ActionableDialogListener;

public abstract class ActionableDialogAction extends AmstradPcAction implements ActionableDialogListener {

	protected ActionableDialogAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		openDialog();
	}

	public void openDialog() {
		if (isEnabled()) {
			setEnabled(false);
			ActionableDialog dialog = createDialog();
			dialog.addListener(this);
			getAmstradPc().showActionableDialog(dialog);
		}
	}

	protected abstract ActionableDialog createDialog();

	@Override
	public void dialogButtonClicked(ActionableDialog dialog, ActionableDialogButton button) {
		// Subclasses may override
	}

	@Override
	public void dialogConfirmed(ActionableDialog dialog) {
		// Subclasses may override
	}

	@Override
	public void dialogCancelled(ActionableDialog dialog) {
		// Subclasses may override
	}

	@Override
	public void dialogClosed(ActionableDialog dialog) {
		setEnabled(true);
	}

}