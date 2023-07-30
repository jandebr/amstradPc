package org.maia.amstrad.program.payload;

import org.maia.amstrad.program.AmstradProgramException;

public abstract class AmstradProgramBinaryPayload extends AmstradProgramPayload {

	protected AmstradProgramBinaryPayload() {
	}

	@Override
	public final boolean isText() {
		return false;
	}

	@Override
	protected final AmstradProgramBinaryPayload toBinaryPayload() {
		return this;
	}

	@Override
	protected final AmstradProgramTextPayload toTextPayload() {
		return null;
	}

	public abstract byte[] getBytes() throws AmstradProgramException;

}