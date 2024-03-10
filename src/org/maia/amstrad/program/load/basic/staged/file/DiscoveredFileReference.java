package org.maia.amstrad.program.load.basic.staged.file;

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

		LOAD("LOAD", true),

		SAVE("SAVE", true),

		OPEN_IN("OPENIN", false),

		OPEN_OUT("OPENOUT", false),

		RUN("RUN", true),

		CHAIN("CHAIN", true),

		CHAIN_MERGE("CHAIN MERGE", true),

		MERGE("MERGE", true);

		private String sourceForm;

		private boolean programReference;

		private Instruction(String sourceForm, boolean programReference) {
			this.sourceForm = sourceForm;
			this.programReference = programReference;
		}

		public String getSourceForm() {
			return sourceForm;
		}

		public boolean isProgramReference() {
			return programReference;
		}

	}

}