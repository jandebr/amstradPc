package org.maia.amstrad.load.basic.staged.file;

import java.io.IOException;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.file.WaitResumeBasicPreprocessor.WaitResumeMacro;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.util.AmstradIO;

public class BinaryLoadBasicPreprocessor extends FileCommandBasicPreprocessor implements LocomotiveBasicMemoryMap {

	private static final int BLOCK_BYTESIZE = 2048;

	public BinaryLoadBasicPreprocessor() {
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
		if (originalCodeContainsKeyword(sourceCode, "LOAD", session)) {
			invokeBinaryLoad(sourceCode, session);
		}
	}

	private void invokeBinaryLoad(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		int lnGoto = macro.getLineNumberFrom();
		int addrResume = macro.getResumeMemoryAddress();
		int addrTrap = session.reserveMemory(1);
		BinaryLoadRuntimeListener listener = new BinaryLoadRuntimeListener(sourceCode, session, addrTrap);
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken LOAD = createKeywordToken(language, "LOAD");
		BasicSourceToken SEP = new InstructionSeparatorToken();
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(LOAD);
				while (i >= 0) {
					// LOAD => binaryio macro
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					BinaryLoadCommand command = BinaryLoadCommand.parseFrom(sequence.subSequence(i, j));
					if (command != null) {
						int ref = listener.registerCommand(command).getReferenceNumber();
						sequence.replaceRange(i, j, stf.createBasicKeyword("POKE"), stf.createLiteral(" "),
								stf.createPositiveInteger16BitHexadecimal(addrResume), stf.createLiteral(","),
								stf.createPositiveIntegerSingleDigitDecimal(0), SEP, stf.createBasicKeyword("POKE"),
								stf.createLiteral(" "), stf.createPositiveInteger16BitHexadecimal(addrTrap),
								stf.createLiteral(","), stf.createPositiveInteger8BitDecimal(ref), SEP,
								stf.createBasicKeyword("GOSUB"), stf.createLiteral(" "),
								stf.createLineNumberReference(lnGoto));
					}
					i = sequence.getNextIndexOf(LOAD, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
		listener.install();
	}

	protected void handleBinaryLoad(BinaryLoadCommand command, FileReference fileReference, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		if (fileReference == null) {
			endWithError(ERR_FILE_NOT_FOUND, sourceCode, macro, session);
		} else {
			try {
				if (shouldLoadInBytes(command)) {
					loadInBytes(command, fileReference, session);
				} else if (shouldLoadInBlocks(command)) {
					loadInBlocks(command, fileReference, session);
				} else {
					delay(DELAYMILLIS_BINARY_LOAD);
					session.getBasicRuntime().loadBinaryFile(fileReference.getTargetFile(), command.getMemoryOffset());
				}
				resumeRun(macro, session);
				System.out.println("BinaryLoad completed successfully");
			} catch (Exception e) {
				endWithError(ERR_BINARY_LOAD_FAILURE, sourceCode, macro, session);
			}
		}
	}

	private boolean shouldLoadInBytes(BinaryLoadCommand command) {
		return command.getMemoryOffset() >= ADDRESS_GRAPHICS_DISPLAY_START;
	}

	private boolean shouldLoadInBlocks(BinaryLoadCommand command) {
		return false;
	}

	private void loadInBytes(BinaryLoadCommand command, FileReference fileReference,
			StagedBasicProgramLoaderSession session) throws IOException {
		delay(DELAYMILLIS_BINARY_LOAD_NEW_BLOCK);
		BasicRuntime rt = session.getBasicRuntime();
		int addr = command.getMemoryOffset();
		byte[] data = AmstradIO.readBinaryFileContents(fileReference.getTargetFile());
		for (int i = 0; i < data.length; i++) {
			rt.poke(addr++, data[i]);
			boolean newBlockAhead = addr % BLOCK_BYTESIZE == 0 && i < data.length - 1;
			if (newBlockAhead) {
				delay(DELAYMILLIS_BINARY_LOAD_NEW_BLOCK);
			} else if (i % 8 == 7) {
				delay(DELAYMILLIS_BINARY_LOAD_DWORD);
			}
		}
	}

	private void loadInBlocks(BinaryLoadCommand command, FileReference fileReference,
			StagedBasicProgramLoaderSession session) throws IOException {
		BasicRuntime rt = session.getBasicRuntime();
		byte[] data = AmstradIO.readBinaryFileContents(fileReference.getTargetFile());
		if (data.length > 0) {
			int blocks = 1 + (data.length - 1) / BLOCK_BYTESIZE;
			for (int bi = 0; bi < blocks; bi++) {
				int offset = bi * BLOCK_BYTESIZE;
				int end = Math.min(offset + BLOCK_BYTESIZE, data.length);
				delay(DELAYMILLIS_BINARY_LOAD_NEW_BLOCK);
				rt.loadBinaryData(data, offset, end - offset, command.getMemoryOffset() + offset);
			}
		}
	}

	private class BinaryLoadRuntimeListener extends FileCommandRuntimeListener {

		public BinaryLoadRuntimeListener(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
				int memoryTrapAddress) {
			super(sourceCode, session, memoryTrapAddress);
		}

		@Override
		protected BinaryLoadMacroHandler createMacroHandler(FileCommandResolver resolver) {
			WaitResumeMacro macro = getSession().getMacroAdded(WaitResumeMacro.class);
			return new BinaryLoadMacroHandler(macro, getSourceCode(), getSession(), resolver);
		}

	}

	private class BinaryLoadMacroHandler extends FileCommandMacroHandler {

		public BinaryLoadMacroHandler(WaitResumeMacro macro, BasicSourceCode sourceCode,
				StagedBasicProgramLoaderSession session, FileCommandResolver resolver) {
			super(macro, sourceCode, session, resolver);
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			handleBinaryLoad((BinaryLoadCommand) command, fileReference, getSourceCode(), getSession());
		}

	}

}