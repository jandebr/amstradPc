package org.maia.amstrad.basic.locomotive.source;

public class InstructionSeparatorToken extends SourceToken {

	public static final char SEPARATOR = ':';

	public InstructionSeparatorToken() {
		super(String.valueOf(SEPARATOR));
	}
	
	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitInstructionSeparator(this);
	}

}