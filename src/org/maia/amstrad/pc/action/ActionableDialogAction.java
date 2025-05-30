package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.dialog.ActionableDialogListener;
import org.maia.swing.dialog.ActionableDialogOption;

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
			dialog.setVisible(true);
		}
	}

	protected abstract ActionableDialog createDialog();

	@Override
	public void dialogButtonClicked(ActionableDialog dialog, ActionableDialogOption dialogOption) {
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
		// A dialog catches key events when in focus. When the dialog is invoked by a key combination involving
		// modifiers, this may leave the JEMU instance and JEMU computer in an obsolete key modifier state causing
		// artefacts when resuming focus. To prevent this, we reset modifiers when a dialog is closed.
		resetKeyModifiers();
		setEnabled(true);
	}

}