package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicSourceCode;

public class DiscoveredFileReference {

	private BasicSourceCode sourceCode;

	private int lineNumber;

	private String sourceFilename;

	private Instruction instruction;

	public DiscoveredFileReference(BasicSourceCode sourceCode, int lineNumber, String sourceFilename,
			Instruction instruction) {
		this.sourceCode = sourceCode;
		this.lineNumber = lineNumber;
		this.sourceFilename = sourceFilename;
		this.instruction = instruction;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DiscoveredFileReference [lineNumber=");
		builder.append(getLineNumber());
		builder.append(", fileName=");
		builder.append(getSourceFilenameWithoutFlags());
		builder.append(", instruction=");
		builder.append(getInstruction().getSourceForm());
		builder.append("]");
		return builder.toString();
	}

	public BasicSourceCode getSourceCode() {
		return sourceCode;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getSourceFilename() {
		return sourceFilename;
	}

	public String getSourceFilenameWithoutFlags() {
		String fn = getSourceFilename();
		if (fn.startsWith(FileCommand.FILENAME_FLAG_SUPPRESS_MESSAGES)) {
			fn = fn.substring(FileCommand.FILENAME_FLAG_SUPPRESS_MESSAGES.length());
		}
		return fn;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public static enum Instruction {

		LOAD("LOAD"),

		SAVE("SAVE"),

		OPEN_IN("OPENIN"),

		OPEN_OUT("OPENOUT"),

		RUN("RUN"),

		CHAIN("CHAIN"),

		CHAIN_MERGE("CHAIN MERGE");

		private String sourceForm;

		private Instruction(String sourceForm) {
			this.sourceForm = sourceForm;
		}

		public String getSourceForm() {
			return sourceForm;
		}

	}

}