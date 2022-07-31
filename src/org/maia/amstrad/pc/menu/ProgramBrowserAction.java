package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.io.File;

import jemu.settings.Settings;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.browser.repo.AmstradProgramRepository;
import org.maia.amstrad.pc.browser.repo.FileBasedAmstradProgramRepository;
import org.maia.amstrad.pc.browser.ui.ProgramBrowserDisplaySource;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;

public class ProgramBrowserAction extends AmstradPcAction {

	private ProgramBrowserDisplaySource displaySource;

	private static String NAME_OPEN = "Open program browser";

	private static String NAME_CLOSE = "Close program browser";

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addMonitorListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (NAME_OPEN.equals(getName())) {
			getAmstradPc().swapDisplaySource(getDisplaySource());
		} else {
			getAmstradPc().resetDisplaySource();
		}
	}

	private ProgramBrowserDisplaySource getDisplaySource() {
		if (displaySource == null) {
			AmstradProgramRepository repository = getAmstradProgramRepository();
			displaySource = new ProgramBrowserDisplaySource(getAmstradPc(), repository);
		}
		return displaySource;
	}

	private AmstradProgramRepository getAmstradProgramRepository() {
		File rootFolder = new File(Settings.get(Settings.PROGRAMS_DIR, "."));
		return new FileBasedAmstradProgramRepository(rootFolder, true, getAmstradPc().getMonitorMode());
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		super.amstradPcDisplaySourceChanged(amstradPc);
		updateName();
	}

	private void updateName() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getCurrentAlternativeDisplaySource();
		if (altDisplaySource == null) {
			// primary display is showing
			changeName(NAME_OPEN);
		} else if (altDisplaySource instanceof ProgramBrowserDisplaySource) {
			// program browser is showing
			changeName(NAME_CLOSE);
		}
	}

}