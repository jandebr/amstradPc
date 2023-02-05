package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;

public class InterruptBasicPreprocessor extends StagedBasicPreprocessor {

	public InterruptBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 1; // for interrupt macro
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(InterruptMacro.class)) {
			addInterruptMacro(sourceCode, session);
		}
		repeatInterruptMacroAfterClear(sourceCode, session);
	}

	private void addInterruptMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireSmallestAvailablePreambleLineNumber();
		int lnGoto = session.getEndingMacroLineNumber();
		addCodeLine(sourceCode, ln, "ON ERROR GOTO " + lnGoto + ":ON BREAK GOSUB " + lnGoto
				+ (session.produceRemarks() ? ":REM @interrupt" : ""));
		session.addMacro(new InterruptMacro(new BasicLineNumberRange(ln)));
	}

	private void repeatInterruptMacroAfterClear(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		InterruptMacro iMacro = session.getMacroAdded(InterruptMacro.class);
		BasicSourceTokenSequence iSequence = sourceCode.getLineByLineNumber(iMacro.getLineNumberFrom()).parse();
		BasicLanguage language = sourceCode.getLanguage();
		int iRem = iSequence.getFirstIndexOf(createKeywordToken(language, "REM"));
		iSequence = iSequence.subSequence(1, iRem > 0 ? iRem - 1 : iSequence.size());
		BasicSourceToken CLEAR = createKeywordToken(language, "CLEAR");
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(CLEAR);
				while (i >= 0) {
					// CLEAR command => repeat interrupt
					sequence.insert(i + 1, new InstructionSeparatorToken());
					sequence.insert(i + 2, iSequence);
					i = sequence.getNextIndexOf(CLEAR, i + 2 + iSequence.size());
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	public static class InterruptMacro extends StagedBasicMacro {

		public InterruptMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

}