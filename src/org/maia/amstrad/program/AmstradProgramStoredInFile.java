package org.maia.amstrad.program;

import java.io.File;

import org.maia.amstrad.AmstradFileType;
import org.maia.amstrad.program.payload.AmstradProgramBinaryFilePayload;
import org.maia.amstrad.program.payload.AmstradProgramPayload;
import org.maia.amstrad.program.payload.AmstradProgramTextFilePayload;

public class AmstradProgramStoredInFile extends AmstradProgram {

	private File file;

	private boolean binaryFileData;

	public AmstradProgramStoredInFile(File file) {
		this(file.getName(), file);
	}

	public AmstradProgramStoredInFile(String programName, File file) {
		this(guessProgramType(file), programName, file);
	}

	public AmstradProgramStoredInFile(AmstradProgramType programType, File file) {
		this(programType, file.getName(), file);
	}

	public AmstradProgramStoredInFile(AmstradProgramType programType, String programName, File file) {
		this(programType, programName, file, guessBinaryFileData(file));
	}

	public AmstradProgramStoredInFile(AmstradProgramType programType, String programName, File file,
			boolean binaryFileData) {
		super(programType, programName);
		this.file = file;
		this.binaryFileData = binaryFileData;
	}

	private static AmstradProgramType guessProgramType(File file) {
		AmstradFileType type = AmstradFileType.guessFileType(file);
		if (type != null)
			return type.getProgramType();
		else
			return null;
	}

	private static boolean guessBinaryFileData(File file) {
		AmstradFileType type = AmstradFileType.guessFileType(file);
		if (type != null)
			return type.isBinaryFileData();
		else
			return true; // arbitrary choice
	}

	@Override
	protected AmstradProgramPayload loadPayload() throws AmstradProgramException {
		AmstradProgramPayload payload = null;
		if (isBinaryFileData()) {
			payload = new AmstradProgramBinaryFilePayload(this, getFile());
		} else {
			payload = new AmstradProgramTextFilePayload(this, getFile());
		}
		return payload;
	}

	public File getFile() {
		return file;
	}

	private boolean isBinaryFileData() {
		return binaryFileData;
	}

}