package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;
import org.maia.amstrad.basic.locomotive.token.Integer16BitHexadecimalToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.basic.locomotive.token.LiteralToken;
import org.maia.amstrad.basic.locomotive.token.SingleDigitDecimalToken;
import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoader;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramRuntime;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.AmstradProgramException;

public class ChainRunBasicPreprocessor extends FileCommandBasicPreprocessor {

	public ChainRunBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 2; // for chainrun macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(ChainRunMacro.class)) {
			addChainRunMacro(sourceCode, session);
		}
		if (originalCodeContainsKeyword(sourceCode, "CHAIN", session)
				|| originalCodeContainsKeyword(sourceCode, "RUN", session)) {
			invokeChainRunMacro(sourceCode, session);
		}
	}

	private void addChainRunMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln2 = session.acquireLargestAvailablePreambleLineNumber();
		int ln1 = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln1, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln1
				+ (session.produceRemarks() ? ":REM @chainrun" : ""));
		addCodeLine(sourceCode, ln2, "RUN 0" + (session.produceRemarks() ? ":REM @chainrun" : ""));
		session.addMacro(new ChainRunMacro(new BasicLineNumberRange(ln1, ln2), addrResume));
	}

	private void invokeChainRunMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrTrap = session.reserveMemory(1);
		ChainRunRuntimeListener listener = new ChainRunRuntimeListener(sourceCode, session, addrTrap);
		BasicLanguage language = sourceCode.getLanguage();
		invokeChainRunMacroOnInstruction(sourceCode, createKeywordToken(language, "CHAIN"), listener, session);
		invokeChainRunMacroOnInstruction(sourceCode, createKeywordToken(language, "RUN"), listener, session);
		listener.install();
	}

	private void invokeChainRunMacroOnInstruction(BasicSourceCode sourceCode, BasicSourceToken instruction,
			ChainRunRuntimeListener listener, StagedBasicProgramLoaderSession session) throws BasicException {
		ChainRunMacro macro = session.getMacroAdded(ChainRunMacro.class);
		int lnGoto = macro.getLineNumberFrom();
		int addrResume = macro.getResumeMemoryAddress();
		int addrTrap = listener.getMemoryTrapAddress();
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken SEP = new InstructionSeparatorToken();
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(instruction);
				while (i >= 0) {
					// CHAIN or RUN => chainrun macro
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					ChainRunCommand command = ChainRunCommand.parseFrom(sequence.subSequence(i, j));
					if (command != null) {
						int ref = listener.registerCommand(command).getReferenceNumber();
						sequence.replaceRange(i, j, createKeywordToken(language, "POKE"), new LiteralToken(" "),
								new Integer16BitHexadecimalToken("&" + Integer.toHexString(addrResume)),
								new LiteralToken(","), new SingleDigitDecimalToken(0), SEP,
								createKeywordToken(language, "POKE"), new LiteralToken(" "),
								new Integer16BitHexadecimalToken("&" + Integer.toHexString(addrTrap)),
								new LiteralToken(","), new SingleDigitDecimalToken(ref), SEP,
								createKeywordToken(language, "GOTO"), new LiteralToken(" "),
								new LineNumberReferenceToken(lnGoto));
					}
					i = sequence.getNextIndexOf(instruction, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	protected void handleChainRun(ChainRunCommand command, AmstradBasicProgramFile chainedProgram,
			BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		ChainRunMacro macro = session.getMacroAdded(ChainRunMacro.class);
		if (chainedProgram == null) {
			endWithError(ERR_FILE_NOT_FOUND, sourceCode, macro, session);
		} else {
			delay(DELAYMILLIS_CHAIN_RUN);
			try {
				performChainRun(command, chainedProgram, session.getLoader());
				System.out.println("ChainRun completed successfully");
			} catch (AmstradProgramException | BasicException e) {
				endWithError(ERR_CHAIN_RUN_FAILURE, sourceCode, macro, session);
			}
		}
	}

	private void performChainRun(ChainRunCommand command, AmstradBasicProgramFile chainedProgram,
			StagedBasicProgramLoader loader) throws AmstradProgramException, BasicException {
		AmstradProgramRuntime chainedRuntime = loader.load(chainedProgram); // disposes the current program runtime,
																			// removes its associated memory traps (and
																			// loads the chained program code)
		chainedRuntime.run(StagedBasicProgramRuntime.RUN_ARG_CHAINRUN); // installs the new memory traps, nothing more
		StagedBasicProgramLoaderSession chainedSession = loader.getLastSession();
		BasicSourceCode sourceCode = chainedSession.getBasicRuntime().exportSourceCode();
		int lnStart = sourceCode.getSmallestLineNumber();
		if (command.hasStartingLineNumber()) {
			BasicLineNumberLinearMapping mapping = chainedSession.getOriginalToStagedLineNumberMapping();
			if (mapping.isMapped(command.getStartingLineNumber())) {
				lnStart = mapping.getNewLineNumber(command.getStartingLineNumber());
			}
		}
		ChainRunMacro macro = chainedSession.getMacroAdded(ChainRunMacro.class);
		substituteLineNumberReference(macro.getLineNumberTo(), lnStart, sourceCode);
		resumeWithNewSourceCode(sourceCode, macro, chainedSession);
	}

	public static class ChainRunMacro extends FileCommandMacro {

		public ChainRunMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

	private class ChainRunRuntimeListener extends FileCommandRuntimeListener {

		public ChainRunRuntimeListener(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
				int memoryTrapAddress) {
			super(sourceCode, session, memoryTrapAddress);
		}

		@Override
		protected ChainRunMacroHandler createMacroHandler(FileCommandResolver resolver) {
			ChainRunMacro macro = getSession().getMacroAdded(ChainRunMacro.class);
			return new ChainRunMacroHandler(macro, getSourceCode(), getSession(), resolver);
		}

	}

	private class ChainRunMacroHandler extends FileCommandMacroHandler {

		public ChainRunMacroHandler(ChainRunMacro macro, BasicSourceCode sourceCode,
				StagedBasicProgramLoaderSession session, FileCommandResolver resolver) {
			super(macro, sourceCode, session, resolver);
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			AmstradBasicProgramFile chainedProgram = getReferencedProgram(fileReference);
			handleChainRun((ChainRunCommand) command, chainedProgram, getSourceCode(), getSession());
		}

	}

}