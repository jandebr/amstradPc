package org.maia.amstrad.program.loader.basic.staged;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;

public class DynamicLinkSetupBasicPreprocessor extends StagedBasicPreprocessor {

	public DynamicLinkSetupBasicPreprocessor() {
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		List<Integer> internalLineNumbers = sourceCode.getAscendingLineNumbers();
		markEndOfCode(sourceCode, session);
		setupExternalLinks(sourceCode, internalLineNumbers, session);
	}

	private void markEndOfCode(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = sourceCode.getNextAvailableLineNumber(sourceCode.getDominantLineNumberStep());
		addCodeLine(sourceCode, ln, "END");
	}

	private void setupExternalLinks(BasicSourceCode sourceCode, List<Integer> internalLineNumbers,
			StagedBasicProgramLoaderSession session) throws BasicException {
		Map<Integer, DynamicLinkMacro> externalLinkMap = new HashMap<Integer, DynamicLinkMacro>();
		BasicLanguage language = sourceCode.getLanguage();
		setupExternalLinks(sourceCode, createKeywordToken(language, "GOTO"), internalLineNumbers, externalLinkMap,
				session);
		setupExternalLinks(sourceCode, createKeywordToken(language, "GOSUB"), internalLineNumbers, externalLinkMap,
				session);
	}

	private void setupExternalLinks(BasicSourceCode sourceCode, BasicSourceToken lineReferenceCommand,
			List<Integer> internalLineNumbers, Map<Integer, DynamicLinkMacro> externalLinkMap,
			StagedBasicProgramLoaderSession session) throws BasicException {
		int lnStep = sourceCode.getDominantLineNumberStep();
		for (BasicSourceCodeLine line : sourceCode) {
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
								int lnMacro = sourceCode.getNextAvailableLineNumber(lnStep);
								macro = new DynamicLinkMacro(lnMacro);
								externalLinkMap.put(ln, macro);
								addCodeLine(sourceCode, lnMacro,
										"'GOTO " + ln + (session.produceRemarks() ? ":REM @link" : ""));
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

	public static class DynamicLinkMacro extends StagedBasicMacro {

		public DynamicLinkMacro(int lineNumber) {
			super(lineNumber, lineNumber);
		}

	}

}