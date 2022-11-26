package org.maia.amstrad.program;

public class AmstradProgramBinaryPayload extends AmstradProgramPayload {

	private byte[] payload;

	public AmstradProgramBinaryPayload(byte[] payload) {
		this.payload = payload;
	}

	@Override
	public boolean isText() {
		return false;
	}

	@Override
	protected AmstradProgramBinaryPayload toBinaryPayload() {
		return this;
	}

	@Override
	protected AmstradProgramTextPayload toTextPayload() {
		return null;
	}

	public byte[] getPayload() {
		return payload;
	}

}