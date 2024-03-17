package org.maia.amstrad.program.load.basic.staged.file;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.load.basic.staged.PreambleJumpingBasicPreprocessor.PreambleJumpingMacro;
import org.maia.amstrad.program.load.basic.staged.PreambleLandingBasicPreprocessor.PreambleLandingMacro;
import org.maia.amstrad.program.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.program.load.basic.staged.file.ChainRunBasicPreprocessor.ChainRunMacro;

public class RunBasicPreprocessor extends FileCommandBasicPreprocessor {

	public RunBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0; // reusing chainrun macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		try {
			return Arrays.asList(stf.createBasicKeyword("RUN"));
		} catch (BasicSyntaxException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (originalCodeContainsKeyword(sourceCode, "RUN", session)) {
			invokeOnRunKeyword(sourceCode, session);
		}
	}

	private void invokeOnRunKeyword(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		ChainRunMacro macro = session.getMacroAdded(ChainRunMacro.class);
		int addrTrap = session.reserveMemory(1);
		RunRuntimeListener listener = new RunRuntimeListener(sourceCode, session, addrTrap);
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken RUN = createKeywordToken(language, "RUN");
		BasicSourceToken SEP = createInstructionSeparatorToken(language);
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(RUN);
				while (i >= 0) {
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					RunCommand command = RunCommand.parseFrom(sequence.subSequence(i, j));
					if (command != null) {
						// RUN => chainrun macro
						int ref = listener.registerCommand(command).getReferenceNumber();
						sequence.replaceRange(i, j, createGotoMacroInvocationSequence(macro, addrTrap, ref));
					}
					i = sequence.getNextIndexOf(RUN, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
		listener.install();
	}

	protected void handleRun(RunCommand command, BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		ChainRunMacro macro = session.getMacroAdded(ChainRunMacro.class);
		try {
			PreambleJumpingMacro jump = session.getMacroAdded(PreambleJumpingMacro.class);
			PreambleLandingMacro land = session.getMacroAdded(PreambleLandingMacro.class);
			int lnStart = land.getLineNumberFrom();
			if (command.hasStartingLineNumber()) {
				lnStart = command.getStartingLineNumber();
			}
			substituteLineNumberReference(jump.getLineNumberFrom(), lnStart, sourceCode);
			substituteLineNumberReference(macro.getLineNumberTo(), sourceCode.getSmallestLineNumber(), sourceCode);
			resumeWithNewSourceCode(sourceCode, macro, session);
		} catch (Exception e) {
			System.err.println(e);
			endWithError(ERR_RUN_FAILURE, sourceCode, macro, session);
		}
	}

	private class RunRuntimeListener extends FileCommandRuntimeListener {

		public RunRuntimeListener(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
				int memoryTrapAddress) {
			super(sourceCode, session, memoryTrapAddress);
		}

		@Override
		protected RunMacroHandler createMacroHandler(FileCommandResolver resolver) {
			ChainRunMacro macro = getSession().getMacroAdded(ChainRunMacro.class);
			return new RunMacroHandler(macro, getSourceCode(), getSession(), resolver);
		}

	}

	private class RunMacroHandler extends FileCommandMacroHandler {

		public RunMacroHandler(ChainRunMacro macro, BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
				FileCommandResolver resolver) {
			super(macro, sourceCode, session, resolver);
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			handleRun((RunCommand) command, getSourceCode(), getSession());
		}

	}

}