package org.maia.amstrad;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.action.ProgramBrowserAction;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcActions;
import org.maia.amstrad.pc.impl.jemu.JemuAmstradPc;
import org.maia.amstrad.pc.monitor.display.overlay.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.overlay.AutotypeDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.overlay.PauseDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.overlay.StackedDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.overlay.SystemStatsDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.overlay.TapeDisplayOverlay;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramBuilder;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.facet.FacetedAmstradProgramRepository;
import org.maia.amstrad.program.repo.file.BasicProgramFileRepository;
import org.maia.amstrad.program.repo.rename.RenamingAmstradProgramRepository;
import org.maia.amstrad.program.repo.search.SearchingAmstradProgramRepository;

import jemu.ui.Console;

public class AmstradFactory {

	private static AmstradFactory instance;

	private AmstradContext context;

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

	public AmstradPc createAmstradPc() {
		JemuAmstradPc amstradPc = new JemuAmstradPc();
		amstradPc.getMonitor().setCustomDisplayOverlay(createDisplayOverlay(amstradPc));
		if (getAmstradContext().isLowPerformance())
			getAmstradContext().activateLowPerformance(amstradPc);
		return amstradPc;
	}

	private AmstradDisplayOverlay createDisplayOverlay(AmstradPc amstradPc) {
		StackedDisplayOverlay overlay = new StackedDisplayOverlay();
		overlay.addOverlay(new PauseDisplayOverlay(amstradPc), 1);
		overlay.addOverlay(new AutotypeDisplayOverlay(amstradPc), 1);
		overlay.addOverlay(new TapeDisplayOverlay(amstradPc), 1);
		overlay.addOverlay(new SystemStatsDisplayOverlay(amstradPc), 0);
		return overlay;
	}

	public AmstradProgramRepository createProgramRepository() {
		AmstradProgramRepositoryConfiguration config = getAmstradContext().getProgramRepositoryConfiguration();
		AmstradProgramRepository repository = new BasicProgramFileRepository(config.getRootFolder());
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

	public ProgramBrowserDisplaySource createProgramRepositoryBrowser(AmstradPc amstradPc) {
		AmstradProgramRepository repository = createProgramRepository();
		return ProgramBrowserDisplaySource.createProgramRepositoryBrowser(amstradPc, repository);
	}

	public ProgramBrowserDisplaySource createProgramInfo(AmstradPc amstradPc, AmstradProgram program) {
		return ProgramBrowserDisplaySource.createProgramInfo(amstradPc, program);
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
		public void initJavaConsole() {
			Console.init();
		}

		@Override
		public void showJavaConsole() {
			Console.frameconsole.setVisible(true);
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