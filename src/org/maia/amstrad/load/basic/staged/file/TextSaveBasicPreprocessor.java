package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.file.WaitResumeBasicPreprocessor.WaitResumeMacro;
import org.maia.amstrad.program.AmstradProgram.FileReference;

public class TextSaveBasicPreprocessor extends FileCommandBasicPreprocessor {

	public TextSaveBasicPreprocessor() {
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
		if (originalCodeContainsKeyword(sourceCode, "OPENOUT", session)) {
			invokeTextSave(sourceCode, session);
		}
	}

	private void invokeTextSave(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrTrap = session.reserveMemory(1);
		TextSaveRuntimeListener listener = new TextSaveRuntimeListener(sourceCode, session, addrTrap);
		invokeOnOpenout(sourceCode, listener, session);
		invokeOnPrint(sourceCode, listener, session);
		invokeOnCloseout(sourceCode, listener, session);
		listener.install();
	}

	private void invokeOnOpenout(BasicSourceCode sourceCode, TextSaveRuntimeListener listener,
			StagedBasicProgramLoaderSession session) throws BasicException {
		int addrTrap = listener.getMemoryTrapAddress();
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken OPENOUT = createKeywordToken(language, "OPENOUT");
		BasicSourceToken SEP = createInstructionSeparatorToken(language);
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(OPENOUT);
				while (i >= 0) {
					// OPENOUT => waitresume macro
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					OpenoutCommand command = OpenoutCommand.parseFrom(sequence.subSequence(i, j));
					if (command != null) {
						int ref = listener.registerCommand(command).getReferenceNumber();
						sequence.replaceRange(i, j, createWaitResumeMacroInvocationSequence(session, addrTrap, ref));
					}
					i = sequence.getNextIndexOf(OPENOUT, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	private void invokeOnPrint(BasicSourceCode sourceCode, TextSaveRuntimeListener listener,
			StagedBasicProgramLoaderSession session) throws BasicException {
		int addrTrap = listener.getMemoryTrapAddress();
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken PRINT = createKeywordToken(language, "PRINT");
		BasicSourceToken SEP = createInstructionSeparatorToken(language);
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(PRINT);
				while (i >= 0) {
					// PRINT => waitresume macro
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					PrintStreamCommand command = PrintStreamCommand.parseFrom(sequence.subSequence(i, j));
					if (command != null) {
						int ref = listener.registerCommand(command).getReferenceNumber();
						sequence.replaceRange(i, j, createWaitResumeMacroInvocationSequence(session, addrTrap, ref));
					}
					i = sequence.getNextIndexOf(PRINT, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	private void invokeOnCloseout(BasicSourceCode sourceCode, TextSaveRuntimeListener listener,
			StagedBasicProgramLoaderSession session) throws BasicException {
		int addrTrap = listener.getMemoryTrapAddress();
		int commandRef = listener.registerCommand(new CloseoutCommand()).getReferenceNumber();
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken CLOSEOUT = createKeywordToken(language, "CLOSEOUT");
		BasicSourceToken SEP = createInstructionSeparatorToken(language);
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(CLOSEOUT);
				while (i >= 0) {
					// CLOSEOUT => waitresume macro
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					sequence.replaceRange(i, j, createWaitResumeMacroInvocationSequence(session, addrTrap, commandRef));
					i = sequence.getNextIndexOf(CLOSEOUT, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	protected void handleOpenout(OpenoutCommand command, FileReference fileReference, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		if (fileReference == null) {
			endWithError(ERR_FILE_NOT_FOUND, sourceCode, macro, session);
		} else {
			try {
				session.openTextFileWriter(fileReference.getTargetFile());
				delay(DELAYMILLIS_OPENOUT);
				resumeRun(macro, session);
				System.out.println("Completed " + command);
			} catch (Exception e) {
				System.err.println(e);
				endWithError(ERR_TEXT_SAVE_FAILURE, sourceCode, macro, session);
			}
		}
	}

	protected void handlePrintStream(PrintStreamCommand command, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		try {
			String value = command.getValueToPrint(session);
			session.getTextFileWriter().writeLine(value);
			delay(DELAYMILLIS_PRINTSTREAM);
			resumeRun(macro, session);
			System.out.println("Completed " + command);
		} catch (Exception e) {
			System.err.println(e);
			endWithError(ERR_TEXT_SAVE_FAILURE, sourceCode, macro, session);
		}
	}

	protected void handleCloseout(CloseoutCommand command, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		try {
			session.closeTextFileWriter();
			delay(DELAYMILLIS_CLOSEOUT);
			resumeRun(macro, session);
			System.out.println("Completed " + command);
		} catch (Exception e) {
			System.err.println(e);
			endWithError(ERR_TEXT_SAVE_FAILURE, sourceCode, macro, session);
		}
	}

	private class TextSaveRuntimeListener extends FileCommandRuntimeListener {

		public TextSaveRuntimeListener(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
				int memoryTrapAddress) {
			super(sourceCode, session, memoryTrapAddress);
		}

		@Override
		protected TextSaveMacroHandler createMacroHandler(FileCommandResolver resolver) {
			WaitResumeMacro macro = getSession().getMacroAdded(WaitResumeMacro.class);
			return new TextSaveMacroHandler(macro, getSourceCode(), getSession(), resolver);
		}

		@Override
		public void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
			super.amstradProgramIsDisposed(programRuntime, programRemainsLoaded);
			getSession().closeTextFileWriter();
		}

	}

	private class TextSaveMacroHandler extends FileCommandMacroHandler {

		public TextSaveMacroHandler(WaitResumeMacro macro, BasicSourceCode sourceCode,
				StagedBasicProgramLoaderSession session, FileCommandResolver resolver) {
			super(macro, sourceCode, session, resolver);
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			if (command instanceof OpenoutCommand) {
				handleOpenout((OpenoutCommand) command, fileReference, getSourceCode(), getSession());
			} else if (command instanceof PrintStreamCommand) {
				handlePrintStream((PrintStreamCommand) command, getSourceCode(), getSession());
			} else if (command instanceof CloseoutCommand) {
				handleCloseout((CloseoutCommand) command, getSourceCode(), getSession());
			}
		}

	}

}