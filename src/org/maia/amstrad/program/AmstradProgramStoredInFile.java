package org.maia.amstrad.program;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.io.AmstradFileType;
import org.maia.amstrad.io.AmstradIO;

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
		try {
			if (isBinaryFileData()) {
				payload = new AmstradProgramBinaryPayload(AmstradIO.readBinaryFileContents(getFile()));
			} else {
				payload = new AmstradProgramTextPayload(AmstradIO.readTextFileContents(getFile()));
			}
		} catch (IOException e) {
			throw new AmstradProgramException(this, "Could not load payload of " + getProgramName(), e);
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