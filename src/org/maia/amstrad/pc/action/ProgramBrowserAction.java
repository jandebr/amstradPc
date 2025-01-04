package org.maia.amstrad.pc.action;

import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.browser.AmstradProgramBrowserListener;

public class ProgramBrowserAction extends ToggleDisplaySourceAction implements AmstradProgramBrowserListener {

	private AmstradProgramBrowser programBrowser;

	public ProgramBrowserAction(AmstradPc amstradPc) {
		this(AmstradFactory.getInstance().createProgramBrowser(amstradPc));
	}

	public ProgramBrowserAction(AmstradProgramBrowser programBrowser) {
		super(programBrowser.getAmstradPc(), new ToggleActionKey(KeyEvent.VK_B, CTRL_KEY_MODIFIER));
		setNameToOpen(getSystemSettings().isProgramCentric() ? "Program browser" : "Open program browser");
		setNameToClose(getSystemSettings().isProgramCentric() ? "Basic" : "Close program browser");
		updateProgramBrowser(programBrowser);
	}

	@Override
	public void programLoadedFromBrowser(AmstradProgramBrowser programBrowser, AmstradProgram program) {
		getInfoAction().updateProgram(program);
	}

	@Override
	public void programRunFromBrowser(AmstradProgramBrowser programBrowser, AmstradProgram program) {
		getInfoAction().updateProgram(program);
	}

	public void showProgramBrowser() {
		showDisplaySource(false);
	}

	public void closeProgramBrowser() {
		closeDisplaySource(false);
	}

	public synchronized void reset(AmstradProgramBrowser programBrowser) {
		updateProgramBrowser(programBrowser);
		updateDisplaySource();
	}

	private void updateProgramBrowser(AmstradProgramBrowser programBrowser) {
		if (getProgramBrowser() != null) {
			getProgramBrowser().removeListener(this);
		}
		setProgramBrowser(programBrowser);
		if (programBrowser != null) {
			programBrowser.addListener(this);
		}
	}

	@Override
	protected boolean canCloseDisplaySource(boolean invokedByKeyEvent) {
		if (invokedByKeyEvent && getSystemSettings().isProgramCentric())
			return false;
		return super.canCloseDisplaySource(invokedByKeyEvent);
	}

	@Override
	protected boolean isDisplaySourceShowing() {
		return isProgramBrowserShowing();
	}

	public boolean isProgramBrowserShowing() {
		return getAmstradContext().isProgramBrowserShowing(getAmstradPc());
	}

	@Override
	public ProgramBrowserDisplaySource getDisplaySource() {
		return getProgramBrowser().getDisplaySource();
	}

	public AmstradProgramBrowser getProgramBrowser() {
		return programBrowser;
	}

	private void setProgramBrowser(AmstradProgramBrowser programBrowser) {
		this.programBrowser = programBrowser;
	}

	private ProgramInfoAction getInfoAction() {
		return getAmstradPc().getActions().getProgramInfoAction();
	}

}