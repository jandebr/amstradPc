package org.maia.amstrad.program.browser;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.action.ActionableDialogAction;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfigurator;
import org.maia.swing.dialog.ActionableDialog;

public class ProgramBrowserSetupAction extends ActionableDialogAction {

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

	@Override
	public void dialogConfirmed(ActionableDialog dialog) {
		AmstradProgramRepositoryConfiguration cfg = ((AmstradProgramRepositoryConfigurator) dialog.getMainComponent())
				.getState();
		getAmstradContext().setProgramRepositoryConfiguration(cfg);
		getBrowserAction().reset();
	}

	@Override
	protected ActionableDialog createDialog() {
		AmstradProgramRepositoryConfiguration cfg = getAmstradContext().getProgramRepositoryConfiguration();
		return AmstradProgramRepositoryConfigurator.createDialog(getAmstradPc().getFrame(), cfg);
	}

	private ProgramBrowserAction getBrowserAction() {
		return browserAction;
	}

}