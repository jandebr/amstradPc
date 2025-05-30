package org.maia.amstrad;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.maia.amstrad.gui.browser.classic.ClassicProgramInfoDisplaySource;
import org.maia.amstrad.gui.overlay.AutotypeDisplayOverlay;
import org.maia.amstrad.gui.overlay.ControlKeysDisplayOverlay;
import org.maia.amstrad.gui.overlay.PauseDisplayOverlay;
import org.maia.amstrad.gui.overlay.StackedDisplayOverlay;
import org.maia.amstrad.gui.overlay.SystemStatsDisplayOverlay;
import org.maia.amstrad.gui.overlay.TapeDisplayOverlay;
import org.maia.amstrad.gui.overlay.TurboDisplayOverlay;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcActions;
import org.maia.amstrad.pc.action.ProgramBrowserAction;
import org.maia.amstrad.pc.impl.cursor.AmstradMonitorCursorControllerImpl;
import org.maia.amstrad.pc.impl.jemu.JemuDirectAmstradPc;
import org.maia.amstrad.pc.impl.jemu.JemuFacadeAmstradPc;
import org.maia.amstrad.pc.impl.joystick.AmstradJoystickDevice;
import org.maia.amstrad.pc.impl.keyboard.virtual.AmstradVirtualKeyboardImpl;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard;
import org.maia.amstrad.pc.menu.maker.AmstradMenuDefaultLookAndFeel;
import org.maia.amstrad.pc.monitor.cursor.AmstradMonitorCursorController;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayOverlay;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramBuilder;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.program.browser.AmstradProgramBrowserStyle;
import org.maia.amstrad.program.browser.impl.CarouselAmstradProgramBrowser;
import org.maia.amstrad.program.browser.impl.ClassicAmstradProgramBrowser;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.facet.FacetedAmstradProgramRepository;
import org.maia.amstrad.program.repo.file.BasicProgramFileRepository;
import org.maia.amstrad.program.repo.filter.FilteredAmstradProgramRepository;
import org.maia.amstrad.program.repo.rename.RenamingAmstradProgramRepository;
import org.maia.amstrad.program.repo.search.SearchingAmstradProgramRepository;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.impl.AmstradDesktopSystem;
import org.maia.amstrad.system.impl.AmstradEntertainmentSystem;
import org.maia.amstrad.system.impl.AmstradJavaCpcSystem;

import jemu.core.device.Computer;
import jemu.settings.Settings;
import jemu.ui.Display;
import jemu.ui.DisplayCanvasRenderDelegate;
import jemu.ui.DisplayClassicRenderDelegate;
import jemu.ui.DisplayRenderDelegate;
import jemu.ui.DisplayStagedRenderDelegate;

public class AmstradFactory {

	private AmstradContext context;

	private static AmstradFactory instance;

	static {
		new AmstradMenuDefaultLookAndFeel(); // Remember default Look & Feel settings
	}

	private AmstradFactory() {
	}

	public AmstradContext getAmstradContext() {
		if (context == null) {
			AmstradSettings userSettings = createUserSettings();
			context = new AmstradContextImpl(userSettings, System.out, System.err);
		}
		return context;
	}

	private AmstradSettings createUserSettings() {
		return new AmstradSettingsImpl();
	}

	public AmstradSystem createAmstradSystem() {
		AmstradSystem system = null;
		String systemName = getAmstradContext().getUserSettings().get(AmstradContext.SETTING_AMSTRAD_SYSTEM,
				AmstradDesktopSystem.NAME);
		if (systemName.equalsIgnoreCase(AmstradDesktopSystem.NAME)) {
			system = new AmstradDesktopSystem();
		} else if (systemName.equalsIgnoreCase(AmstradEntertainmentSystem.NAME)) {
			system = new AmstradEntertainmentSystem();
		} else if (systemName.equalsIgnoreCase(AmstradJavaCpcSystem.NAME)) {
			system = new AmstradJavaCpcSystem();
		} else {
			// default (Desktop)
			system = new AmstradDesktopSystem();
		}
		return system;
	}

	public AmstradPc createAmstradPc() {
		return createJemuAmstradPc();
	}

	public AmstradPc createJemuAmstradPc() {
		Computer computer = createJemuComputer();
		Display display = createJemuDisplay();
		return customizeAmstradPc(new JemuDirectAmstradPc(computer, display));
	}

	private Computer createJemuComputer() {
		Computer computer = null;
		String computerSystem = Settings.get(Settings.SYSTEM, "CPC464");
		try {
			computer = Computer.createComputer(null, computerSystem);
		} catch (Exception e) {
			System.err.println(e);
		}
		return computer;
	}

	private Display createJemuDisplay() {
		return new Display(createDisplayRenderDelegate());
	}

	private DisplayRenderDelegate createDisplayRenderDelegate() {
		DisplayRenderDelegate delegate = null;
		String name = Settings.get(Settings.DISPLAY_RENDER_DELEGATE, DisplayClassicRenderDelegate.NAME).trim();
		if (DisplayClassicRenderDelegate.NAME.equalsIgnoreCase(name)) {
			delegate = new DisplayClassicRenderDelegate();
		} else if (DisplayStagedRenderDelegate.NAME.equalsIgnoreCase(name)) {
			delegate = new DisplayStagedRenderDelegate();
		} else if (DisplayCanvasRenderDelegate.NAME.equalsIgnoreCase(name)) {
			delegate = new DisplayCanvasRenderDelegate();
		} else {
			delegate = new DisplayClassicRenderDelegate();
		}
		System.out.println("Display render delegate " + delegate.getName());
		return delegate;
	}

	public AmstradPc createJemuClassicAmstradPc() {
		return customizeAmstradPc(new JemuFacadeAmstradPc());
	}

	private AmstradPc customizeAmstradPc(AmstradPc amstradPc) {
		amstradPc.getMonitor().setCustomDisplayOverlay(createCustomDisplayOverlay(amstradPc));
		if (getAmstradContext().isLowPerformance())
			getAmstradContext().activateLowPerformance(amstradPc);
		return amstradPc;
	}

	private AmstradDisplayOverlay createCustomDisplayOverlay(AmstradPc amstradPc) {
		StackedDisplayOverlay overlay = new StackedDisplayOverlay();
		overlay.addOverlay(new PauseDisplayOverlay(amstradPc), 2);
		overlay.addOverlay(new TurboDisplayOverlay(amstradPc), 2);
		overlay.addOverlay(new AutotypeDisplayOverlay(amstradPc), 2);
		overlay.addOverlay(new TapeDisplayOverlay(amstradPc), 2);
		overlay.addOverlay(new SystemStatsDisplayOverlay(amstradPc), 1);
		overlay.addOverlay(new ControlKeysDisplayOverlay(amstradPc), 1);
		overlay.addOverlay(amstradPc.getVirtualKeyboardDisplayOverlay(), 0);
		return overlay;
	}

	public AmstradJoystick createJoystick(AmstradPc amstradPc, AmstradJoystickID joystickId) {
		return new AmstradJoystickDevice(amstradPc, joystickId);
	}

	public AmstradMonitorCursorController createCursorController(AmstradPc amstradPc) {
		return new AmstradMonitorCursorControllerImpl(amstradPc);
	}

	public AmstradVirtualKeyboard createVirtualKeyboard(AmstradPc amstradPc) {
		return new AmstradVirtualKeyboardImpl(amstradPc);
	}

	public AmstradProgramRepository createProgramRepository() {
		AmstradProgramRepositoryConfiguration config = getAmstradContext().getProgramRepositoryConfiguration();
		AmstradProgramRepository repository = new BasicProgramFileRepository(config.getRootFolder());
		repository = new FilteredAmstradProgramRepository(repository); // filters out hidden programs
		if (config.isHideSequenceNumbers()) {
			repository = RenamingAmstradProgramRepository.withSequenceNumbersHidden(repository);
		}
		if (config.isSearchByProgramName()) {
			repository = SearchingAmstradProgramRepository.withSearchByProgramName(repository,
					config.getSearchString());
		}
		if (config.isFaceted()) {
			repository = new FacetedAmstradProgramRepository(repository, config.getFacets());
		}
		return repository;
	}

	public AmstradProgramBrowser createProgramBrowser(AmstradPc amstradPc) {
		AmstradProgramBrowser browser = null;
		AmstradProgramRepository repository = createProgramRepository();
		AmstradProgramBrowserStyle browserStyle = getAmstradContext().getProgramBrowserStyle();
		if (AmstradProgramBrowserStyle.CLASSIC.equals(browserStyle)) {
			browser = new ClassicAmstradProgramBrowser(amstradPc, repository);
		} else if (AmstradProgramBrowserStyle.CAROUSEL.equals(browserStyle)) {
			browser = new CarouselAmstradProgramBrowser(amstradPc, repository);
		}
		return browser;
	}

	public ClassicProgramInfoDisplaySource createProgramInfoDisplaySource(AmstradPc amstradPc, AmstradProgram program) {
		AmstradProgramBrowser programBrowser = getAmstradContext().getProgramBrowser(amstradPc);
		return new ClassicProgramInfoDisplaySource(programBrowser, program);
	}

	public AmstradPcSnapshotFile createCpcSnapshotProgram(File snapshotFile) {
		return createCpcSnapshotProgram(snapshotFile.getName(), snapshotFile);
	}

	public AmstradPcSnapshotFile createCpcSnapshotProgram(String programName, File snapshotFile) {
		return new AmstradPcSnapshotFile(programName, snapshotFile);
	}

	public AmstradProgram createBasicProgram(File basicFile) {
		return createBasicProgram(basicFile.getName(), basicFile);
	}

	public AmstradProgram createBasicProgram(String programName, File basicFile) {
		return new AmstradBasicProgramFile(programName, basicFile);
	}

	public AmstradProgram createBasicDescribedProgram(File basicFile, File metadataFile)
			throws AmstradProgramException {
		return createBasicDescribedProgram(basicFile.getName(), basicFile, metadataFile);
	}

	public AmstradProgram createBasicDescribedProgram(String programName, File basicFile, File metadataFile)
			throws AmstradProgramException {
		AmstradProgram program = createBasicProgram(programName, basicFile);
		AmstradProgramBuilder builder = AmstradProgramBuilder.createFor(program);
		try {
			builder.loadAmstradMetaData(metadataFile);
		} catch (IOException e) {
			throw new AmstradProgramException(program, e);
		}
		return builder.build();
	}

	public static AmstradFactory getInstance() {
		if (instance == null) {
			setInstance(new AmstradFactory());
		}
		return instance;
	}

	private static synchronized void setInstance(AmstradFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

	private class AmstradContextImpl extends AmstradContext {

		private AmstradSettings userSettings;

		private PrintStream consoleOutputStream;

		private PrintStream consoleErrorStream;

		private Map<AmstradPc, Boolean> basicProtectiveModes;

		public AmstradContextImpl(AmstradSettings userSettings, PrintStream consoleOutputStream,
				PrintStream consoleErrorStream) {
			this.userSettings = userSettings;
			this.consoleOutputStream = consoleOutputStream;
			this.consoleErrorStream = consoleErrorStream;
			this.basicProtectiveModes = new HashMap<AmstradPc, Boolean>();
		}

		@Override
		public AmstradSettings getUserSettings() {
			return userSettings;
		}

		@Override
		public PrintStream getConsoleOutputStream() {
			return consoleOutputStream;
		}

		@Override
		public PrintStream getConsoleErrorStream() {
			return consoleErrorStream;
		}

		@Override
		public AmstradProgramBrowser getProgramBrowser(AmstradPc amstradPc) {
			AmstradProgramBrowser browser = null;
			ProgramBrowserAction browserAction = amstradPc.getActions().getProgramBrowserAction();
			if (browserAction != null) {
				browser = browserAction.getProgramBrowser();
			}
			return browser;
		}

		@Override
		public void showProgramBrowser(AmstradPc amstradPc) {
			if (!isProgramBrowserShowing(amstradPc)) {
				ProgramBrowserAction browserAction = amstradPc.getActions().getProgramBrowserAction();
				if (browserAction != null) {
					browserAction.showProgramBrowser();
				}
			}
		}

		@Override
		public boolean isBasicProtectiveMode(AmstradPc amstradPc) {
			Boolean protective = basicProtectiveModes.get(amstradPc);
			return protective != null && protective.booleanValue();
		}

		@Override
		public void setBasicProtectiveMode(AmstradPc amstradPc, boolean protective) {
			basicProtectiveModes.put(amstradPc, Boolean.valueOf(protective));
			AmstradPcActions actions = amstradPc.getActions();
			actions.getSaveBasicSourceFileAction().setEnabled(!protective);
			actions.getSaveBasicBinaryFileAction().setEnabled(!protective);
			actions.getSaveSnapshotFileAction().setEnabled(!protective);
		}

	}

}