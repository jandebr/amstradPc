package org.maia.amstrad.load.basic.staged.file;

public abstract class FileCommand {

	private String filename;

	private boolean suppressMessages;

	protected FileCommand(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public boolean isSuppressMessages() {
		return suppressMessages;
	}

	public void setSuppressMessages(boolean suppressMessages) {
		this.suppressMessages = suppressMessages;
	}

}