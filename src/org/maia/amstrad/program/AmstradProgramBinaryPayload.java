package org.maia.amstrad.program;

public class AmstradProgramBinaryPayload extends AmstradProgramPayload {

	private byte[] bytes;

	public AmstradProgramBinaryPayload(byte[] bytes) {
		this.bytes = bytes;
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

	public byte[] getBytes() {
		return bytes;
	}

}