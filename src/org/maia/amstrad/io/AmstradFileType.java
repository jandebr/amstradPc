package org.maia.amstrad.io;

import java.io.File;

import org.maia.amstrad.program.AmstradProgramType;

public enum AmstradFileType {

	BASIC_SOURCE_CODE_FILE(".bas", false, AmstradProgramType.BASIC_PROGRAM),

	BASIC_BYTE_CODE_FILE(".bin", true, AmstradProgramType.BASIC_PROGRAM),

	JAVACPC_SNAPSHOT_FILE_UNCOMPRESSED(".sna", true, AmstradProgramType.CPC_SNAPSHOT),

	JAVACPC_SNAPSHOT_FILE_COMPRESSED(".snz", true, AmstradProgramType.CPC_SNAPSHOT),

	AMSTRAD_METADATA_FILE(".amd", false);

	private String fileExtension;

	private boolean binaryFileData;

	private AmstradProgramType programType;

	private AmstradFileType(String fileExtension, boolean binaryFileData) {
		this(fileExtension, binaryFileData, null);
	}

	private AmstradFileType(String fileExtension, boolean binaryFileData, AmstradProgramType programType) {
		this.fileExtension = fileExtension;
		this.binaryFileData = binaryFileData;
		this.programType = programType;
	}

	public static AmstradFileType guessFileType(File file) {
		for (AmstradFileType type : AmstradFileType.values()) {
			if (type.matches(file))
				return type;
		}
		return null;
	}

	public boolean matches(File file) {
		return file != null && file.getName().toLowerCase().endsWith(getFileExtension());
	}

	public String getFileExtensionWithoutDot() {
		return getFileExtension().substring(1);
	}

	public boolean isAmstradProgram() {
		return getProgramType() != null;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public boolean isBinaryFileData() {
		return binaryFileData;
	}

	public AmstradProgramType getProgramType() {
		return programType;
	}

}