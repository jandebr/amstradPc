package org.maia.amstrad.program.browser;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.overlay.controlkeys.ControlKey;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramType;
import org.maia.amstrad.program.browser.config.AmstradProgramBrowserCoverImageOption;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.util.GenericListenerList;

public abstract class AmstradProgramBrowser {

	private AmstradPc amstradPc;

	private AmstradProgramRepository programRepository;

	private AmstradProgramBrowserStyle style;

	private ProgramBrowserDisplaySource displaySource;

	private List<ControlKey> additionalControlKeys;

	private GenericListenerList<AmstradProgramBrowserListener> listeners;

	private static final String SETTING_COVER_IMAGE_FOLDER_OPTION = "program_browser.cover_images.folders";

	private static final String SETTING_COVER_IMAGE_PROGRAM_OPTION = "program_browser.cover_images.programs";

	private static final String SETTING_ENABLE_BASIC_STAGING = "basic_staging.enable";

	protected AmstradProgramBrowser(AmstradPc amstradPc, AmstradProgramRepository programRepository,
			AmstradProgramBrowserStyle style) {
		this.amstradPc = amstradPc;
		this.programRepository = programRepository;
		this.style = style;
		this.displaySource = createDisplaySource();
		this.additionalControlKeys = new Vector<ControlKey>();
		this.listeners = new GenericListenerList<AmstradProgramBrowserListener>();
	}

	protected abstract ProgramBrowserDisplaySource createDisplaySource();

	protected void addAdditionalControlKey(ControlKey controlKey) {
		getAdditionalControlKeys().add(controlKey);
	}

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

	public AmstradProgramBrowserCoverImageOption getCoverImageOptionForFolders() {
		return getCoverImageOption(SETTING_COVER_IMAGE_FOLDER_OPTION, getDefaultCoverImageOptionForFolders());
	}

	public AmstradProgramBrowserCoverImageOption getCoverImageOptionForPrograms() {
		return getCoverImageOption(SETTING_COVER_IMAGE_PROGRAM_OPTION, getDefaultCoverImageOptionForPrograms());
	}

	private AmstradProgramBrowserCoverImageOption getCoverImageOption(String setting,
			AmstradProgramBrowserCoverImageOption defaultOption) {
		AmstradProgramBrowserCoverImageOption option = null;
		String value = getAmstradSettings().get(setting + "." + getStyle().getDisplayName().toLowerCase(), null);
		if (value == null) {
			value = getAmstradSettings().get(setting, null);
		}
		if (value != null) {
			try {
				option = AmstradProgramBrowserCoverImageOption.valueOf(value.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
			}
		}
		if (option == null) {
			option = defaultOption;
		}
		return option;
	}

	protected AmstradProgramBrowserCoverImageOption getDefaultCoverImageOptionForFolders() {
		return AmstradProgramBrowserCoverImageOption.REPOSITORY;
	}

	protected AmstradProgramBrowserCoverImageOption getDefaultCoverImageOptionForPrograms() {
		return AmstradProgramBrowserCoverImageOption.REPOSITORY;
	}

	public abstract boolean isShowMonitor();

	public abstract boolean isShowPause();

	public abstract boolean isShowControlKeys();

	public abstract boolean isMonitorResizable();

	public boolean isStagedRun(AmstradProgram program) {
		if (!AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType()))
			return false;
		if (program.isNoStage())
			return false;
		if (!getAmstradSettings().getBool(SETTING_ENABLE_BASIC_STAGING, true))
			return false;
		return true;
	}

	private AmstradSettings getAmstradSettings() {
		return AmstradFactory.getInstance().getAmstradContext().getUserSettings();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	public AmstradProgramRepository getProgramRepository() {
		return programRepository;
	}

	public AmstradProgramBrowserStyle getStyle() {
		return style;
	}

	public ProgramBrowserDisplaySource getDisplaySource() {
		return displaySource;
	}

	public List<ControlKey> getAdditionalControlKeys() {
		return additionalControlKeys;
	}

	private GenericListenerList<AmstradProgramBrowserListener> getListeners() {
		return listeners;
	}

}