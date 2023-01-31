package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
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
import org.maia.amstrad.load.basic.staged.StagedBasicMacro;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.FileReference;

public class ChainMergeBasicPreprocessor extends FileCommandBasicPreprocessor {

	private static final int ERROR_PROGRAM_NOT_FOUND = 32;

	public ChainMergeBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 4; // for jumping, chainmerge and landing macro
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (originalCodeContainsKeyword(sourceCode, "CHAIN", session)) {
			if (!session.hasMacrosAdded(ChainMergeMacro.class)) {
				addChainMergeMacro(sourceCode, session);
			}
			invokeChainMergeMacro(sourceCode, session);
		}
	}

	private void addChainMergeMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln4 = session.acquireLargestAvailablePreambleLineNumber();
		int ln3 = session.acquireLargestAvailablePreambleLineNumber();
		int ln2 = session.acquireLargestAvailablePreambleLineNumber();
		int ln1 = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln1, "GOTO " + ln4 + (session.produceRemarks() ? ":REM @jump" : ""));
		addCodeLine(sourceCode, ln2, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln2
				+ (session.produceRemarks() ? ":REM @chainmerge" : ""));
		addCodeLine(sourceCode, ln3, "RESTORE:GOTO 0" + (session.produceRemarks() ? ":REM @chainmerge" : ""));
		addCodeLine(sourceCode, ln4, session.produceRemarks() ? "REM @land" : "'");
		session.addMacro(new JumpingMacro(ln1));
		session.addMacro(new ChainMergeMacro(ln2, ln3, addrResume));
		session.addMacro(new LandingMacro(ln4));
	}

	private void invokeChainMergeMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		ChainMergeMacro macro = session.getMacroAdded(ChainMergeMacro.class);
		int lnGoto = macro.getLineNumberStart();
		int addrResume = macro.getResumeMemoryAddress();
		int addrTrap = session.reserveMemory(1);
		ChainMergeRuntimeListener listener = new ChainMergeRuntimeListener(sourceCode, session, addrTrap);
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken CHAIN = createKeywordToken(language, "CHAIN");
		BasicSourceToken MERGE = createKeywordToken(language, "MERGE");
		BasicSourceToken SEP = new InstructionSeparatorToken();
		BasicLineNumberScope scope = session.getScopeExcludingMacros();
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(CHAIN);
				while (i >= 0) {
					int j = sequence.getIndexFollowingWhitespace(i + 1);
					if (j >= 0) {
						if (sequence.get(j).equals(MERGE)) {
							// CHAIN MERGE => chain merge macro
							j = sequence.getNextIndexOf(SEP, j + 1);
							if (j < 0)
								j = sequence.size();
							ChainMergeCommand command = ChainMergeCommand.parseFrom(sequence.subSequence(i, j));
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
						}
					}
					i = sequence.getNextIndexOf(CHAIN, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
		listener.install();
	}

	protected void handleChainMerge(ChainMergeCommand command, AmstradProgram chainedProgram,
			BasicSourceCode currentSourceCode, StagedBasicProgramLoaderSession session) {
		if (chainedProgram == null) {
			resumeWithError(ERROR_PROGRAM_NOT_FOUND, currentSourceCode, session);
		} else {
			// TODO IF chainedProgram already merged, do nothing ELSE merge
			System.out.println(command);
			System.out.println(chainedProgram);
		}
	}

	private void resumeWithError(int errorCode, BasicSourceCode currentSourceCode,
			StagedBasicProgramLoaderSession session) {
		int ln = session.getMacroAdded(ChainMergeMacro.class).getLineNumberEnd();
		try {
			addCodeLine(currentSourceCode, ln,
					"ERROR " + errorCode + (session.produceRemarks() ? ":REM @chainmerge###" : "")); // TODO fix filler
			System.out.println(currentSourceCode);
			replaceRunningCode(currentSourceCode, session);
			resumeRun(session);
		} catch (BasicException e) {
			e.printStackTrace();
		}
	}

	private void replaceRunningCode(BasicSourceCode newSourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		session.getBasicRuntime().swap(newSourceCode);
	}

	private void resumeRun(StagedBasicProgramLoaderSession session) {
		int addr = session.getMacroAdded(ChainMergeMacro.class).getResumeMemoryAddress();
		session.getBasicRuntime().poke(addr, (byte) 1);
	}

	public static class JumpingMacro extends StagedBasicMacro {

		public JumpingMacro(int lineNumber) {
			super(lineNumber);
		}

	}

	public static class LandingMacro extends StagedBasicMacro {

		public LandingMacro(int lineNumber) {
			super(lineNumber);
		}

	}

	public static class ChainMergeMacro extends StagedBasicMacro {

		private int resumeMemoryAddress;

		public ChainMergeMacro(int lineNumberStart, int lineNumberEnd, int resumeMemoryAddress) {
			super(lineNumberStart, lineNumberEnd);
			this.resumeMemoryAddress = resumeMemoryAddress;
		}

		public int getResumeMemoryAddress() {
			return resumeMemoryAddress;
		}

	}

	private class ChainMergeRuntimeListener extends FileCommandRuntimeListener {

		private BasicSourceCode sourceCode;

		public ChainMergeRuntimeListener(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
				int memoryTrapAddress) {
			super(session, memoryTrapAddress);
			this.sourceCode = sourceCode;
		}

		@Override
		protected FileCommandMacroHandler createMacroHandler(FileCommandResolver resolver) {
			ChainMergeMacro macro = getSession().getMacroAdded(ChainMergeMacro.class);
			return new ChainMergeMacroHandler(macro, getSourceCode(), getSession(), resolver);
		}

		private BasicSourceCode getSourceCode() {
			return sourceCode;
		}

	}

	private class ChainMergeMacroHandler extends FileCommandMacroHandler {

		private BasicSourceCode sourceCode;

		public ChainMergeMacroHandler(ChainMergeMacro macro, BasicSourceCode sourceCode,
				StagedBasicProgramLoaderSession session, FileCommandResolver resolver) {
			super(macro, session, resolver);
			this.sourceCode = sourceCode;
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			AmstradProgram chainedProgram = getReferencedProgram(fileReference);
			handleChainMerge((ChainMergeCommand) command, chainedProgram, getSourceCode(), getSession());
		}

		private BasicSourceCode getSourceCode() {
			return sourceCode;
		}

	}

}