package org.maia.amstrad.basic.locomotive.minify;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;

public class LocomotiveBasicRemarksMinifier extends LocomotiveBasicMinifier {

	public LocomotiveBasicRemarksMinifier() {
	}

	@Override
	public void minify(BasicSourceCode sourceCode) throws BasicException {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		BasicSourceToken REM = stf.createBasicKeyword("REM");
		BasicSourceToken REM_SHORTHAND = stf.createBasicKeyword("'");
		BasicSourceToken SEP = stf.createInstructionSeparator();
		for (BasicSourceCodeLine line : sourceCode) {
			BasicSourceTokenSequence sequence = line.parse();
			int n = sequence.size();
			int i = sequence.getFirstIndexOf(REM);
			if (i < 0)
				i = n;
			int j = sequence.getFirstIndexOf(REM_SHORTHAND);
			if (j < 0)
				j = n;
			int k = Math.min(i, j);
			if (k < n) {
				int s = sequence.getPreviousIndexOf(SEP, k - 1);
				if (s >= 0)
					k = s;
				s = sequence.getIndexPrecedingWhitespace(k - 1);
				if (s >= 0)
					k = s + 1;
				sequence.removeRange(k, n);
				if (sequence.size() == 1 && sequence.startsWithLineNumber()) {
					sequence.append(REM_SHORTHAND); // no empty numbered lines
				}
				updateLine(sourceCode, sequence);
			}
		}
	}

}