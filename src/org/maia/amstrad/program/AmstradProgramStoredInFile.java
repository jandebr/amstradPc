package org.maia.amstrad.program;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.pc.AmstradFileType;
import org.maia.amstrad.program.payload.AmstradProgramBinaryPayload;
import org.maia.amstrad.program.payload.AmstradProgramPayload;
import org.maia.amstrad.program.payload.AmstradProgramTextPayload;
import org.maia.amstrad.util.AmstradUtils;

public class AmstradProgramStoredInFile extends AmstradProgram {

	private File file;

	private boolean binaryFileData;

	public AmstradProgramStoredInFile(AmstradProgramType programType, File file) {
		this(programType, file, guessBinaryFileData(file));
	}

	public AmstradProgramStoredInFile(AmstradProgramType programType, File file, boolean binaryFileData) {
		this(programType, file.getName(), file, binaryFileData);
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
				payload = new AmstradProgramBinaryPayload(AmstradUtils.readBinaryFileContents(getFile()));
			} else {
				payload = new AmstradProgramTextPayload(AmstradUtils.readTextFileContents(getFile()));
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