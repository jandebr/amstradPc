package org.maia.amstrad.program.browser;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.util.GenericListenerList;

public abstract class AmstradProgramBrowser {

	private AmstradPc amstradPc;

	private AmstradProgramRepository programRepository;

	private ProgramBrowserDisplaySource displaySource;

	private GenericListenerList<AmstradProgramBrowserListener> listeners;

	protected AmstradProgramBrowser(AmstradPc amstradPc, AmstradProgramRepository programRepository) {
		this.amstradPc = amstradPc;
		this.programRepository = programRepository;
		this.displaySource = createDisplaySource();
		this.listeners = new GenericListenerList<AmstradProgramBrowserListener>();
	}

	protected abstract ProgramBrowserDisplaySource createDisplaySource();

	public void addListener(AmstradProgramBrowserListener listener) {
		getListeners().addListener(listener);
	}

	public void removeListener(AmstradProgramBrowserListener listener) {
		getListeners().removeListener(listener);
	}

	public void fireProgramLoaded(AmstradProgram program) {
		for (AmstradProgramBrowserListener listener : getListeners()) {
			listener.programLoadedFromBrowser(this, program);
		}
	}

	public void fireProgramRun(AmstradProgram program) {
		for (AmstradProgramBrowserListener listener : getListeners()) {
			listener.programRunFromBrowser(this, program);
		}
	}

	public AmstradProgram getCurrentProgram() {
		return getDisplaySource().getCurrentProgram();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	public AmstradProgramRepository getProgramRepository() {
		return programRepository;
	}

	public ProgramBrowserDisplaySource getDisplaySource() {
		return displaySource;
	}

	private GenericListenerList<AmstradProgramBrowserListener> getListeners() {
		return listeners;
	}

}