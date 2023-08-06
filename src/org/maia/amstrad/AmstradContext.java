package org.maia.amstrad;

import java.io.File;
import java.io.PrintStream;

import org.maia.amstrad.gui.terminate.AmstradTerminationDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcActions;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.facet.FacetFactory;

public abstract class AmstradContext {

	private static final String SETTING_MODE = "mode";

	private static final String SETTING_CURRENT_DIR = "current_dir";

	private static final String SETTING_PROGRAM_REPO_DIR = "program_repo.file.dir";

	private static final String SETTING_PROGRAM_REPO_HIDE_SEQNR = "program_repo.rename.hide_seqnr";

	private static final String SETTING_PROGRAM_REPO_SEARCH_BY_NAME = "program_repo.search.by_name";

	private static final String SETTING_PROGRAM_REPO_SEARCH_STRING = "program_repo.search.string";

	private static final String SETTING_PROGRAM_REPO_FACETED = "program_repo.facet.enable";

	private static final String SETTING_PROGRAM_REPO_FACETS = "program_repo.facet.facets";

	private static final String SETTING_PROGRAM_REPO_DIR_MANAGED = "program_repo.file.dir-managed";

	private static final String SETTING_PROGRAM_REPO_DIR_MANAGED_CLEANUP = "program_repo.file.dir-managed.cleanup.enable";

	private static final String SETTING_LOWPERFORMANCE = "lowperformance";

	private static final String SETTING_LOWPERFORMANCE_ALLOW_MONITOR_EFFECT = "lowperformance.allow_display_effect";

	private static final String SETTING_LOWPERFORMANCE_ALLOW_BILINEAR_EFFECT = "lowperformance.allow_bilinear";

	private static final String SETTING_LOWPERFORMANCE_ALLOW_SCANLINES_EFFECT = "lowperformance.allow_scanlines";

	private static final String SETTING_TERMINATE_ANIMATE = "quit.animate";

	private static final String SETTING_TERMINATE_SYSTEM_COMMAND = "quit.command";

	private static final String SYSTEM_PROPERTY_GETDOWN = "com.threerings.getdown";

	private static final String SYSTEM_PROPERTY_VERSION = "javacpc-version";

	protected AmstradContext() {
	}

	public abstract AmstradSettings getUserSettings();

	public abstract PrintStream getConsoleOutputStream();

	public abstract PrintStream getConsoleErrorStream();

	public abstract void initJavaConsole();

	public abstract void showJavaConsole();

	public abstract void showProgramBrowser(AmstradPc amstradPc);

	public boolean isProgramBrowserShowing(AmstradPc amstradPc) {
		return isTypedAlternativeDisplaySourceShowing(amstradPc, AmstradAlternativeDisplaySourceType.PROGRAM_BROWSER);
	}

	public boolean isProgramStandaloneInfoShowing(AmstradPc amstradPc) {
		return isTypedAlternativeDisplaySourceShowing(amstradPc, AmstradAlternativeDisplaySourceType.PROGRAM_STANDALONE_INFO);
	}

	public boolean isImageShowing(AmstradPc amstradPc) {
		return isTypedAlternativeDisplaySourceShowing(amstradPc, AmstradAlternativeDisplaySourceType.IMAGE);
	}

	public boolean isTerminationShowing(AmstradPc amstradPc) {
		return isTypedAlternativeDisplaySourceShowing(amstradPc, AmstradAlternativeDisplaySourceType.TERMINATION);
	}

	private boolean isTypedAlternativeDisplaySourceShowing(AmstradPc amstradPc,
			AmstradAlternativeDisplaySourceType type) {
		AmstradAlternativeDisplaySource altDisplaySource = amstradPc.getMonitor().getCurrentAlternativeDisplaySource();
		if (altDisplaySource == null)
			return false;
		return type.equals(altDisplaySource.getType());
	}

	public boolean isLowPerformance() {
		return getUserSettings().getBool(SETTING_LOWPERFORMANCE, false);
	}

	public void activateLowPerformance(AmstradPc amstradPc) {
		getUserSettings().setBool(SETTING_LOWPERFORMANCE, true);
		// Turn off cpu-intensive monitor options
		AmstradMonitor monitor = amstradPc.getMonitor();
		boolean allowMonitor = getUserSettings().getBool(SETTING_LOWPERFORMANCE_ALLOW_MONITOR_EFFECT, false);
		boolean allowBilinear = getUserSettings().getBool(SETTING_LOWPERFORMANCE_ALLOW_BILINEAR_EFFECT, false);
		boolean allowScanlines = getUserSettings().getBool(SETTING_LOWPERFORMANCE_ALLOW_SCANLINES_EFFECT, false);
		if (!allowMonitor)
			monitor.setMonitorEffect(false);
		if (!allowBilinear)
			monitor.setMonitorBilinearEffect(false);
		if (!allowScanlines)
			monitor.setMonitorScanLinesEffect(false);
		// Disable monitor options
		AmstradPcActions actions = amstradPc.getActions();
		actions.getMonitorEffectAction().setEnabled(allowMonitor);
		actions.getMonitorBilinearEffectAction().setEnabled(allowBilinear);
		actions.getMonitorScanLinesEffectAction().setEnabled(allowScanlines);
	}

	public abstract boolean isBasicProtectiveMode(AmstradPc amstradPc);

	public abstract void setBasicProtectiveMode(AmstradPc amstradPc, boolean protective);

	public AmstradMode getMode() {
		AmstradMode mode = AmstradMode.forName(getUserSettings().get(SETTING_MODE, AmstradMode.DEFAULT.getName()));
		if (mode != null) {
			return mode;
		} else {
			return AmstradMode.DEFAULT;
		}
	}

	public File getCurrentDirectory() {
		String dir = getUserSettings().get(SETTING_CURRENT_DIR, null);
		if (dir != null) {
			return new File(dir);
		} else {
			return getProgramRepositoryConfiguration().getRootFolder();
		}
	}

	public void setCurrentDirectory(File currentDirectory) {
		if (!currentDirectory.isDirectory())
			throw new IllegalArgumentException("The current directory must be a directory");
		getUserSettings().set(SETTING_CURRENT_DIR, currentDirectory.getAbsolutePath());
	}

	public File getProgramRepositoryRootFolder() {
		File rootFolder = new File(getUserSettings().get(SETTING_PROGRAM_REPO_DIR, "."));
		if (!rootFolder.exists() || !rootFolder.isDirectory())
			rootFolder = new File(".");
		return rootFolder;
	}

	public void setProgramRepositoryRootFolder(File rootFolder) {
		if (!rootFolder.isDirectory())
			throw new IllegalArgumentException("The root folder must be a directory");
		getUserSettings().set(SETTING_PROGRAM_REPO_DIR, rootFolder.getAbsolutePath());
	}

	public AmstradProgramRepositoryConfiguration getProgramRepositoryConfiguration() {
		AmstradSettings settings = getUserSettings();
		AmstradProgramRepositoryConfiguration configuration = new AmstradProgramRepositoryConfiguration();
		configuration.setRootFolder(getProgramRepositoryRootFolder());
		configuration.setHideSequenceNumbers(settings.getBool(SETTING_PROGRAM_REPO_HIDE_SEQNR, true));
		configuration.setSearchByProgramName(settings.getBool(SETTING_PROGRAM_REPO_SEARCH_BY_NAME, false));
		configuration.setSearchString(settings.get(SETTING_PROGRAM_REPO_SEARCH_STRING, ""));
		configuration.setFaceted(settings.getBool(SETTING_PROGRAM_REPO_FACETED, false));
		configuration
				.setFacets(FacetFactory.getInstance().fromExternalForm(settings.get(SETTING_PROGRAM_REPO_FACETS, "")));
		return configuration;
	}

	public void setProgramRepositoryConfiguration(AmstradProgramRepositoryConfiguration configuration) {
		setProgramRepositoryRootFolder(configuration.getRootFolder());
		AmstradSettings settings = getUserSettings();
		settings.setBool(SETTING_PROGRAM_REPO_HIDE_SEQNR, configuration.isHideSequenceNumbers());
		settings.setBool(SETTING_PROGRAM_REPO_SEARCH_BY_NAME, configuration.isSearchByProgramName());
		settings.set(SETTING_PROGRAM_REPO_SEARCH_STRING, configuration.getSearchString());
		settings.setBool(SETTING_PROGRAM_REPO_FACETED, configuration.isFaceted());
		settings.set(SETTING_PROGRAM_REPO_FACETS, FacetFactory.getInstance().toExternalForm(configuration.getFacets()));
	}

	public File getManagedProgramRepositoryRootFolder() {
		File folder = null;
		String path = getUserSettings().get(SETTING_PROGRAM_REPO_DIR_MANAGED, "");
		if (!path.isEmpty()) {
			folder = new File(path);
		}
		return folder;
	}

	public boolean isManagedProgramRepositoryCleanupEnabled() {
		return getUserSettings().getBool(SETTING_PROGRAM_REPO_DIR_MANAGED_CLEANUP, true);
	}

	public boolean isLaunchedByGetdown() {
		return Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_GETDOWN, "false"));
	}

	public String getVersionString() {
		return System.getProperty(SYSTEM_PROPERTY_VERSION, "");
	}

	public boolean isAnimateOnTerminate() {
		return getUserSettings().getBool(SETTING_TERMINATE_ANIMATE, false);
	}

	public String getSystemCommandOnTerminate() {
		String mode = getMode().getName().toLowerCase();
		String command = getUserSettings().get(SETTING_TERMINATE_SYSTEM_COMMAND + "." + mode, null);
		if (command == null) {
			command = getUserSettings().get(SETTING_TERMINATE_SYSTEM_COMMAND, null);
		}
		return command;
	}

	public void executeSystemCommand(String systemCommand) {
		if (systemCommand == null || systemCommand.isEmpty())
			return;
		try {
			System.out.println("Executing system command: " + systemCommand);
			Runtime.getRuntime().exec(systemCommand.trim().split(" "));
			System.out.println("System command executed");
		} catch (Exception e) {
			System.err.println("Failed to execute system command");
			System.err.println(e);
		}
	}

}