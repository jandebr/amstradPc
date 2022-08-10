package org.maia.amstrad.pc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
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

	public static void sleep(long milliseconds) {
		if (milliseconds > 0L) {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

	public static CharSequence readTextFileContents(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder(2048);
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}
		reader.close();
		return sb;
	}

	public static byte[] readBinaryFileContents(File file) throws IOException {
		byte[] data = new byte[(int) file.length()];
		byte[] buffer = new byte[2048];
		int dataIndex = 0;
		FileInputStream in = new FileInputStream(file);
		int bytesRead = in.read(buffer);
		while (bytesRead >= 0) {
			System.arraycopy(buffer, 0, data, dataIndex, bytesRead);
			dataIndex += bytesRead;
			bytesRead = in.read(buffer);
		}
		in.close();
		return data;
	}

}