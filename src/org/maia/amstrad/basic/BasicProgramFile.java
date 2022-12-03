package org.maia.amstrad.basic;

import java.io.File;

import org.maia.amstrad.program.AmstradProgramStoredInFile;
import org.maia.amstrad.program.AmstradProgramType;

public class BasicProgramFile extends AmstradProgramStoredInFile {

	public BasicProgramFile(File file) {
		this(file.getName(), file);
	}

	public BasicProgramFile(String programName, File file) {
		super(AmstradProgramType.BASIC_PROGRAM, programName, file);
	}

}