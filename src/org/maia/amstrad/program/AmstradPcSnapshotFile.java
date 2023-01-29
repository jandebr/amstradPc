package org.maia.amstrad.program;

import java.io.File;

public class AmstradPcSnapshotFile extends AmstradProgramStoredInFile {

	public AmstradPcSnapshotFile(File file) {
		this(file.getName(), file);
	}

	public AmstradPcSnapshotFile(String programName, File file) {
		super(AmstradProgramType.CPC_SNAPSHOT, programName, file);
	}

}