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
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;

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
		addCodeLine(sourceCode, ln2, "END" + (session.produceRemarks() ? ":REM @chainrun" : ""));
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
			try {
				delay(DELAYMILLIS_CHAIN_RUN / 2L);
				AmstradMonitor monitor = session.getAmstradPc().getMonitor();
				monitor.freezeFrame();
				resumeRun(macro, session);
				session.getAmstradPc().getBasicRuntime().waitUntilReady();
				session.getProgramRuntime().dispose(true);
				delay(DELAYMILLIS_CHAIN_RUN / 2L);
				AmstradProgramRuntime chainedProgramRuntime = session.getLoader().load(chainedProgram);
				if (command.hasStartingLineNumber()) {
					int ln = command.getStartingLineNumber();
					BasicLineNumberLinearMapping mapping = session.getLoader().getLastSession()
							.getOriginalToStagedLineNumberMapping();
					if (mapping.isMapped(ln)) {
						chainedProgramRuntime.run(String.valueOf(mapping.getNewLineNumber(ln)));
					} else {
						chainedProgramRuntime.run();
					}
				} else {
					chainedProgramRuntime.run();
				}
				delay(DELAYMILLIS_CHAIN_RUN_DISPLAY); // avoid seeing "RUN" on display
				monitor.unfreezeFrame();
				System.out.println("ChainRun completed successfully");
			} catch (AmstradProgramException e) {
				endWithError(ERR_CHAIN_RUN_FAILURE, sourceCode, macro, session);
			}
		}
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