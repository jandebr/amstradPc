package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.basic.locomotive.token.LiteralToken;
import org.maia.amstrad.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.load.basic.BasicProgramLoader;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;

public class EndingBasicPreprocessor extends StagedBasicPreprocessor {

	public EndingBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 1; // for interrupt macro
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(EndingMacro.class)) {
			addEndingMacro(sourceCode, session);
			addInterruptMacro(sourceCode, session);
		}
		invokeMacrosFromCode(sourceCode, session);
	}

	private void addEndingMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrTrap = session.reserveMemory(1);
		int ln = getNextAvailableLineNumber(sourceCode);
		addCodeLine(sourceCode, ln,
				"POKE &" + Integer.toHexString(addrTrap) + ",1:END" + (session.produceRemarks() ? ":REM @ending" : ""));
		session.addMacro(new EndingMacro(ln));
		session.getProgramRuntime().addListener(new EndingRuntimeListener(session, addrTrap));
	}

	private void addInterruptMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireFirstAvailablePreambleLineNumber();
		int lnGoto = session.getEndingMacroLineNumber();
		addCodeLine(sourceCode, ln, "ON ERROR GOTO " + lnGoto + ":ON BREAK GOSUB " + lnGoto
				+ (session.produceRemarks() ? ":REM @interrupt" : ""));
		session.addMacro(new InterruptMacro(ln));
	}

	protected void invokeMacrosFromCode(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		BasicLineNumberScope scope = session.getScopeExcludingMacros();
		invokeEndingMacroOnBreak(sourceCode, scope, session);
		invokeEndingMacroOnEndCommands(sourceCode, scope, session);
		invokeEndingMacroOnGotoLoops(sourceCode, scope, session);
		repeatInterruptMacroAfterClear(sourceCode, scope, session);
	}

	private void invokeEndingMacroOnBreak(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			StagedBasicProgramLoaderSession session) throws BasicException {
		int lnGoto = session.getEndingMacroLineNumber();
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken ON_BREAK = createKeywordToken(language, "ON BREAK");
		BasicSourceToken STOP = createKeywordToken(language, "STOP");
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(ON_BREAK);
				while (i >= 0) {
					i = sequence.getIndexFollowingWhitespace(i + 1);
					if (i >= 0) {
						if (sequence.get(i).equals(STOP)) {
							// ON BREAK STOP => ON BREAK GOSUB ending macro
							sequence.replace(i, createKeywordToken(language, "GOSUB"), new LiteralToken(" "),
									new LineNumberReferenceToken(lnGoto));
						}
						i = sequence.getNextIndexOf(ON_BREAK, i);
					}
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	private void invokeEndingMacroOnEndCommands(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicLanguage language = sourceCode.getLanguage();
		invokeEndingMacroOnEndCommand(sourceCode, scope, createKeywordToken(language, "END"), session);
		invokeEndingMacroOnEndCommand(sourceCode, scope, createKeywordToken(language, "STOP"), session);
	}

	private void invokeEndingMacroOnEndCommand(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			BasicSourceToken command, StagedBasicProgramLoaderSession session) throws BasicException {
		int lnGoto = session.getEndingMacroLineNumber();
		BasicLanguage language = sourceCode.getLanguage();
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(command);
				while (i >= 0) {
					// End command => Goto ending macro
					sequence.replace(i, createKeywordToken(language, "GOTO"), new LiteralToken(" "),
							new LineNumberReferenceToken(lnGoto));
					i = sequence.getNextIndexOf(command, i + 3);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	private void invokeEndingMacroOnGotoLoops(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			StagedBasicProgramLoaderSession session) throws BasicException {
		int lnGoto = session.getEndingMacroLineNumber();
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken GOTO = createKeywordToken(language, "GOTO");
		BasicSourceToken IF = createKeywordToken(language, "IF");
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				if (!sequence.contains(IF)) {
					int i = sequence.getFirstIndexOf(GOTO);
					while (i >= 0) {
						i = sequence.getIndexFollowingWhitespace(i + 1);
						if (i >= 0) {
							if (sequence.get(i) instanceof LineNumberReferenceToken) {
								int ln = ((LineNumberReferenceToken) sequence.get(i)).getLineNumber();
								if (ln == line.getLineNumber()) {
									// Goto loop => Goto ending macro
									sequence.replace(i, new LineNumberReferenceToken(lnGoto));
								}
							}
							i = sequence.getNextIndexOf(GOTO, i);
						}
					}
					if (sequence.isModified()) {
						addCodeLine(sourceCode, sequence);
					}
				}
			}
		}
	}

	private void repeatInterruptMacroAfterClear(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			StagedBasicProgramLoaderSession session) throws BasicException {
		InterruptMacro iMacro = session.getMacroAdded(InterruptMacro.class);
		BasicSourceTokenSequence iSequence = sourceCode.getLineByLineNumber(iMacro.getLineNumberStart()).parse();
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

	protected void handleProgramEnded(StagedBasicProgramLoaderSession session) {
		handleCodeDisclosure(session);
		performEndingAction(session);
	}

	protected void handleCodeDisclosure(StagedBasicProgramLoaderSession session) {
		try {
			EndingBasicCodeDisclosure disclosure = session.getCodeDisclosure();
			if (EndingBasicCodeDisclosure.HIDE_CODE.equals(disclosure)) {
				session.getBasicRuntime().renew();
			} else if (EndingBasicCodeDisclosure.ORIGINAL_CODE.equals(disclosure)) {
				BasicProgramLoader loader = AmstradProgramLoaderFactory.getInstance()
						.createOriginalBasicProgramLoader(session.getAmstradPc());
				loader.load(session.getProgram());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void performEndingAction(StagedBasicProgramLoaderSession session) {
		EndingBasicAction endingAction = session.getEndingAction();
		if (endingAction != null) {
			endingAction.perform(session.getProgramRuntime());
		}
	}

	public static class EndingMacro extends StagedBasicMacro {

		public EndingMacro(int lineNumber) {
			super(lineNumber);
		}

	}

	public static class InterruptMacro extends StagedBasicMacro {

		public InterruptMacro(int lineNumber) {
			super(lineNumber);
		}

	}

	private class EndingRuntimeListener extends StagedBasicProgramRuntimeListener {

		private int memoryTrapAddress;

		public EndingRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
			super(session);
			this.memoryTrapAddress = memoryTrapAddress;
		}

		@Override
		protected void stagedProgramIsRun() {
			EndingMacro macro = getSession().getEndingMacro();
			addMemoryTrap(getMemoryTrapAddress(), new EndingMacroHandler(macro, getSession()));
		}

		@Override
		protected void stagedProgramIsDisposed(boolean programRemainsLoaded) {
			removeMemoryTrapsAt(getMemoryTrapAddress());
		}

		private int getMemoryTrapAddress() {
			return memoryTrapAddress;
		}

	}

	private class EndingMacroHandler extends StagedBasicMacroHandler implements AmstradMemoryTrapHandler {

		public EndingMacroHandler(EndingMacro macro, StagedBasicProgramLoaderSession session) {
			super(macro, session);
		}

		@Override
		public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
			getSession().getBasicRuntime().waitUntilReady();
			getSession().getProgramRuntime().dispose(true);
			handleProgramEnded(getSession());
		}

	}

}