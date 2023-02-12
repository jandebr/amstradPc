package org.maia.amstrad.load.basic.staged.file;

import java.io.IOException;

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
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.file.WaitResumeBasicPreprocessor.WaitResumeMacro;
import org.maia.amstrad.program.AmstradProgram.FileReference;

public class BinarySaveBasicPreprocessor extends FileCommandBasicPreprocessor {

	public BinarySaveBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0; // reusing waitresume macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (originalCodeContainsKeyword(sourceCode, "SAVE", session)) {
			invokeBinarySave(sourceCode, session);
		}
	}

	private void invokeBinarySave(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		int lnGoto = macro.getLineNumberFrom();
		int addrResume = macro.getResumeMemoryAddress();
		int addrTrap = session.reserveMemory(1);
		BinarySaveRuntimeListener listener = new BinarySaveRuntimeListener(sourceCode, session, addrTrap);
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken SAVE = createKeywordToken(language, "SAVE");
		BasicSourceToken SEP = new InstructionSeparatorToken();
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(SAVE);
				while (i >= 0) {
					// SAVE => binaryio macro
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					BinarySaveCommand command = BinarySaveCommand.parseFrom(sequence.subSequence(i, j));
					if (command != null) {
						int ref = listener.registerCommand(command).getReferenceNumber();
						sequence.replaceRange(i, j, createKeywordToken(language, "POKE"), new LiteralToken(" "),
								new Integer16BitHexadecimalToken("&" + Integer.toHexString(addrResume)),
								new LiteralToken(","), new SingleDigitDecimalToken(0), SEP,
								createKeywordToken(language, "POKE"), new LiteralToken(" "),
								new Integer16BitHexadecimalToken("&" + Integer.toHexString(addrTrap)),
								new LiteralToken(","), new SingleDigitDecimalToken(ref), SEP,
								createKeywordToken(language, "GOSUB"), new LiteralToken(" "),
								new LineNumberReferenceToken(lnGoto));
					}
					i = sequence.getNextIndexOf(SAVE, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
		listener.install();
	}

	protected void handleBinarySave(BinarySaveCommand command, FileReference fileReference, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		if (fileReference == null) {
			endWithError(ERR_FILE_NOT_FOUND, sourceCode, macro, session);
		} else {
			try {
				session.getBasicRuntime().saveBinaryFile(fileReference.getTargetFile(), command.getMemoryOffset(),
						command.getMemoryLength());
				resumeRun(macro, session);
				System.out.println("BinarySave completed successfully");
			} catch (IOException e) {
				endWithError(ERR_BINARY_SAVE_FAILURE, sourceCode, macro, session);
			}
		}
	}

	private class BinarySaveRuntimeListener extends FileCommandRuntimeListener {

		public BinarySaveRuntimeListener(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
				int memoryTrapAddress) {
			super(sourceCode, session, memoryTrapAddress);
		}

		@Override
		protected BinarySaveMacroHandler createMacroHandler(FileCommandResolver resolver) {
			WaitResumeMacro macro = getSession().getMacroAdded(WaitResumeMacro.class);
			return new BinarySaveMacroHandler(macro, getSourceCode(), getSession(), resolver);
		}

	}

	private class BinarySaveMacroHandler extends FileCommandMacroHandler {

		public BinarySaveMacroHandler(WaitResumeMacro macro, BasicSourceCode sourceCode,
				StagedBasicProgramLoaderSession session, FileCommandResolver resolver) {
			super(macro, sourceCode, session, resolver);
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			handleBinarySave((BinarySaveCommand) command, fileReference, getSourceCode(), getSession());
		}

	}

}