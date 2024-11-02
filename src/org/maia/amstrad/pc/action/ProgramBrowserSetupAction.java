package org.maia.amstrad.pc.action;

import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfigurator;
import org.maia.swing.dialog.ActionableDialog;

public class ProgramBrowserSetupAction extends ActionableDialogAction {

	public ProgramBrowserSetupAction(AmstradPc amstradPc) {
		this(amstradPc, "Setup program browser...");
	}

	public ProgramBrowserSetupAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		amstradPc.getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (!isTriggeredByMenuKeyBindings()) {
			if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_B && event.isControlDown()
					&& event.isShiftDown()) {
				openDialog();
			}
		}
	}

	@Override
	public void dialogConfirmed(ActionableDialog dialog) {
		AmstradProgramRepositoryConfiguration cfg = ((AmstradProgramRepositoryConfigurator) dialog.getMainComponent())
				.getState();
		getAmstradContext().setProgramRepositoryConfiguration(cfg);
		AmstradProgramBrowser updatedBrowser = AmstradFactory.getInstance().createProgramBrowser(getAmstradPc());
		getBrowserAction().reset(updatedBrowser);
	}

	@Override
	protected ActionableDialog createDialog() {
		AmstradProgramRepositoryConfiguration cfg = getAmstradContext().getProgramRepositoryConfiguration();
		return AmstradProgramRepositoryConfigurator.createDialog(getAmstradPc().getFrame(), cfg);
	}

	private ProgramBrowserAction getBrowserAction() {
		return getAmstradPc().getActions().getProgramBrowserAction();
	}

}