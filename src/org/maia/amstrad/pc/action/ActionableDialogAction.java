package org.maia.amstrad.pc.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
			dialog.setVisible(true);
		}
	}

	@Override
	public void dialogButtonClicked(ActionableDialog dialog, ActionableDialogButton button) {
		// Subclasses may override
	}

	@Override
	public void dialogCancelled(ActionableDialog dialog) {
		// Subclasses may override
	}

	@Override
	public void dialogClosed(ActionableDialog dialog) {
		resetKeyModifiers();
		setEnabled(true);
	}

	protected abstract ActionableDialog createDialog();

	private void resetKeyModifiers() {
		Component display = getAmstradPc().getDisplayPane();
		if (display instanceof KeyListener) {
			KeyListener kl = (KeyListener) display;
			char cUnd = KeyEvent.CHAR_UNDEFINED;
			kl.keyReleased(new KeyEvent(display, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_CONTROL, cUnd));
			kl.keyReleased(new KeyEvent(display, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_SHIFT, cUnd));
			kl.keyReleased(new KeyEvent(display, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_ALT, cUnd));
		}
	}

}