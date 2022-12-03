package org.maia.amstrad.program.repo.file;

import java.io.File;

import org.maia.amstrad.basic.BasicProgramFile;
import org.maia.amstrad.pc.AmstradFileType;
import org.maia.amstrad.program.AmstradProgramStoredInFile;

public class BasicProgramFileRepository extends FileBasedAmstradProgramRepository {

	public BasicProgramFileRepository(File rootFolder) {
		super(rootFolder);
	}

	public BasicProgramFileRepository(File rootFolder, boolean folderPerProgram) {
		super(rootFolder, folderPerProgram);
	}

	@Override
	protected boolean isProgramFile(File file) {
		return AmstradFileType.BASIC_SOURCE_CODE_FILE.matches(file)
				|| AmstradFileType.BASIC_BYTE_CODE_FILE.matches(file);
	}

	@Override
	protected boolean isRemasteredProgramFile(File file) {
		return isProgramFile(file) && file.getName().toLowerCase().contains("remastered");
	}

	@Override
	protected AmstradProgramStoredInFile createProgram(String programName, File file) {
		return new BasicProgramFile(programName, file);
	}

}