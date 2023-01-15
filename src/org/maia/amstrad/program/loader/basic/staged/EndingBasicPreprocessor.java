package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
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
	}

	protected void addMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		int lnStep = sourceCode.getDominantLineNumberStep();
		int ln = sourceCode.getNextAvailableLineNumber(lnStep);
		int addr = session.reserveMemoryTrapAddress();
		addCodeLine(sourceCode, ln, "REM @Ending");
		addCodeLine(sourceCode, ln + 1 * lnStep, "POKE &" + Integer.toHexString(addr) + ",1");
		addCodeLine(sourceCode, ln + 2 * lnStep, "GOTO " + (ln + 2 * lnStep));
		session.addMacro(new EndingMacro(ln, addr));
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

		public EndingMacro(int lineNumberStart, int memoryTrapAddress) {
			super(lineNumberStart);
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