package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicSourceToken;

public abstract class LocomotiveBasicSourceToken extends BasicSourceToken {

	protected LocomotiveBasicSourceToken(String sourceFragment) {
		super(sourceFragment);
	}

	public abstract void invite(LocomotiveBasicSourceTokenVisitor visitor);

}