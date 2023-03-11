package org.maia.amstrad.load.basic.staged.file;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.VariableToken;
import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.file.PrintStreamCommand.Argument;
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
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		try {
			return Arrays.asList(stf.createBasicKeyword("OPENOUT"), stf.createBasicKeyword("PRINT"),
					stf.createBasicKeyword("CLOSEOUT"));
		} catch (BasicSyntaxException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (originalCodeContainsKeyword(sourceCode, "OPENOUT", session)) {
			invokeTextSave(sourceCode, session);
		}
	}

	private void invokeTextSave(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (sourceCode instanceof LocomotiveBasicSourceCode) {
			Set<VariableToken> vars = ((LocomotiveBasicSourceCode) sourceCode).getUniqueVariables();
			StringTypedVariableToken textBufferVariable = LocomotiveBasicVariableSpace.generateNewStringVariable(vars);
			int addrTrap = session.reserveMemory(1);
			TextSaveRuntimeListener listener = new TextSaveRuntimeListener(sourceCode, textBufferVariable, session,
					addrTrap);
			invokeOnOpenout(sourceCode, listener, session);
			invokeOnPrint(sourceCode, listener, session);
			invokeOnCloseout(sourceCode, listener, session);
			listener.install();
		}
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
		int commandRef = listener.registerCommand(new PrintStreamCommand()).getReferenceNumber();
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken PRINT = createKeywordToken(language, "PRINT");
		BasicSourceToken SEP = createInstructionSeparatorToken(language);
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(PRINT);
				while (i >= 0) {
					// PRINT #9 => waitresume macro
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					PrintStreamCommand command = PrintStreamCommand.parseFrom(sequence.subSequence(i, j));
					if (command != null) {
						BasicSourceTokenSequence commandSeq = new BasicSourceTokenSequence();
						for (int argi = 0; argi < command.getArguments().size(); argi++) {
							if (argi > 0)
								commandSeq.append(stf.createInstructionSeparator());
							Argument commandArg = command.getArguments().get(argi);
							commandSeq.append(listener.getTextBufferVariable(), stf.createOperator("="));
							if (commandArg.hasVariable()) {
								BasicSourceTokenSequence varSeq = new BasicSourceTokenSequence();
								varSeq.append(commandArg.getVariable());
								if (commandArg.isVariableIndexed()) {
									varSeq.append(stf.createLiteral(commandArg.getVariableArrayIndexString()));
								}
								if (commandArg.getVariable() instanceof StringTypedVariableToken) {
									commandSeq.append(varSeq);
								} else {
									commandSeq.append(stf.createBasicKeyword("STR$"), stf.createLiteral("("));
									commandSeq.append(varSeq);
									commandSeq.append(stf.createLiteral(")"));
								}
							} else if (commandArg.hasLiteralString()) {
								commandSeq.append(commandArg.getLiteralString());
							} else if (commandArg.hasLiteralNumber()) {
								commandSeq.append(stf.createBasicKeyword("STR$"), stf.createLiteral("("),
										commandArg.getLiteralNumber(), stf.createLiteral(")"));
							}
							commandSeq.append(stf.createInstructionSeparator());
							commandSeq.append(createWaitResumeMacroInvocationSequence(session, addrTrap, commandRef));
						}
						sequence.replaceRange(i, j, commandSeq);
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
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
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
					BasicSourceTokenSequence commandSeq = createWaitResumeMacroInvocationSequence(session, addrTrap,
							commandRef);
					commandSeq.append(stf.createInstructionSeparator(), listener.getTextBufferVariable(),
							stf.createOperator("="), stf.createLiteralQuoted(""));
					sequence.replaceRange(i, j, commandSeq);
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

	protected void handlePrintStream(PrintStreamCommand command, StringTypedVariableToken textBufferVariable,
			BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		try {
			LocomotiveBasicVariableSpace vars = getRuntimeVariables(session);
			String value = vars.getValue(textBufferVariable);
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

		private StringTypedVariableToken textBufferVariable;

		public TextSaveRuntimeListener(BasicSourceCode sourceCode, StringTypedVariableToken textBufferVariable,
				StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
			super(sourceCode, session, memoryTrapAddress);
			this.textBufferVariable = textBufferVariable;
		}

		@Override
		protected TextSaveMacroHandler createMacroHandler(FileCommandResolver resolver) {
			WaitResumeMacro macro = getSession().getMacroAdded(WaitResumeMacro.class);
			return new TextSaveMacroHandler(macro, getSourceCode(), getTextBufferVariable(), getSession(), resolver);
		}

		@Override
		public void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
			super.amstradProgramIsDisposed(programRuntime, programRemainsLoaded);
			getSession().closeTextFileWriter();
		}

		public StringTypedVariableToken getTextBufferVariable() {
			return textBufferVariable;
		}

	}

	private class TextSaveMacroHandler extends FileCommandMacroHandler {

		private StringTypedVariableToken textBufferVariable;

		public TextSaveMacroHandler(WaitResumeMacro macro, BasicSourceCode sourceCode,
				StringTypedVariableToken textBufferVariable, StagedBasicProgramLoaderSession session,
				FileCommandResolver resolver) {
			super(macro, sourceCode, session, resolver);
			this.textBufferVariable = textBufferVariable;
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			if (command instanceof OpenoutCommand) {
				handleOpenout((OpenoutCommand) command, fileReference, getSourceCode(), getSession());
			} else if (command instanceof PrintStreamCommand) {
				handlePrintStream((PrintStreamCommand) command, getTextBufferVariable(), getSourceCode(), getSession());
			} else if (command instanceof CloseoutCommand) {
				handleCloseout((CloseoutCommand) command, getSourceCode(), getSession());
			}
		}

		private StringTypedVariableToken getTextBufferVariable() {
			return textBufferVariable;
		}

	}

}