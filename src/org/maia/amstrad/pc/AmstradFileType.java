package org.maia.amstrad.pc;

import java.io.File;

public enum AmstradFileType {

	BASIC_SOURCE_CODE_FILE(".bas"),

	BASIC_BYTE_CODE_FILE(".bin"),

	JAVACPC_SNAPSHOT_FILE_UNCOMPRESSED(".sna"),

	JAVACPC_SNAPSHOT_FILE_COMPRESSED(".snz"),

	AMSTRAD_METADATA_FILE(".amd");

	private String fileExtension;

	private AmstradFileType(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public static boolean isRemasteredBasicSourceCodeFile(File file) {
		return BASIC_SOURCE_CODE_FILE.matches(file) && file.getName().toLowerCase().contains("remastered");
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

}