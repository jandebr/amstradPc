package org.maia.amstrad.gui.browser.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradMode;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.ProgramBrowserListener;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.util.GenericListenerList;

public class ProgramBrowserAction extends AmstradPcAction implements ProgramBrowserListener {

	private ProgramBrowserDisplaySource displaySource;

	private GenericListenerList<ProgramBrowserListener> browserListeners;

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		this.browserListeners = new GenericListenerList<ProgramBrowserListener>();
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
			if (getMode().isProgramBrowserCentric()) {
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
		return getMode().isProgramBrowserCentric() ? "Program browser" : "Open program browser";
	}

	private String getNameToClose() {
		return getMode().isProgramBrowserCentric() ? "New Basic prompt" : "Close program browser";
	}

	private AmstradMode getMode() {
		return AmstradFactory.getInstance().getAmstradContext().getMode();
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

	private ProgramBrowserDisplaySource getDisplaySource() {
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