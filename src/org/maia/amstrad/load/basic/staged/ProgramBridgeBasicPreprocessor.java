package org.maia.amstrad.load.basic.staged;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;

public class ProgramBridgeBasicPreprocessor extends StagedBasicPreprocessor {

	public ProgramBridgeBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 0;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		List<Integer> internalLineNumbers = sourceCode.getAscendingLineNumbers();
		addProgramBridgeMacro(sourceCode, session);
		addDynamicLinkMacros(sourceCode, internalLineNumbers, session);
	}

	private void addProgramBridgeMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = getNextAvailableLineNumber(sourceCode);
		// Goto line number will be replaced by EndingBasicPreprocessor
		addCodeLine(sourceCode, ln, "GOTO 0" + (session.produceRemarks() ? ":REM @bridge" : ""));
		session.addMacro(new ProgramBridgeMacro(ln));
	}

	private void addDynamicLinkMacros(BasicSourceCode sourceCode, List<Integer> internalLineNumbers,
			StagedBasicProgramLoaderSession session) throws BasicException {
		Map<Integer, DynamicLinkMacro> externalLinkMap = new HashMap<Integer, DynamicLinkMacro>();
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken GOTO = createKeywordToken(language, "GOTO");
		BasicSourceToken GOSUB = createKeywordToken(language, "GOSUB");
		addDynamicLinkMacros(sourceCode, GOTO, internalLineNumbers, externalLinkMap, session);
		addDynamicLinkMacros(sourceCode, GOSUB, internalLineNumbers, externalLinkMap, session);
	}

	private void addDynamicLinkMacros(BasicSourceCode sourceCode, BasicSourceToken lineReferenceCommand,
			List<Integer> internalLineNumbers, Map<Integer, DynamicLinkMacro> externalLinkMap,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicLineNumberScope scope = session.getScopeExcludingMacros();
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(lineReferenceCommand);
				while (i >= 0) {
					i = sequence.getIndexFollowingWhitespace(i + 1);
					if (i >= 0) {
						if (sequence.get(i) instanceof LineNumberReferenceToken) {
							int ln = ((LineNumberReferenceToken) sequence.get(i)).getLineNumber();
							if (!internalLineNumbers.contains(ln)) {
								// External line number => Dynamic link macro
								DynamicLinkMacro macro = externalLinkMap.get(ln);
								if (macro == null) {
									int lnMacro = getNextAvailableLineNumber(sourceCode);
									macro = new DynamicLinkMacro(lnMacro, ln);
									externalLinkMap.put(ln, macro);
									addCodeLine(sourceCode, lnMacro,
											"GOTO 0" + (session.produceRemarks() ? ":REM @link" : ""));
									session.addMacro(macro);
								}
								sequence.replace(i, new LineNumberReferenceToken(macro.getLineNumberStart()));
							}
						}
						i = sequence.getNextIndexOf(lineReferenceCommand, i);
					}
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	public static class ProgramBridgeMacro extends StagedBasicMacro {

		public ProgramBridgeMacro(int lineNumber) {
			super(lineNumber);
		}

	}

	public static class DynamicLinkMacro extends StagedBasicMacro {

		private int originalLineNumber;

		public DynamicLinkMacro(int lineNumber, int originalLineNumber) {
			super(lineNumber);
			this.originalLineNumber = originalLineNumber;
		}

		public int getOriginalLineNumber() {
			return originalLineNumber;
		}

	}

}