package org.maia.amstrad.basic.locomotive.minify;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;
import org.maia.amstrad.basic.locomotive.token.LiteralToken;
import org.maia.amstrad.basic.locomotive.token.OperatorToken;

public class LocomotiveBasicWhitespaceMinifier extends LocomotiveBasicMinifier {

	public LocomotiveBasicWhitespaceMinifier() {
	}

	@Override
	public void minify(BasicSourceCode sourceCode) throws BasicException {
		for (BasicSourceCodeLine line : sourceCode) {
			BasicSourceTokenSequence sequence = line.parse();
			int i = 0;
			int j = -1; // index of previous, non-blank token
			while (i < sequence.size()) {
				BasicSourceToken token = sequence.get(i);
				if (!token.isBlank()) {
					if (canTrimWhitespaceOnEitherSide(token)) {
						int k = sequence.getIndexFollowingWhitespace(i + 1);
						if (k < 0)
							k = sequence.size();
						sequence.replaceRange(j + 1, k, token);
						i = j + 1;
					}
					j = i;
				}
				i++;
			}
			// trim at start of line
			if (sequence.startsWithLineNumber()) {
				i = sequence.getIndexFollowingWhitespace(1);
				sequence.removeRange(1, i);
			}
			// trim at end of line
			i = sequence.getIndexPrecedingWhitespace(sequence.size() - 1);
			sequence.removeRange(i + 1, sequence.size());
			updateLine(sourceCode, sequence);
		}
	}

	private boolean canTrimWhitespaceOnEitherSide(BasicSourceToken token) {
		if (token instanceof InstructionSeparatorToken) {
			return true;
		} else if (token instanceof OperatorToken) {
			return !((OperatorToken) token).getOperator().isAlphabetic();
		} else if (token instanceof LiteralToken) { // not quoted literal
			String str = token.getSourceFragment();
			if (str.length() == 1) {
				char c = str.charAt(0);
				return c == ',' || c == ';';
			}
		}
		return false;
	}

}