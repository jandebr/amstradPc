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

public class ProgramCarouselAction extends AmstradPcAction implements AmstradProgramBrowserListener {

	private AmstradProgramBrowser programBrowser;

	private String nameToOpen;

	private String nameToClose;

	private boolean carouselMode;

	private boolean resumeAfterCarousel;

	public ProgramCarouselAction(AmstradPc amstradPc) {
		this(AmstradFactory.getInstance().createProgramBrowser(amstradPc));
	}

	public ProgramCarouselAction(AmstradProgramBrowser programBrowser) {
		super(programBrowser.getAmstradPc(), "");
		this.nameToOpen = getSystemSettings().isProgramCentric() ? "Program carousel" : "Open program carousel";
		this.nameToClose = getSystemSettings().isProgramCentric() ? "Basic" : "Close program carousel";
		updateProgramBrowser(programBrowser);
		updateName();
		getAmstradPc().getMonitor().addMonitorListener(this);
		getAmstradPc().getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramCarousel();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (!isTriggeredByMenuKeyBindings()) {
			if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_C && event.isControlDown()
					&& !event.isShiftDown()) {
				toggleProgramCarousel();
			}
		}
	}

	public void toggleProgramCarousel() {
		if (getNameToOpen().equals(getName())) {
			showProgramCarousel();
		} else {
			closeProgramCarousel();
		}
	}

	public void showProgramCarousel() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().swapDisplaySource(getProgramBrowser().getDisplaySource());
		}
	}

	public void closeProgramCarousel() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().resetDisplaySource();
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		super.amstradDisplaySourceChanged(monitor);
		updateName();
		if (isProgramCarouselShowing()) {
			carouselMode = true; // enter carousel
			resumeAfterCarousel = !getAmstradPc().isPaused();
			if (resumeAfterCarousel) {
				getAmstradPc().pause(); // pause
			}
		} else {
			if (monitor.isPrimaryDisplaySourceShowing() && carouselMode) {
				// exiting carousel
				if (resumeAfterCarousel && getAmstradPc().isPaused()) {
					monitor.getAmstradPc().resume(); // auto-resume
				}
			}
			carouselMode = false;
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
		if (isProgramCarouselShowing()) {
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

	public boolean isProgramCarouselShowing() {
		return getAmstradContext().isProgramCarouselShowing(getAmstradPc());
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