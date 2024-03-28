package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace.VariableNotFoundException;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.UntypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.VariableToken;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.program.load.basic.staged.WaitResumeBasicPreprocessor.WaitResumeMacro;

public class RandomizeBasicPreprocessor extends StagedBasicPreprocessor {

	public RandomizeBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 1; // for randomize macro
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
		if (!session.hasMacrosAdded(RandomizeMacro.class)) {
			addRandomizeMacro(sourceCode, session);
		}
	}

	private void addRandomizeMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (sourceCode instanceof LocomotiveBasicSourceCode) {
			int ln = session.acquireSmallestAvailablePreambleLineNumber();
			int addrTrap = session.reserveMemory(1);
			Set<VariableToken> vars = ((LocomotiveBasicSourceCode) sourceCode).getUniqueVariables();
			UntypedVariableToken rndSeedVariable = LocomotiveBasicVariableSpace.generateNewUntypedVariable(vars);
			RandomizeRuntimeListener listener = new RandomizeRuntimeListener(session, addrTrap, rndSeedVariable);
			BasicSourceTokenSequence sequence = createWaitResumeMacroInvocationSequence(session, addrTrap, 1);
			String vs = rndSeedVariable.getSourceFragment();
			String lineCode = vs + "=TIME:" + sequence.getSourceCode() + ":RANDOMIZE " + vs
					+ (session.produceRemarks() ? ":REM @random" : "");
			addCodeLine(sourceCode, ln, lineCode);
			session.addMacro(new RandomizeMacro(new BasicLineNumberRange(ln)));
			listener.install();
		}
	}

	public static class RandomizeMacro extends StagedBasicMacro {

		public RandomizeMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

	private class RandomizeRuntimeListener extends StagedBasicProgramTrappedRuntimeListener {

		private UntypedVariableToken rndSeedVariable;

		public RandomizeRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress,
				UntypedVariableToken rndSeedVariable) {
			super(session, memoryTrapAddress);
			this.rndSeedVariable = rndSeedVariable;
		}

		@Override
		protected RandomizeMacroHandler createMemoryTrapHandler() {
			RandomizeMacro macro = getSession().getMacroAdded(RandomizeMacro.class);
			return new RandomizeMacroHandler(macro, getSession(), getRndSeedVariable());
		}

		public UntypedVariableToken getRndSeedVariable() {
			return rndSeedVariable;
		}

	}

	private class RandomizeMacroHandler extends StagedBasicMacroHandler {

		private UntypedVariableToken rndSeedVariable;

		public RandomizeMacroHandler(RandomizeMacro macro, StagedBasicProgramLoaderSession session,
				UntypedVariableToken rndSeedVariable) {
			super(macro, session);
			this.rndSeedVariable = rndSeedVariable;
		}

		@Override
		public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
			BasicRuntime rt = getSession().getBasicRuntime();
			if (rt instanceof LocomotiveBasicRuntime) {
				LocomotiveBasicVariableSpace varSpace = ((LocomotiveBasicRuntime) rt).getVariableSpace();
				try {
					varSpace.setValue(getRndSeedVariable(), generateRandomSeed(), false);
				} catch (VariableNotFoundException e) {
					System.err.println(e);
				}
			}
			WaitResumeMacro macro = getSession().getMacroAdded(WaitResumeMacro.class);
			resumeRun(macro, getSession());
		}

		private double generateRandomSeed() {
			double a = Math.floor(Math.random() * 10000);
			double b = Math.floor(Math.random() * 10000);
			return a * b;
		}

		public UntypedVariableToken getRndSeedVariable() {
			return rndSeedVariable;
		}

	}

}