package org.maia.amstrad.program.payload;

import org.maia.amstrad.program.AmstradProgramException;

public abstract class AmstradProgramTextPayload extends AmstradProgramPayload {

	protected AmstradProgramTextPayload() {
	}

	@Override
	public final boolean isText() {
		return true;
	}

	@Override
	protected final AmstradProgramBinaryPayload toBinaryPayload() {
		return null;
	}

	@Override
	protected final AmstradProgramTextPayload toTextPayload() {
		return this;
	}

	public abstract CharSequence getText() throws AmstradProgramException;

}