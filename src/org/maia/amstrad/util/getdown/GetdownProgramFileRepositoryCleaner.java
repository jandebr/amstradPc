package org.maia.amstrad.util.getdown;

import java.io.File;

import org.maia.amstrad.util.ProgramFileRepositoryCleaner;

public class GetdownProgramFileRepositoryCleaner extends ProgramFileRepositoryCleaner {

	public GetdownProgramFileRepositoryCleaner() {
	}

	@Override
	protected boolean isFileEligibleForRemoval(File file) {
		if (isGetdownVersionFile(file))
			return false;
		File versionFile = new File(file.getParentFile(), file.getName() + "v");
		if (!versionFile.exists())
			return true;
		if (!versionFile.isFile())
			return true;
		return false;
	}

	private boolean isGetdownVersionFile(File file) {
		String name = file.getName();
		if (name.endsWith("v")) {
			File versionedFile = new File(file.getParentFile(), name.substring(0, name.length() - 1));
			return versionedFile.exists() && versionedFile.isFile();
		} else {
			return false;
		}
	}

}