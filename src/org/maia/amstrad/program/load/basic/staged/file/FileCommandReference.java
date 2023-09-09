package org.maia.amstrad.program.load.basic.staged.file;

public class FileCommandReference {

	private FileCommand command;

	private int referenceNumber;

	public FileCommandReference(FileCommand command, int referenceNumber) {
		this.command = command;
		this.referenceNumber = referenceNumber;
	}

	public FileCommand getCommand() {
		return command;
	}

	public int getReferenceNumber() {
		return referenceNumber;
	}

}