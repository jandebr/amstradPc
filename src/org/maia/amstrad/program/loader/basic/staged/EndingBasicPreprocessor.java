package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.basic.locomotive.token.LiteralToken;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.loader.AmstradProgramLoaderFactory;
import org.maia.amstrad.program.loader.basic.BasicProgramLoader;

public class EndingBasicPreprocessor extends StagedBasicPreprocessor {

	public EndingBasicPreprocessor() {
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(EndingMacro.class)) {
			addMacros(sourceCode, session);
			session.getProgramRuntime().addListener(new EndingRuntimeListener(session));
		}
		invokeMacrosFromCode(sourceCode, session);
	}

	protected void addMacros(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		addEndingMacro(sourceCode, session);
		addInterruptMacro(sourceCode, session);
	}

	protected void addEndingMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addr = session.reserveMemoryTrapAddress();
		int ln = sourceCode.getNextAvailableLineNumber(sourceCode.getDominantLineNumberStep());
		addCodeLine(sourceCode, ln,
				"POKE &" + Integer.toHexString(addr) + ",1:END" + (session.leaveRemarks() ? ":REM @ending" : ""));
		session.addMacro(new EndingMacro(ln, addr));
	}

	protected void addInterruptMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireFirstAvailablePreambleLineNumber();
		int lnGoto = session.getMacroAdded(EndingMacro.class).getLineNumberStart();
		addCodeLine(sourceCode, ln, "ON ERROR GOTO " + lnGoto + ":ON BREAK GOSUB " + lnGoto
				+ (session.leaveRemarks() ? ":REM @interrupt" : ""));
		session.addMacro(new InterruptMacro(ln));
	}

	protected void invokeMacrosFromCode(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		BasicLineNumberScope scope = session.getScopeExcludingMacros();
		EndingMacro emacro = session.getMacroAdded(EndingMacro.class);
		invokeEndingMacroOnBreak(sourceCode, scope, emacro);
		invokeEndingMacroOnEndCommands(sourceCode, scope, emacro);
		invokeEndingMacroOnGotoLoops(sourceCode, scope, emacro);
		invokeEndingMacroAtEndOfCode(sourceCode, emacro);
		InterruptMacro imacro = session.getMacroAdded(InterruptMacro.class);
		repeatInterruptMacroAfterClear(sourceCode, scope, imacro);
	}

	private void invokeEndingMacroOnBreak(BasicSourceCode sourceCode, BasicLineNumberScope scope, EndingMacro macro)
			throws BasicException {
		int lnGoto = macro.getLineNumberStart();
		BasicSourceToken ON_BREAK = createKeywordToken("ON BREAK");
		BasicSourceToken STOP = createKeywordToken("STOP");
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line.getLineNumber())) {
				BasicSourceTokenSequence sequence = line.parse();
				boolean lineEdited = false;
				int i = sequence.getFirstIndexOf(ON_BREAK);
				while (i >= 0) {
					i = sequence.getIndexFollowingWhitespace(i + 1);
					if (i >= 0) {
						if (sequence.get(i).equals(STOP)) {
							// ON BREAK STOP => ON BREAK GOSUB macro
							sequence.replace(i, createKeywordToken("GOSUB"), new LiteralToken(" "),
									new LineNumberReferenceToken(lnGoto));
							lineEdited = true;
						}
						i = sequence.getNextIndexOf(ON_BREAK, i);
					}
				}
				if (lineEdited) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	private void invokeEndingMacroOnEndCommands(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			EndingMacro macro) throws BasicException {
		invokeEndingMacroOnEndCommand(sourceCode, scope, createKeywordToken("END"), macro);
		invokeEndingMacroOnEndCommand(sourceCode, scope, createKeywordToken("STOP"), macro);
	}

	private void invokeEndingMacroOnEndCommand(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			BasicSourceToken command, EndingMacro macro) throws BasicException {
		int lnGoto = macro.getLineNumberStart();
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line.getLineNumber())) {
				BasicSourceTokenSequence sequence = line.parse();
				boolean lineEdited = false;
				int i = sequence.getFirstIndexOf(command);
				while (i >= 0) {
					// End command => Goto macro
					sequence.replace(i, createKeywordToken("GOTO"), new LiteralToken(" "),
							new LineNumberReferenceToken(lnGoto));
					lineEdited = true;
					i = sequence.getNextIndexOf(command, i + 3);
				}
				if (lineEdited) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	private void invokeEndingMacroOnGotoLoops(BasicSourceCode sourceCode, BasicLineNumberScope scope, EndingMacro macro)
			throws BasicException {
		int lnGoto = macro.getLineNumberStart();
		BasicSourceToken GOTO = createKeywordToken("GOTO");
		BasicSourceToken IF = createKeywordToken("IF");
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line.getLineNumber())) {
				BasicSourceTokenSequence sequence = line.parse();
				if (!sequence.contains(IF)) {
					boolean lineEdited = false;
					int i = sequence.getFirstIndexOf(GOTO);
					while (i >= 0) {
						i = sequence.getIndexFollowingWhitespace(i + 1);
						if (i >= 0) {
							if (sequence.get(i) instanceof LineNumberReferenceToken) {
								int ln = ((LineNumberReferenceToken) sequence.get(i)).getLineNumber();
								if (ln == line.getLineNumber()) {
									// Goto loop => Goto macro
									sequence.replace(i, new LineNumberReferenceToken(lnGoto));
									lineEdited = true;
								}
							}
							i = sequence.getNextIndexOf(GOTO, i);
						}
					}
					if (lineEdited) {
						addCodeLine(sourceCode, sequence);
					}
				}
			}
		}
	}

	private void invokeEndingMacroAtEndOfCode(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
		int ln = sourceCode.getNextAvailableLineNumber(sourceCode.getDominantLineNumberStep());
		int lnGoto = macro.getLineNumberStart();
		addCodeLine(sourceCode, ln, "GOTO " + lnGoto);
	}

	private void repeatInterruptMacroAfterClear(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			InterruptMacro macro) throws BasicException {
		BasicSourceTokenSequence iSequence = sourceCode.getLineByLineNumber(macro.getLineNumberStart()).parse();
		int iRem = iSequence.getFirstIndexOf(createKeywordToken("REM"));
		iSequence = iSequence.subSequence(1, iRem > 0 ? iRem - 1 : iSequence.size());
		BasicSourceToken CLEAR = createKeywordToken("CLEAR");
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line.getLineNumber())) {
				BasicSourceTokenSequence sequence = line.parse();
				boolean lineEdited = false;
				int i = sequence.getFirstIndexOf(CLEAR);
				while (i >= 0) {
					// CLEAR command => repeat interrupt
					sequence.insert(i + 1, new InstructionSeparatorToken());
					sequence.insert(i + 2, iSequence);
					lineEdited = true;
					i = sequence.getNextIndexOf(CLEAR, i + 2 + iSequence.size());
				}
				if (lineEdited) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	protected void handleProgramEnded(StagedBasicProgramLoaderSession session) {
		discloseCode(session);
		performEndingAction(session);
	}

	protected void discloseCode(StagedBasicProgramLoaderSession session) {
		AmstradPc amstradPc = session.getAmstradPc();
		EndingBasicCodeDisclosure disclosure = session.getCodeDisclosure();
		if (EndingBasicCodeDisclosure.HIDE_CODE.equals(disclosure)) {
			amstradPc.getMonitor().freezeFrame();
			amstradPc.getKeyboard().exec("NEW");
			amstradPc.getKeyboard().exec("CLS");
			amstradPc.getMonitor().unfreezeFrame();
		} else if (EndingBasicCodeDisclosure.ORIGINAL_CODE.equals(disclosure)) {
			BasicProgramLoader loader = AmstradProgramLoaderFactory.getInstance()
					.createOriginalBasicProgramLoader(amstradPc);
			try {
				loader.load(session.getProgram());
			} catch (AmstradProgramException e) {
				e.printStackTrace();
			}
		}
	}

	protected void performEndingAction(StagedBasicProgramLoaderSession session) {
		EndingBasicAction endingAction = session.getEndingAction();
		if (endingAction != null) {
			endingAction.perform(session.getProgramRuntime());
		}
	}

	public static class InterruptMacro extends StagedBasicMacro {

		public InterruptMacro(int lineNumber) {
			super(lineNumber, lineNumber);
		}

	}

	public static class EndingMacro extends StagedBasicMacro {

		private int memoryTrapAddress;

		public EndingMacro(int lineNumber, int memoryTrapAddress) {
			super(lineNumber, lineNumber);
			this.memoryTrapAddress = memoryTrapAddress;
		}

		public int getMemoryTrapAddress() {
			return memoryTrapAddress;
		}

	}

	private class EndingMacroHandler extends StagedBasicMacroHandler implements AmstradMemoryTrapHandler {

		public EndingMacroHandler(EndingMacro macro, StagedBasicProgramLoaderSession session) {
			super(macro, session);
		}

		@Override
		public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
			getSession().getAmstradPc().getBasicRuntime().waitUntilPromptInDirectModus();
			getSession().getProgramRuntime().dispose(true);
			handleProgramEnded(getSession());
		}

	}

	private class EndingRuntimeListener extends StagedBasicProgramRuntimeListener {

		public EndingRuntimeListener(StagedBasicProgramLoaderSession session) {
			super(session);
		}

		@Override
		protected void stagedProgramIsRun() {
			EndingMacro macro = getEndingMacro();
			addMemoryTrap(macro.getMemoryTrapAddress(), new EndingMacroHandler(macro, getSession()));
		}

		@Override
		protected void stagedProgramIsDisposed(boolean programRemainsLoaded) {
			removeMemoryTrapsAt(getEndingMacro().getMemoryTrapAddress());
		}

		private EndingMacro getEndingMacro() {
			return getSession().getMacroAdded(EndingMacro.class);
		}

	}

}