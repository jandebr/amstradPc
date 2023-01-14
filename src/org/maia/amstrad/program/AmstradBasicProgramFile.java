package org.maia.amstrad.program;

import java.io.File;

public class AmstradBasicProgramFile extends AmstradProgramStoredInFile {

	public AmstradBasicProgramFile(File file) {
		this(file.getName(), file);
	}

	public AmstradBasicProgramFile(String programName, File file) {
		super(AmstradProgramType.BASIC_PROGRAM, programName, file);
	}

}