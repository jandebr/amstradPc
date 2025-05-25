package org.maia.amstrad.program.load.basic.staged.file;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.program.load.basic.staged.StagedCommandResolver;
import org.maia.amstrad.program.load.basic.staged.WaitResumeBasicPreprocessor.WaitResumeMacro;
import org.maia.util.io.IOUtils;

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
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		try {
			return Arrays.asList(stf.createBasicKeyword("LOAD"));
		} catch (BasicSyntaxException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (originalCodeContainsKeyword(sourceCode, "LOAD", session)) {
			invokeBinaryLoad(sourceCode, session);
		}
	}

	private void invokeBinaryLoad(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrTrap = session.reserveMemory(1);
		BinaryLoadRuntimeListener listener = new BinaryLoadRuntimeListener(sourceCode, session, addrTrap);
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken LOAD = createKeywordToken(language, "LOAD");
		BasicSourceToken SEP = createInstructionSeparatorToken(language);
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(LOAD);
				while (i >= 0) {
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					BinaryLoadCommand command = BinaryLoadCommand.parseFrom(sequence.subSequence(i, j));
					if (command != null) {
						// LOAD => waitresume macro
						int ref = listener.registerCommand(command).getReferenceNumber();
						sequence.replaceRange(i, j, createWaitResumeMacroInvocationSequence(session, addrTrap, ref));
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
		if (fileReference == null || !fileReference.getTargetFile().exists()) {
			endWithError(ERR_FILE_NOT_FOUND, sourceCode, macro, session);
		} else {
			try {
				startFileOperation(session, fileReference, false, command.isSuppressMessages());
				if (shouldLoadInBytes(command)) {
					loadInBytes(command, fileReference, session);
				} else if (shouldLoadInBlocks(command)) {
					loadInBlocks(command, fileReference, session);
				} else {
					delayFileOperation(DELAYMILLIS_BINARY_LOAD);
					session.getAmstradPc().getTape().loadBinaryFile(fileReference.getTargetFile(),
							command.getMemoryOffset());
				}
				resumeRun(macro, session);
				System.out.println("Completed " + command);
			} catch (Exception e) {
				System.err.println(e);
				endWithError(ERR_BINARY_LOAD_FAILURE, sourceCode, macro, session);
			} finally {
				stopFileOperation(session);
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
		delayFileOperation(DELAYMILLIS_BINARY_LOAD_NEW_BLOCK);
		int programEndedAddr = session.getEndingMacro().getMemoryTrapAddress();
		BasicRuntime rt = session.getBasicRuntime();
		int addr = command.getMemoryOffset();
		byte[] data = IOUtils.readBinaryFileContents(fileReference.getTargetFile());
		for (int i = 0; i < data.length; i++) {
			if (rt.peek(programEndedAddr) > 0)
				break;
			rt.poke(addr++, data[i]);
			boolean newBlockAhead = addr % BLOCK_BYTESIZE == 0 && i < data.length - 1;
			if (newBlockAhead) {
				delayFileOperation(DELAYMILLIS_BINARY_LOAD_NEW_BLOCK);
			} else if (i % 8 == 7) {
				delayFileOperation(DELAYMILLIS_BINARY_LOAD_DWORD);
			}
		}
	}

	private void loadInBlocks(BinaryLoadCommand command, FileReference fileReference,
			StagedBasicProgramLoaderSession session) throws IOException {
		int programEndedAddr = session.getEndingMacro().getMemoryTrapAddress();
		BasicRuntime rt = session.getBasicRuntime();
		byte[] data = IOUtils.readBinaryFileContents(fileReference.getTargetFile());
		if (data.length > 0) {
			int blocks = 1 + (data.length - 1) / BLOCK_BYTESIZE;
			for (int bi = 0; bi < blocks; bi++) {
				int offset = bi * BLOCK_BYTESIZE;
				int end = Math.min(offset + BLOCK_BYTESIZE, data.length);
				delayFileOperation(DELAYMILLIS_BINARY_LOAD_NEW_BLOCK);
				if (rt.peek(programEndedAddr) > 0)
					break;
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
		protected BinaryLoadMacroHandler createMacroHandler(StagedCommandResolver resolver) {
			WaitResumeMacro macro = getSession().getMacroAdded(WaitResumeMacro.class);
			return new BinaryLoadMacroHandler(macro, getSourceCode(), getSession(), resolver);
		}

	}

	private class BinaryLoadMacroHandler extends FileCommandMacroHandler {

		public BinaryLoadMacroHandler(WaitResumeMacro macro, BasicSourceCode sourceCode,
				StagedBasicProgramLoaderSession session, StagedCommandResolver resolver) {
			super(macro, sourceCode, session, resolver);
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			handleBinaryLoad((BinaryLoadCommand) command, fileReference, getSourceCode(), getSession());
		}

	}

}