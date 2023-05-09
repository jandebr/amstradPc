package org.maia.amstrad.util;

import java.io.File;
import java.util.List;
import java.util.Vector;

public abstract class ProgramFileRepositoryCleaner {

	protected ProgramFileRepositoryCleaner() {
	}

	public void cleanProgramRepository(File rootFolder, boolean removeEmptySubfolders) {
		System.out.println("Checking program file repository for cleanup");
		removeObsoleteProgramFiles(rootFolder);
		if (removeEmptySubfolders) {
			removeEmptyProgramSubfolders(rootFolder);
		}
	}

	private void removeObsoleteProgramFiles(File rootFolder) {
		List<File> filesToRemove = new Vector<File>();
		gatherFilesEligibleForRemoval(rootFolder, filesToRemove);
		if (!filesToRemove.isEmpty()) {
			System.out.println("Attempting to remove " + filesToRemove.size() + " obsolete program file(s)");
			int removed = 0;
			for (File file : filesToRemove) {
				if (file.delete())
					removed++;
			}
			System.out.println("Removed " + removed + " obsolete program file(s)");
		}
	}

	private void removeEmptyProgramSubfolders(File folder) {
		System.out.println("Attempting to remove empty program subfolders");
		int removed = removeEmptySubfolders(folder);
		System.out.println("Removed " + removed + " empty program subfolder(s)");
	}

	private int removeEmptySubfolders(File folder) {
		int removed = 0;
		for (File child : folder.listFiles()) {
			if (child.isDirectory()) {
				removed += removeEmptySubfolders(child);
				if (isEmptyFolder(child)) {
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