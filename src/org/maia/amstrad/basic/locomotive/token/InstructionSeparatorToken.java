package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceToken;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class InstructionSeparatorToken extends LocomotiveBasicSourceToken {

	public static final char SEPARATOR = ':';

	public InstructionSeparatorToken() {
		super(String.valueOf(SEPARATOR));
	}
	
	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitInstructionSeparator(this);
	}

}