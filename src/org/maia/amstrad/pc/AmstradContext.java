package org.maia.amstrad.pc;

import java.io.File;
import java.io.PrintStream;

import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.FileBasedAmstradProgramRepository;

public abstract class AmstradContext {

	private static final String SETTING_PROGRAMS_DIR = "programs_dir";

	private static final String SETTING_CURRENT_DIR = "current_dir";

	protected AmstradContext() {
	}

	public abstract AmstradSettings getUserSettings();

	public abstract PrintStream getConsoleOutputStream();

	public abstract PrintStream getConsoleErrorStream();

	public AmstradProgramRepository getAmstradProgramRepository() {
		return new FileBasedAmstradProgramRepository(getAmstradProgramRepositoryRootFolder());
	}

	public File getAmstradProgramRepositoryRootFolder() {
		return new File(getUserSettings().get(SETTING_PROGRAMS_DIR, "."));
	}

	public void setAmstradProgramRepositoryRootFolder(File rootFolder) {
		if (!rootFolder.isDirectory())
			throw new IllegalArgumentException("The root folder must be a directory");
		getUserSettings().set(SETTING_PROGRAMS_DIR, rootFolder.getAbsolutePath());
	}

	public File getCurrentDirectory() {
		String dir = getUserSettings().get(SETTING_CURRENT_DIR, null);
		if (dir != null) {
			return new File(dir);
		} else {
			return getAmstradProgramRepositoryRootFolder();
		}
	}

	public void setCurrentDirectory(File currentDirectory) {
		if (!currentDirectory.isDirectory())
			throw new IllegalArgumentException("The current directory must be a directory");
		getUserSettings().set(SETTING_CURRENT_DIR, currentDirectory.getAbsolutePath());
	}

}