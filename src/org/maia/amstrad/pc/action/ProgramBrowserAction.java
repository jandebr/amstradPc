package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.browser.AmstradProgramBrowserListener;

public class ProgramBrowserAction extends AmstradPcAction implements AmstradProgramBrowserListener {

	private AmstradProgramBrowser programBrowser;

	private String nameToOpen;

	private String nameToClose;

	private boolean browserMode;

	private boolean resumeAfterBrowser;

	public ProgramBrowserAction(AmstradPc amstradPc) {
		this(AmstradFactory.getInstance().createProgramBrowser(amstradPc));
	}

	public ProgramBrowserAction(AmstradProgramBrowser programBrowser) {
		super(programBrowser.getAmstradPc(), "");
		this.nameToOpen = getSystemSettings().isProgramCentric() ? "Program browser" : "Open program browser";
		this.nameToClose = getSystemSettings().isProgramCentric() ? "Basic" : "Close program browser";
		updateProgramBrowser(programBrowser);
		updateName();
		getAmstradPc().getMonitor().addMonitorListener(this);
		getAmstradPc().getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramBrowser();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (!isTriggeredByMenuKeyBindings()) {
			if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_B && event.isControlDown()
					&& !event.isShiftDown()) {
				toggleProgramBrowser();
			}
		}
	}

	public void toggleProgramBrowser() {
		if (getNameToOpen().equals(getName())) {
			showProgramBrowser();
		} else {
			closeProgramBrowser();
		}
	}

	public void showProgramBrowser() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().swapDisplaySource(getProgramBrowser().getDisplaySource());
		}
	}

	public void closeProgramBrowser() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().resetDisplaySource();
		}
	}

	public void reset(AmstradProgramBrowser programBrowser) {
		updateProgramBrowser(programBrowser);
		if (isProgramBrowserShowing()) {
			getAmstradPc().getMonitor().swapDisplaySource(getProgramBrowser().getDisplaySource());
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		super.amstradDisplaySourceChanged(monitor);
		updateName();
		if (isProgramBrowserShowing()) {
			browserMode = true; // enter browser
			resumeAfterBrowser = !getAmstradPc().isPaused();
			if (resumeAfterBrowser) {
				getAmstradPc().pause(); // pause
			}
		} else {
			if (monitor.isPrimaryDisplaySourceShowing() && browserMode) {
				// exiting browser
				if (resumeAfterBrowser && getAmstradPc().isPaused()) {
					monitor.getAmstradPc().resume(); // auto-resume
				}
			}
			browserMode = false;
		}
	}

	@Override
	public void programLoadedFromBrowser(AmstradProgramBrowser programBrowser, AmstradProgram program) {
		getInfoAction().updateProgram(program);
	}

	@Override
	public void programRunFromBrowser(AmstradProgramBrowser programBrowser, AmstradProgram program) {
		getInfoAction().updateProgram(program);
	}

	private void updateName() {
		if (isProgramBrowserShowing()) {
			changeName(getNameToClose());
		} else {
			changeName(getNameToOpen());
		}
	}

	private synchronized void updateProgramBrowser(AmstradProgramBrowser programBrowser) {
		if (getProgramBrowser() != null) {
			getProgramBrowser().removeListener(this);
		}
		setProgramBrowser(programBrowser);
		if (programBrowser != null) {
			programBrowser.addListener(this);
		}
	}

	public boolean isProgramBrowserShowing() {
		return getAmstradContext().isProgramBrowserShowing(getAmstradPc());
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

	public String getNameToOpen() {
		return nameToOpen;
	}

	public void setNameToOpen(String nameToOpen) {
		this.nameToOpen = nameToOpen;
		updateName();
	}

	public String getNameToClose() {
		return nameToClose;
	}

	public void setNameToClose(String nameToClose) {
		this.nameToClose = nameToClose;
		updateName();
	}

}