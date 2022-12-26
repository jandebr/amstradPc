package org.maia.amstrad.basic.locomotive;

public abstract class LocomotiveBasicProcessor {

	protected LocomotiveBasicProcessor() {
	}

	protected LocomotiveBasicKeywords getBasicKeywords() {
		return LocomotiveBasicKeywords.getInstance();
	}

}