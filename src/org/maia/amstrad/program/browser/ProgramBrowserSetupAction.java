package org.maia.amstrad.program.browser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfigurator;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.dialog.ActionableDialog.ActionableDialogButton;
import org.maia.swing.dialog.ActionableDialogListener;

public class ProgramBrowserSetupAction extends AmstradPcAction implements ActionableDialogListener {

	private ProgramBrowserAction browserAction;

	public ProgramBrowserSetupAction(ProgramBrowserAction browserAction) {
		this(browserAction, "Setup program browser...");
	}

	public ProgramBrowserSetupAction(ProgramBrowserAction browserAction, String name) {
		super(browserAction.getAmstradPc(), name);
		this.browserAction = browserAction;
		getAmstradPc().addEventListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		openDialog();
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (event instanceof AmstradPcKeyboardEvent) {
			AmstradPcKeyboardEvent keyEvent = (AmstradPcKeyboardEvent) event;
			if (keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_B && keyEvent.isControlDown()
					&& keyEvent.isShiftDown()) {
				openDialog();
			}
		}
	}

	public void openDialog() {
		if (isEnabled()) {
			setEnabled(false);
			AmstradProgramRepositoryConfiguration cfg = getAmstradContext().getProgramRepositoryConfiguration();
			ActionableDialog dialog = AmstradProgramRepositoryConfigurator.createDialog(getAmstradPc().getFrame(), cfg);
			dialog.addListener(this);
			dialog.setVisible(true);
		}
	}

	@Override
	public void dialogButtonClicked(ActionableDialog dialog, ActionableDialogButton button) {
	}

	@Override
	public void dialogCancelled(ActionableDialog dialog) {
	}

	@Override
	public void dialogConfirmed(ActionableDialog dialog) {
		AmstradProgramRepositoryConfiguration cfg = ((AmstradProgramRepositoryConfigurator) dialog.getMainComponent())
				.getState();
		getAmstradContext().setProgramRepositoryConfiguration(cfg);
		getBrowserAction().reset();
	}

	@Override
	public void dialogClosed(ActionableDialog dialog) {
		setEnabled(true);
	}

	private ProgramBrowserAction getBrowserAction() {
		return browserAction;
	}

}