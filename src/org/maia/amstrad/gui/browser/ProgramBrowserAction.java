package org.maia.amstrad.gui.browser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.util.GenericListenerList;

public class ProgramBrowserAction extends AmstradPcAction implements ProgramBrowserListener {

	private ProgramBrowserDisplaySource displaySource;

	private GenericListenerList<ProgramBrowserListener> browserListeners;

	private String nameToOpen;

	private String nameToClose;

	private boolean browserMode;

	private boolean resumeAfterBrowser;

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		this.browserListeners = new GenericListenerList<ProgramBrowserListener>();
		this.nameToOpen = getSystemSettings().isProgramCentric() ? "Program browser" : "Open program browser";
		this.nameToClose = getSystemSettings().isProgramCentric() ? "Basic" : "Close program browser";
		updateName();
		amstradPc.getMonitor().addMonitorListener(this);
		amstradPc.getKeyboard().addKeyboardListener(this);
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
			getAmstradPc().getMonitor().swapDisplaySource(getDisplaySource());
		}
	}

	public void closeProgramBrowser() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().resetDisplaySource();
		}
	}

	public void reset() {
		invalidateDisplaySource();
		if (isProgramBrowserShowing()) {
			getAmstradPc().getMonitor().swapDisplaySource(getDisplaySource());
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

	private void updateName() {
		if (isProgramBrowserShowing()) {
			changeName(getNameToClose());
		} else {
			changeName(getNameToOpen());
		}
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

	public boolean isProgramBrowserShowing() {
		return getAmstradContext().isProgramBrowserShowing(getAmstradPc());
	}

	public void addListener(ProgramBrowserListener listener) {
		getBrowserListeners().addListener(listener);
	}

	public void removeListener(ProgramBrowserListener listener) {
		getBrowserListeners().removeListener(listener);
	}

	@Override
	public void programLoadedFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program) {
		for (ProgramBrowserListener listener : getBrowserListeners()) {
			listener.programLoadedFromBrowser(displaySource, program);
		}
	}

	@Override
	public void programRunFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program) {
		for (ProgramBrowserListener listener : getBrowserListeners()) {
			listener.programRunFromBrowser(displaySource, program);
		}
	}

	private void invalidateDisplaySource() {
		if (displaySource != null) {
			displaySource.removeListener(this);
			displaySource = null;
		}
	}

	public ProgramBrowserDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = AmstradFactory.getInstance().createProgramRepositoryBrowser(getAmstradPc());
			displaySource.addListener(this);
		}
		return displaySource;
	}

	private GenericListenerList<ProgramBrowserListener> getBrowserListeners() {
		return browserListeners;
	}

}