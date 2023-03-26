package org.maia.amstrad.gui.browser.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.ProgramBrowserListener;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramBrowserAction extends AmstradPcAction implements ProgramBrowserListener {

	private ProgramBrowserDisplaySource displaySource;

	private List<ProgramBrowserListener> browserListeners;

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		this.browserListeners = new Vector<ProgramBrowserListener>();
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
		if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_B && event.isControlDown()
				&& !event.isShiftDown()) {
			toggleProgramBrowser();
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
			if (isKioskMode()) {
				getAmstradPc().reboot(false, false);
			}
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
	}

	private void updateName() {
		if (isProgramBrowserShowing()) {
			changeName(getNameToClose());
		} else {
			changeName(getNameToOpen());
		}
	}

	private String getNameToOpen() {
		return isKioskMode() ? "Program browser" : "Open program browser";
	}

	private String getNameToClose() {
		return isKioskMode() ? "Basic new prompt" : "Close program browser";
	}

	private boolean isKioskMode() {
		return AmstradFactory.getInstance().getAmstradContext().isKioskMode();
	}

	public boolean isProgramBrowserShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getMonitor()
				.getCurrentAlternativeDisplaySource();
		if (altDisplaySource == null)
			return false;
		if (!(altDisplaySource instanceof ProgramBrowserDisplaySource))
			return false;
		return !((ProgramBrowserDisplaySource) altDisplaySource).isStandaloneInfo();
	}

	public void addListener(ProgramBrowserListener listener) {
		getBrowserListeners().add(listener);
	}

	public void removeListener(ProgramBrowserListener listener) {
		getBrowserListeners().remove(listener);
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

	private ProgramBrowserDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = AmstradFactory.getInstance().createProgramRepositoryBrowser(getAmstradPc());
			displaySource.addListener(this);
		}
		return displaySource;
	}

	private List<ProgramBrowserListener> getBrowserListeners() {
		return browserListeners;
	}

}