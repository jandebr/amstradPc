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
		if (!session.isMacroAdded(EndingMacro.class)) {
			addMacro(sourceCode, session);
			session.getProgramRuntime().addListener(new EndingRuntimeListener(session));
		}
		invokeMacroFromCode(sourceCode, session.getMacroAdded(EndingMacro.class));
	}

	protected void addMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		int lnStep = sourceCode.getDominantLineNumberStep();
		int ln = sourceCode.getNextAvailableLineNumber(lnStep);
		int addr = session.reserveMemoryTrapAddress();
		addCodeLine(sourceCode, ln, "REM @Ending[");
		addCodeLine(sourceCode, ln + 1 * lnStep, "POKE &" + Integer.toHexString(addr) + ",1");
		addCodeLine(sourceCode, ln + 2 * lnStep, "GOTO " + (ln + 2 * lnStep));
		addCodeLine(sourceCode, ln + 3 * lnStep, "REM @Ending]");
		session.addMacro(new EndingMacro(ln, ln + 3 * lnStep, addr));
	}

	protected void invokeMacroFromCode(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
		invokeMacroAtEndOfCode(sourceCode, macro);
		invokeMacroFromEndCommands(sourceCode, macro);
		invokeMacroFromGotoLoops(sourceCode, macro);
	}

	private void invokeMacroAtEndOfCode(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
		int ln = sourceCode.getNextAvailableLineNumber(sourceCode.getDominantLineNumberStep());
		addCodeLine(sourceCode, ln, "GOTO " + macro.getLineNumberStart());
	}

	private void invokeMacroFromEndCommands(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
		invokeMacroFromEndCommand(sourceCode, createKeywordToken("END"), macro);
		invokeMacroFromEndCommand(sourceCode, createKeywordToken("STOP"), macro);
	}

	private void invokeMacroFromEndCommand(BasicSourceCode sourceCode, BasicSourceToken command, EndingMacro macro)
			throws BasicException {
		for (BasicSourceCodeLine line : sourceCode) {
			BasicSourceTokenSequence sequence = line.parse();
			boolean lineEdited = false;
			int i = sequence.getFirstIndexOf(command);
			while (i >= 0) {
				// End command => Goto macro
				sequence.replace(i, createKeywordToken("GOTO"), new LiteralToken(" "),
						new LineNumberReferenceToken(macro.getLineNumberStart()));
				lineEdited = true;
				i = sequence.getNextIndexOf(command, i + 3);
			}
			if (lineEdited) {
				addCodeLine(sourceCode, sequence);
			}
		}
	}

	private void invokeMacroFromGotoLoops(BasicSourceCode sourceCode, EndingMacro macro) throws BasicException {
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
								sequence.replace(i, new LineNumberReferenceToken(macro.getLineNumberStart()));
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