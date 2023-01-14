package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.AmstradProgramRuntimeListener;
import org.maia.amstrad.program.loader.AmstradProgramLoaderFactory;
import org.maia.amstrad.program.loader.basic.BasicProgramLoader;
import org.maia.amstrad.util.AmstradUtils;

public class EndingBasicPreprocessor extends StagedBasicPreprocessor {

	public EndingBasicPreprocessor() {
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		EndingMacro macro = addMacro(sourceCode, session);
		session.getProgramRuntime().addListener(new EndingRuntimeListener(session, macro));
	}

	protected EndingMacro addMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int lnStep = sourceCode.getDominantLineNumberStep();
		int ln = sourceCode.getNextAvailableLineNumber(lnStep);
		int addr = session.claimMemoryTrapAddress();
		String address = "&" + Integer.toHexString(addr);
		addCodeLine(sourceCode, ln, "REM @Ending");
		addCodeLine(sourceCode, ln + 1 * lnStep, "POKE " + address + ",1");
		addCodeLine(sourceCode, ln + 2 * lnStep, "GOTO " + (ln + 2 * lnStep));
		addCodeLine(sourceCode, ln + 3 * lnStep, "END");
		return new EndingMacro(ln, addr);
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
		AmstradUtils.sleep(500L); // wait for Basic direct modus
		discloseCode(session);
		clearScreen(session);
		performEndingAction(session);
	}

	protected void discloseCode(StagedBasicProgramLoaderSession session) {
		AmstradPc amstradPc = session.getAmstradPc();
		EndingBasicCodeDisclosure disclosure = session.getCodeDisclosure();
		if (EndingBasicCodeDisclosure.HIDE_CODE.equals(disclosure)) {
			amstradPc.getMonitor().freezeFrame();
			amstradPc.getKeyboard().enter("NEW");
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

	protected void clearScreen(StagedBasicProgramLoaderSession session) {
		AmstradPc amstradPc = session.getAmstradPc();
		amstradPc.getMonitor().freezeFrame();
		amstradPc.getKeyboard().enter("CLS");
		amstradPc.getMonitor().unfreezeFrame();
	}

	protected void performEndingAction(StagedBasicProgramLoaderSession session) {
		EndingBasicAction endingAction = session.getEndingAction();
		if (endingAction != null) {
			endingAction.perform(session.getProgramRuntime());
		}
	}

	private static class EndingMacro {

		private int lineNumberStart;

		private int memoryTrapAddress;

		public EndingMacro(int lineNumberStart, int memoryTrapAddress) {
			this.lineNumberStart = lineNumberStart;
			this.memoryTrapAddress = memoryTrapAddress;
		}

		public int getLineNumberStart() {
			return lineNumberStart;
		}

		public int getMemoryTrapAddress() {
			return memoryTrapAddress;
		}

	}

	private class EndingMacroHandler implements AmstradMemoryTrapHandler {

		private StagedBasicProgramLoaderSession session;

		public EndingMacroHandler(StagedBasicProgramLoaderSession session) {
			this.session = session;
		}

		@Override
		public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
			AmstradPc amstradPc = getSession().getAmstradPc();
			amstradPc.getMonitor().freezeFrame();
			amstradPc.getKeyboard().breakEscape(); // to Basic direct modus
		}

		private StagedBasicProgramLoaderSession getSession() {
			return session;
		}

	}

	private class EndingRuntimeListener implements AmstradProgramRuntimeListener {

		private StagedBasicProgramLoaderSession session;

		private EndingMacro macro;

		public EndingRuntimeListener(StagedBasicProgramLoaderSession session, EndingMacro macro) {
			this.session = session;
			this.macro = macro;
		}

		@Override
		public void amstradProgramIsRun(AmstradProgramRuntime programRuntime) {
			programRuntime.getAmstradPc().getMemory().addMemoryTrap(getMacro().getMemoryTrapAddress(), false,
					new EndingMacroHandler(getSession()));
		}

		@Override
		public void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
			programRuntime.getAmstradPc().getMemory().removeMemoryTrapsAt(getMacro().getMemoryTrapAddress());
			if (programRemainsLoaded) {
				// Break-Escape
				handleProgramEndedInSeparateThread(getSession());
			}
		}

		private StagedBasicProgramLoaderSession getSession() {
			return session;
		}

		private EndingMacro getMacro() {
			return macro;
		}

	}

}