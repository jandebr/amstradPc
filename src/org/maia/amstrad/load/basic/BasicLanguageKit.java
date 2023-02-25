package org.maia.amstrad.load.basic;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCodeLine;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;

public abstract class BasicLanguageKit {

	private static final BasicLanguage DEFAULT_LANGUAGE = BasicLanguage.LOCOMOTIVE_BASIC;

	private static final BasicLanguageKit locomotiveKit = new LocomotiveBasicLanguageKit();

	public static BasicLanguageKit forLanguage(BasicLanguage language) {
		BasicLanguageKit kit = null;
		if (BasicLanguage.LOCOMOTIVE_BASIC.equals(language)) {
			kit = locomotiveKit;
		}
		return kit;
	}

	public static BasicLanguage guessLanguageOfSourceCode(CharSequence code) {
		return DEFAULT_LANGUAGE;
	}

	public static BasicLanguage guessLanguageOfByteCode(byte[] code) {
		return DEFAULT_LANGUAGE;
	}

	private BasicLanguageKit() {
	}

	public abstract BasicSourceCode parseSourceCode(CharSequence code) throws BasicException;

	public abstract BasicByteCode parseByteCode(byte[] code) throws BasicException;

	public abstract BasicSourceCodeLine createSourceCodeLine(int lineNumber, String lineCode)
			throws BasicSyntaxException;

	public abstract BasicSourceCodeLine createSourceCodeLine(BasicSourceTokenSequence sequence)
			throws BasicSyntaxException;

	public abstract BasicSourceToken createInstructionSeparatorToken();

	public abstract BasicSourceToken createKeywordToken(String keyword) throws BasicSyntaxException;

	private static class LocomotiveBasicLanguageKit extends BasicLanguageKit {

		public LocomotiveBasicLanguageKit() {
		}

		@Override
		public BasicSourceCode parseSourceCode(CharSequence code) throws BasicException {
			return new LocomotiveBasicSourceCode(code);
		}

		@Override
		public BasicByteCode parseByteCode(byte[] code) {
			return new LocomotiveBasicByteCode(code);
		}

		@Override
		public BasicSourceCodeLine createSourceCodeLine(int lineNumber, String lineCode) throws BasicSyntaxException {
			return new LocomotiveBasicSourceCodeLine(lineNumber + " " + lineCode);
		}

		@Override
		public BasicSourceCodeLine createSourceCodeLine(BasicSourceTokenSequence sequence) throws BasicSyntaxException {
			return new LocomotiveBasicSourceCodeLine(sequence.getSourceCode());
		}

		@Override
		public BasicSourceToken createInstructionSeparatorToken() {
			return LocomotiveBasicSourceTokenFactory.getInstance().createInstructionSeparator();
		}

		@Override
		public BasicSourceToken createKeywordToken(String keyword) throws BasicSyntaxException {
			return LocomotiveBasicSourceTokenFactory.getInstance().createBasicKeyword(keyword);
		}

	}

}