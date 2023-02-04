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
import org.maia.amstrad.load.basic.staged.ErrorOutBasicPreprocessor.ErrorOutMacro;
import org.maia.amstrad.load.basic.staged.ErrorOutCodes;
import org.maia.amstrad.load.basic.staged.StagedBasicMacro;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.FileReference;

public class ChainMergeBasicPreprocessor extends FileCommandBasicPreprocessor implements ErrorOutCodes {

	public ChainMergeBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 2; // for chainmerge macro
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(ChainMergeMacro.class)) {
			addChainMergeMacro(sourceCode, session);
		}
		if (originalCodeContainsKeyword(sourceCode, "CHAIN", session)) {
			invokeChainMergeMacro(sourceCode, session);
		}
	}

	private void addChainMergeMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln2 = session.acquireLargestAvailablePreambleLineNumber();
		int ln1 = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln1, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln1
				+ (session.produceRemarks() ? ":REM @chainmerge" : ""));
		addCodeLine(sourceCode, ln2, "RESTORE:GOTO 0" + (session.produceRemarks() ? ":REM @chainmerge" : ""));
		session.addMacro(new ChainMergeMacro(ln1, ln2, addrResume));
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
			endWithError(ERR_FILE_NOT_FOUND, currentSourceCode, session);
		} else {
			// TODO IF chainedProgram already merged, do nothing ELSE merge
			System.out.println(command);
			System.out.println(chainedProgram);
		}
	}

	private void endWithError(int errorCode, BasicSourceCode currentSourceCode,
			StagedBasicProgramLoaderSession session) {
		try {
			int ln = session.getMacroAdded(ChainMergeMacro.class).getLineNumberEnd();
			int lnResume = session.getMacroAdded(ErrorOutMacro.class).getLineNumberStart();
			substituteGotoLineNumber(ln, lnResume, currentSourceCode, session);
			substituteErrorCode(errorCode, currentSourceCode, session);
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