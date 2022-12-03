package org.maia.amstrad.pc;

import java.io.File;

import org.maia.amstrad.program.AmstradProgramStoredInFile;
import org.maia.amstrad.program.AmstradProgramType;

public class AmstradPcSnapshotFile extends AmstradProgramStoredInFile {

	public AmstradPcSnapshotFile(File file) {
		this(file.getName(), file);
	}

	public AmstradPcSnapshotFile(String programName, File file) {
		super(AmstradProgramType.CPC_SNAPSHOT, programName, file);
	}

}