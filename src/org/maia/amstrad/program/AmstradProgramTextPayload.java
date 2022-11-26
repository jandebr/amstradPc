package org.maia.amstrad.program;

public class AmstradProgramTextPayload extends AmstradProgramPayload {

	private CharSequence payload;

	public AmstradProgramTextPayload(CharSequence payload) {
		this.payload = payload;
	}

	@Override
	public boolean isText() {
		return true;
	}

	@Override
	protected AmstradProgramBinaryPayload toBinaryPayload() {
		return null;
	}

	@Override
	protected AmstradProgramTextPayload toTextPayload() {
		return this;
	}

	public CharSequence getPayload() {
		return payload;
	}

}