package org.maia.amstrad.pc;

import java.io.File;

public enum AmstradFileType {

	BASIC_SOURCE_CODE_FILE(".bas", false),

	BASIC_BYTE_CODE_FILE(".bin", true),

	JAVACPC_SNAPSHOT_FILE_UNCOMPRESSED(".sna", true),

	JAVACPC_SNAPSHOT_FILE_COMPRESSED(".snz", true),

	AMSTRAD_METADATA_FILE(".amd", false);

	private String fileExtension;

	private boolean binaryFileData;

	private AmstradFileType(String fileExtension, boolean binaryFileData) {
		this.fileExtension = fileExtension;
		this.binaryFileData = binaryFileData;
	}

	public static AmstradFileType guessFileType(File file) {
		for (AmstradFileType type : AmstradFileType.values()) {
			if (type.matches(file))
				return type;
		}
		return null;
	}

	public boolean matches(File file) {
		return file != null && file.isFile() && file.getName().toLowerCase().endsWith(getFileExtension());
	}

	public String getFileExtensionWithoutDot() {
		return getFileExtension().substring(1);
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public boolean isBinaryFileData() {
		return binaryFileData;
	}

}