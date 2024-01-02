package org.maia.amstrad.program;

import org.maia.amstrad.AmstradException;

public class AmstradProgramException extends AmstradException {

	private AmstradProgram program;

	public AmstradProgramException(AmstradProgram program, String message) {
		this(program, message, null);
	}

	public AmstradProgramException(AmstradProgram program, Throwable cause) {
		this(program, null, cause);
	}

	public AmstradProgramException(AmstradProgram program, String message, Throwable cause) {
		super(message, cause);
		this.program = program;
	}

	public AmstradProgram getProgram() {
		return program;
	}

}