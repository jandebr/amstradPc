package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSyntaxException;

public class LocomotiveBasicSourceCodeLine extends BasicSourceCodeLine {

	public LocomotiveBasicSourceCodeLine(String text) throws BasicSyntaxException {
		super(text);
	}

	@Override
	public LocomotiveBasicSourceCodeLineScanner createScanner() {
		return new LocomotiveBasicSourceCodeLineScanner(getText(), LocomotiveBasicKeywords.getInstance());
	}

}