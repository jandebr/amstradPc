package org.maia.amstrad.program;

public abstract class AmstradProgramPayload {

	protected AmstradProgramPayload() {
	}

	public final boolean isBinary() {
		return !isText();
	}

	public abstract boolean isText();

	public final AmstradProgramBinaryPayload asBinaryPayload() {
		if (!isBinary())
			throw new UnsupportedOperationException("This is not a binary payload");
		else
			return toBinaryPayload();
	}

	public final AmstradProgramTextPayload asTextPayload() {
		if (!isText())
			throw new UnsupportedOperationException("This is not a text payload");
		else
			return toTextPayload();
	}

	protected abstract AmstradProgramBinaryPayload toBinaryPayload();

	protected abstract AmstradProgramTextPayload toTextPayload();

}