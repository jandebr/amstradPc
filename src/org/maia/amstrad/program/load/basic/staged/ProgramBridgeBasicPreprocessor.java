package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;

public class ProgramBridgeBasicPreprocessor extends StagedBasicPreprocessor {

	public ProgramBridgeBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0;
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return false;
	}

	@Override
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		return Collections.emptyList();
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		addProgramBridgeMacro(sourceCode, session);
		addDynamicLinkMacros(sourceCode, session);
	}

	private void addProgramBridgeMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = getNextAvailableLineNumber(sourceCode);
		addCodeLine(sourceCode, ln, "GOTO " + ln + (session.produceRemarks() ? ":REM @bridge" : ""));
		session.addMacro(new ProgramBridgeMacro(new BasicLineNumberRange(ln)));
	}

	private void addDynamicLinkMacros(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		Map<Integer, DynamicLinkMacro> externalLinkMap = new HashMap<Integer, DynamicLinkMacro>();
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(LineNumberReferenceToken.class);
				while (i >= 0) {
					int ln = ((LineNumberReferenceToken) sequence.get(i)).getLineNumber();
					if (!scope.isInScope(ln)) {
						// External line number => Dynamic link macro
						DynamicLinkMacro macro = externalLinkMap.get(ln);
						if (macro == null) {
							int lnMacro = getNextAvailableLineNumber(sourceCode);
							macro = new DynamicLinkMacro(new BasicLineNumberRange(lnMacro), ln);
							externalLinkMap.put(ln, macro);
							addCodeLine(sourceCode, lnMacro, "GOTO 0" + (session.produceRemarks() ? ":REM @link" : ""));
							session.addMacro(macro);
						}
						sequence.replace(i, new LineNumberReferenceToken(macro.getLineNumberFrom()));
					}
					i = sequence.getNextIndexOf(LineNumberReferenceToken.class, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	public static class ProgramBridgeMacro extends StagedBasicMacro {

		public ProgramBridgeMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

	public static class DynamicLinkMacro extends StagedBasicMacro {

		private int originalLineNumber;

		public DynamicLinkMacro(BasicLineNumberRange range, int originalLineNumber) {
			super(range);
			this.originalLineNumber = originalLineNumber;
		}

		public int getOriginalLineNumber() {
			return originalLineNumber;
		}

	}

}