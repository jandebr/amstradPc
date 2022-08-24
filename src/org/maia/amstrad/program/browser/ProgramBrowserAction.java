package org.maia.amstrad.program.browser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.AmstradProgramRepository;

public class ProgramBrowserAction extends AmstradPcAction {

	private ProgramBrowserDisplaySource displaySource;

	private boolean infoMode;

	private static String NAME_OPEN = "Open program browser";

	private static String NAME_CLOSE = "Close program browser";

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addStateListener(this);
		amstradPc.addMonitorListener(this);
		amstradPc.addEventListener(this);
	}

	public void showProgramBrowser() {
		if (isEnabled()) {
			getAmstradPc().swapDisplaySource(getDisplaySource());
		}
	}

	public void hideProgramBrowser() {
		if (isEnabled()) {
			getAmstradPc().resetDisplaySource();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (isEnabled()) {
			if (NAME_OPEN.equals(getName())) {
				showProgramBrowser();
			} else {
				hideProgramBrowser();
			}
		}
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		super.amstradPcPausing(amstradPc);
		setEnabled(false);
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		super.amstradPcResuming(amstradPc);
		setEnabled(true);
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		super.amstradPcDisplaySourceChanged(amstradPc);
		updateName();
		if (!isProgramBrowserShowing()) {
			infoMode = false;
		}
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (isEnabled() && event instanceof AmstradPcKeyboardEvent) {
			KeyEvent key = ((AmstradPcKeyboardEvent) event).getKeyPressed();
			handleKeyEvent(key);
		}
	}

	private void handleKeyEvent(KeyEvent key) {
		int keyCode = key.getKeyCode();
		if (!isProgramBrowserShowing()) {
			if (keyCode == KeyEvent.VK_HOME) {
				showProgramBrowser();
			} else if (keyCode == KeyEvent.VK_F1) {
				AmstradProgram program = getDisplaySource().getLastLaunchedProgram();
				if (program != null && program.hasDescriptiveInfo()) {
					getDisplaySource().showDescriptiveInfo(program);
					showProgramBrowser();
					infoMode = true;
				}
			}
		} else {
			if (keyCode == KeyEvent.VK_F1 && infoMode) {
				hideProgramBrowser();
			}
		}
	}

	private void updateName() {
		if (isProgramBrowserShowing()) {
			changeName(NAME_CLOSE);
		} else {
			changeName(NAME_OPEN);
		}
	}

	public boolean isProgramBrowserShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getCurrentAlternativeDisplaySource();
		return altDisplaySource != null && altDisplaySource instanceof ProgramBrowserDisplaySource;
	}

	public ProgramBrowserDisplaySource getDisplaySource() {
		if (displaySource == null) {
			AmstradProgramRepository repository = AmstradFactory.getInstance().getAmstradContext()
					.getAmstradProgramRepository();
			displaySource = new ProgramBrowserDisplaySource(getAmstradPc(), repository);
		}
		return displaySource;
	}

}