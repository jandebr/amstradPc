package org.maia.amstrad.pc;

import java.io.File;
import java.io.PrintStream;

import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.facet.FacetFactory;

public abstract class AmstradContext {

	private static final String SETTING_CURRENT_DIR = "current_dir";

	private static final String SETTING_PROGRAM_REPO_DIR = "program_repo.file.dir";

	private static final String SETTING_PROGRAM_REPO_HIDE_SEQNR = "program_repo.rename.hide_seqnr";

	private static final String SETTING_PROGRAM_REPO_SEARCH_BY_NAME = "program_repo.search.by_name";

	private static final String SETTING_PROGRAM_REPO_SEARCH_STRING = "program_repo.search.string";

	private static final String SETTING_PROGRAM_REPO_FACETED = "program_repo.facet.enable";

	private static final String SETTING_PROGRAM_REPO_FACETS = "program_repo.facet.facets";

	protected AmstradContext() {
	}

	public abstract AmstradSettings getUserSettings();

	public abstract PrintStream getConsoleOutputStream();

	public abstract PrintStream getConsoleErrorStream();

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
		return new File(getUserSettings().get(SETTING_PROGRAM_REPO_DIR, "."));
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
		configuration.setFacets(FacetFactory.getInstance().fromExternalForm(
				settings.get(SETTING_PROGRAM_REPO_FACETS, "")));
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

}