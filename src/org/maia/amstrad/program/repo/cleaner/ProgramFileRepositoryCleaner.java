package org.maia.amstrad.program.repo.cleaner;

import java.io.File;
import java.util.List;
import java.util.Vector;

import jemu.ui.Console;

public abstract class ProgramFileRepositoryCleaner {

	protected ProgramFileRepositoryCleaner() {
	}

	public void cleanProgramRepository(File rootFolder, boolean removeEmptySubfolders) {
		Console.println("Cleaning program repository at " + rootFolder.getAbsolutePath());
		removeObsoleteProgramFiles(rootFolder);
		if (removeEmptySubfolders) {
			removeEmptyProgramSubfolders(rootFolder);
		}
		Console.println();
	}

	private void removeObsoleteProgramFiles(File rootFolder) {
		List<File> filesToRemove = new Vector<File>();
		gatherFilesEligibleForRemoval(rootFolder, filesToRemove);
		if (!filesToRemove.isEmpty()) {
			Console.println("Attempting to remove " + filesToRemove.size() + " obsolete program file(s)");
			int removed = 0;
			for (File file : filesToRemove) {
				Console.println("Removing " + file.getAbsolutePath());
				if (file.delete())
					removed++;
			}
			Console.println("Removed " + removed + " obsolete program file(s)");
		}
	}

	private void removeEmptyProgramSubfolders(File folder) {
		Console.println("Attempting to remove empty program subfolders");
		int removed = removeEmptySubfolders(folder);
		Console.println("Removed " + removed + " empty program subfolder(s)");
	}

	private int removeEmptySubfolders(File folder) {
		int removed = 0;
		for (File child : folder.listFiles()) {
			if (child.isDirectory()) {
				removed += removeEmptySubfolders(child);
				if (isEmptyFolder(child)) {
					Console.println("Removing " + child.getAbsolutePath());
					if (child.delete())
						removed++;
				}
			}
		}
		return removed;
	}

	private void gatherFilesEligibleForRemoval(File folder, List<File> filesToRemove) {
		for (File child : folder.listFiles()) {
			if (child.isDirectory()) {
				gatherFilesEligibleForRemoval(child, filesToRemove);
			} else if (child.isFile()) {
				if (isFileEligibleForRemoval(child)) {
					filesToRemove.add(child);
				}
			}
		}
	}

	protected abstract boolean isFileEligibleForRemoval(File file);

	private boolean isEmptyFolder(File folder) {
		return folder.list().length == 0;
	}

}