package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.basic.locomotive.token.LiteralToken;
import org.maia.amstrad.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.load.basic.BasicProgramLoader;
import org.maia.amstrad.load.basic.staged.ProgramBridgeBasicPreprocessor.ProgramBridgeMacro;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.program.AmstradProgramException;

public class EndingBasicPreprocessor extends StagedBasicPreprocessor {

	public EndingBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 1; // for ending macro
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(EndingMacro.class)) {
			addEndingMacro(sourceCode, session);
		}
		invokeMacrosFromCode(sourceCode, session);
	}

	private void addEndingMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrTrap = session.reserveMemory(1);
		int ln = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln,
				"POKE &" + Integer.toHexString(addrTrap) + ",1:END" + (session.produceRemarks() ? ":REM @end" : ""));
		session.addMacro(new EndingMacro(new BasicLineNumberRange(ln)));
		// Install global macro handler via listener
		EndingRuntimeListener listener = new EndingRuntimeListener(session, addrTrap);
		listener.install();
	}

	protected void invokeMacrosFromCode(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		invokeEndingMacroOnBreak(sourceCode, scope, session);
		invokeEndingMacroOnEndCommands(sourceCode, scope, session);
		invokeEndingMacroOnGotoLoops(sourceCode, scope, session);
		invokeEndingMacroAtCodeHorizon(sourceCode, session);
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

	private void invokeEndingMacroAtCodeHorizon(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.getMacroAdded(ProgramBridgeMacro.class).getLineNumberFrom();
		int lnGoto = session.getEndingMacroLineNumber();
		substituteGotoLineNumber(ln, lnGoto, sourceCode, session);
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
		} catch (BasicException | AmstradProgramException e) {
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

		public EndingMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

	private class EndingRuntimeListener extends StagedBasicProgramRuntimeListener {

		public EndingRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
			super(session, memoryTrapAddress);
		}

		@Override
		protected AmstradMemoryTrapHandler createMemoryTrapHandler() {
			EndingMacro macro = getSession().getEndingMacro();
			return new EndingMacroHandler(macro, getSession());
		}

	}

	private class EndingMacroHandler extends StagedBasicMacroHandler {

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