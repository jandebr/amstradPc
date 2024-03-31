package org.maia.amstrad.program.load.basic.staged;

public class StagedCommandReference {

	private StagedCommand command;

	private int referenceNumber;

	public StagedCommandReference(StagedCommand command, int referenceNumber) {
		this.command = command;
		this.referenceNumber = referenceNumber;
	}

	public StagedCommand getCommand() {
		return command;
	}

	public int getReferenceNumber() {
		return referenceNumber;
	}

}