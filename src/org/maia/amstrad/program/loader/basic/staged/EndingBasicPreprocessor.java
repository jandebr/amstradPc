package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
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
		invokeEndingMacroFromCode(sourceCode, session.getMacroAdded(EndingMacro.class));
	}

	protected void addMacros(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		addEndingMacro(sourceCode, session);
		addInterruptMacro(sourceCode, session);
	}

	protected void addEndingMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addr = session.reserveMemoryTrapAddress();
		int lnStep = sourceCode.getDominantLineNumberStep();
		int lnStart = sourceCode.getNextAvailableLineNumber(lnStep);
		int ln = lnStart - lnStep;
		if (session.leaveRemarks()) {
			ln += lnStep;
			addCodeLine(sourceCode, ln, "REM @ending[");
		}
		ln += lnStep;
		addCodeLine(sourceCode, ln, "POKE &" + Integer.toHexString(addr) + ",1");
		ln += lnStep;
		addCodeLine(sourceCode, ln, "GOTO " + ln);
		if (session.leaveRemarks()) {
			ln += lnStep;
			addCodeLine(sourceCode, ln, "REM @ending]");
		}
		session.addMacro(new EndingMacro(lnStart, ln, addr));
	}

	protected void addInterruptMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireFirstAvailablePreambleLineNumber();
		int lnGoto = session.getMacroAdded(EndingMacro.class).getLineNumberStart();
		addCodeLine(sourceCode, ln, "ON ERROR GOTO " + lnGoto + (session.leaveRemarks() ? ":REM @interrupt" : ""));
	}

	protected void invokeEndingMacroFromCode(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
		invokeEndingMacroAtEndOfCode(sourceCode, macro);
		invokeEndingMacroFromEndCommands(sourceCode, macro);
		invokeEndingMacroFromGotoLoops(sourceCode, macro);
	}

	private void invokeEndingMacroAtEndOfCode(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
		int ln = sourceCode.getNextAvailableLineNumber(sourceCode.getDominantLineNumberStep());
		int lnGoto = macro.getLineNumberStart();
		addCodeLine(sourceCode, ln, "GOTO " + lnGoto);
	}

	private void invokeEndingMacroFromEndCommands(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
		invokeEndingMacroFromEndCommand(sourceCode, createKeywordToken("END"), macro);
		invokeEndingMacroFromEndCommand(sourceCode, createKeywordToken("STOP"), macro);
	}

	private void invokeEndingMacroFromEndCommand(BasicSourceCode sourceCode, BasicSourceToken command,
			EndingMacro macro) throws BasicException {
		int lnGoto = macro.getLineNumberStart();
		for (BasicSourceCodeLine line : sourceCode) {
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

	private void invokeEndingMacroFromGotoLoops(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
		int lnGoto = macro.getLineNumberStart();
		BasicSourceToken GOTO = createKeywordToken("GOTO");
		BasicSourceToken IF = createKeywordToken("IF");
		for (BasicSourceCodeLine line : sourceCode) {
			if (!macro.containsLine(line.getLineNumber())) {
				BasicSourceTokenSequence sequence = line.parse();
				if (!sequence.contains(IF)) {
					boolean lineEdited = false;
					int i = sequence.getFirstIndexOf(GOTO);
					while (i >= 0) {
						i = sequence.getIndexFollowingWhitespace(i + 1);
						if (i >= 0 && sequence.get(i) instanceof LineNumberReferenceToken) {
							int ln = ((LineNumberReferenceToken) sequence.get(i)).getLineNumber();
							if (ln == line.getLineNumber()) {
								// Goto loop => Goto macro
								sequence.replace(i, new LineNumberReferenceToken(lnGoto));
								lineEdited = true;
							}
							i = sequence.getNextIndexOf(GOTO, i + 1);
						}
					}
					if (lineEdited) {
						addCodeLine(sourceCode, sequence);
					}
				}
			}
		}
	}

	private void handleProgramEndedInSeparateThread(final StagedBasicProgramLoaderSession session) {
		runInSeparateThread(new Runnable() {

			@Override
			public void run() {
				handleProgramEnded(session);
			}
		});
	}

	protected void handleProgramEnded(StagedBasicProgramLoaderSession session) {
		discloseCode(session);
		clearScreen(session);
		performEndingAction(session);
	}

	protected void discloseCode(StagedBasicProgramLoaderSession session) {
		AmstradPc amstradPc = session.getAmstradPc();
		EndingBasicCodeDisclosure disclosure = session.getCodeDisclosure();
		if (EndingBasicCodeDisclosure.HIDE_CODE.equals(disclosure)) {
			amstradPc.getMonitor().freezeFrame();
			amstradPc.getBasicRuntime().waitUntilPromptInDirectModus();
			amstradPc.getKeyboard().enter("NEW");
		} else if (EndingBasicCodeDisclosure.ORIGINAL_CODE.equals(disclosure)) {
			BasicProgramLoader loader = AmstradProgramLoaderFactory.getInstance()
					.createOriginalBasicProgramLoader(amstradPc);
			amstradPc.getBasicRuntime().waitUntilPromptInDirectModus();
			try {
				loader.load(session.getProgram());
			} catch (AmstradProgramException e) {
				e.printStackTrace();
			}
		}
	}

	protected void clearScreen(StagedBasicProgramLoaderSession session) {
		AmstradPc amstradPc = session.getAmstradPc();
		amstradPc.getMonitor().freezeFrame();
		amstradPc.getBasicRuntime().waitUntilPromptInDirectModus();
		amstradPc.getKeyboard().enter("CLS");
		amstradPc.getBasicRuntime().waitUntilPromptInDirectModus();
		amstradPc.getMonitor().unfreezeFrame();
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

		public EndingMacro(int lineNumberStart, int lineNumberEnd, int memoryTrapAddress) {
			super(lineNumberStart, lineNumberEnd);
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
			AmstradPc amstradPc = getSession().getAmstradPc();
			amstradPc.getMonitor().freezeFrame();
			amstradPc.getKeyboard().breakEscape(); // to Basic direct modus
		}

	}

	private class EndingRuntimeListener extends StagedBasicProgramRuntimeListener {

		public EndingRuntimeListener(StagedBasicProgramLoaderSession session) {
			super(session);
		}

		@Override
		protected void stagedProgramIsRun() {
			addMemoryTrap(getMacro().getMemoryTrapAddress(), new EndingMacroHandler(getMacro(), getSession()));
		}

		@Override
		protected void stagedProgramIsDisposed(boolean programRemainsLoaded) {
			removeMemoryTrapsAt(getMacro().getMemoryTrapAddress());
			if (programRemainsLoaded) {
				// Break-Escape
				handleProgramEndedInSeparateThread(getSession());
			}
		}

		private EndingMacro getMacro() {
			return getSession().getMacroAdded(EndingMacro.class);
		}

	}

}