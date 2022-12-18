package org.maia.amstrad.program;

public class AmstradProgramTextPayload extends AmstradProgramPayload {

	private CharSequence text;

	public AmstradProgramTextPayload(CharSequence text) {
		this.text = text;
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

	public CharSequence getText() {
		return text;
	}

}