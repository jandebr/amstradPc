package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;

public class ProgramCarouselAction extends AmstradPcAction {

	private ProgramBrowserDisplaySource displaySource;

	private String nameToOpen;

	private String nameToClose;

	private boolean carouselMode;

	private boolean resumeAfterCarousel;

	public ProgramCarouselAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		this.nameToOpen = getSystemSettings().isProgramCentric() ? "Program carousel" : "Open program carousel";
		this.nameToClose = getSystemSettings().isProgramCentric() ? "Basic" : "Close program carousel";
		updateName();
		amstradPc.getMonitor().addMonitorListener(this);
		amstradPc.getKeyboard().addKeyboardListener(this);
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
			getAmstradPc().getMonitor().swapDisplaySource(getDisplaySource());
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

	private void updateName() {
		if (isProgramCarouselShowing()) {
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

	public boolean isProgramCarouselShowing() {
		return getAmstradContext().isProgramCarouselShowing(getAmstradPc());
	}

	public ProgramBrowserDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = AmstradFactory.getInstance().createCarouselProgramBrowserDisplaySource(getAmstradPc());
		}
		return displaySource;
	}

}