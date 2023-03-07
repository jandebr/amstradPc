package org.maia.amstrad.basic.locomotive.minify;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicMinifier;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCodeLine;

public abstract class LocomotiveBasicMinifier implements BasicMinifier {

	protected LocomotiveBasicMinifier() {
	}

	protected void updateLine(BasicSourceCode sourceCode, BasicSourceTokenSequence sequence) throws BasicException {
		if (sequence.isModified()) {
			sourceCode.addLine(new LocomotiveBasicSourceCodeLine(sequence.getSourceCode()));
		}
	}

}