package org.maia.amstrad.load.basic.staged.file;

import java.io.File;
import java.util.Iterator;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicMemoryFullException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.load.basic.BasicPreprocessor;
import org.maia.amstrad.load.basic.BasicPreprocessorBatch;
import org.maia.amstrad.load.basic.staged.CompactBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.InterruptBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.ProgramBridgeBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.ProgramBridgeBasicPreprocessor.ProgramBridgeMacro;
import org.maia.amstrad.load.basic.staged.StagedBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.StagedLineNumberMapping;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.AmstradProgramException;

public class ChainMergeBasicPreprocessor extends FileCommandBasicPreprocessor {

	public ChainMergeBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 2; // for chainmerge macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
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
		addCodeLine(sourceCode, ln2, "POKE &" + Integer.toHexString(addrResume) + ",0:RESTORE:GOTO 0"
				+ (session.produceRemarks() ? ":REM @chainmerge" : ""));
		session.addMacro(new ChainMergeMacro(new BasicLineNumberRange(ln1, ln2), addrResume));
	}

	private void invokeChainMergeMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		ChainMergeMacro macro = session.getMacroAdded(ChainMergeMacro.class);
		int addrTrap = session.reserveMemory(1);
		ChainMergeRuntimeListener listener = new ChainMergeRuntimeListener(sourceCode, session, addrTrap);
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken CHAIN = createKeywordToken(language, "CHAIN");
		BasicSourceToken MERGE = createKeywordToken(language, "MERGE");
		BasicSourceToken SEP = createInstructionSeparatorToken(language);
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
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
								sequence.replaceRange(i, j, createGotoMacroInvocationSequence(macro, addrTrap, ref));
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
			BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) {
		System.out.println("Handling " + command);
		ChainMergeMacro macro = session.getMacroAdded(ChainMergeMacro.class);
		if (chainedProgram == null) {
			endWithError(ERR_FILE_NOT_FOUND, sourceCode, macro, session);
		} else {
			BasicSourceCode sourceCodeBeforeMerge = sourceCode.clone();
			try {
				if (!isProgramAlreadyChained(chainedProgram, session)) {
					delay(DELAYMILLIS_CHAIN_MERGE);
					performChainMerge(command, chainedProgram, sourceCode, session);
				}
				resumeWithNewSourceCode(getResumeLineNumber(command, sourceCode), sourceCode, session);
				System.out.println("Completed " + command);
			} catch (BasicMemoryFullException e) {
				endWithError(ERR_MEMORY_FULL, sourceCodeBeforeMerge, macro, session);
			} catch (Exception e) {
				System.err.println(e);
				endWithError(ERR_CHAIN_MERGE_FAILURE, sourceCodeBeforeMerge, macro, session);
			}
		}
	}

	private void performChainMerge(ChainMergeCommand command, AmstradProgram chainedProgram, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) throws BasicException, AmstradProgramException {
		int lnChainedOffset = getNextAvailableLineNumber(sourceCode);
		StagedLineNumberMapping stagedMapping = session.getOriginalToStagedLineNumberMapping();
		// Preprocess chained code
		BasicSourceCode chainedSourceCode = session.getLoader().retrieveSourceCode(chainedProgram);
		StagedBasicProgramLoaderSession chainedSession = session.createNewSession();
		chainedSession.setOriginalToStagedVariableMapping(session.getOriginalToStagedVariableMapping());
		BasicSourceTokenSequence interruptSequence = InterruptBasicPreprocessor.extractInterruptSequence(sourceCode,
				session);
		preprocessChainedSourceCode(chainedSourceCode, lnChainedOffset, chainedSession, interruptSequence,
				stagedMapping);
		// Bridge programs in current code
		bridgePrograms(sourceCode, lnChainedOffset, session);
		// Merge
		removeDeletionLines(command, sourceCode);
		sourceCode.merge(chainedSourceCode);
		session.addMacrosFrom(chainedSession);
		session.addProgramToChain(chainedProgram);
		// Preprocess the entire merged code
		getPreprocessorsForMergedCode(session).preprocess(sourceCode, session);
	}

	private void preprocessChainedSourceCode(BasicSourceCode chainedSourceCode, int chainedLineNumberOffset,
			StagedBasicProgramLoaderSession chainedSession, BasicSourceTokenSequence interruptSequence,
			StagedLineNumberMapping stagedMapping) throws BasicException {
		new CompactBasicPreprocessor().preprocess(chainedSourceCode, chainedSession);
		new ProgramBridgeBasicPreprocessor().preprocess(chainedSourceCode, chainedSession);
		new ChainedInterruptBasicPreprocessor(interruptSequence).preprocess(chainedSourceCode, chainedSession);
		BasicLineNumberScope chainedCodeScope = chainedSession.getSnapshotScopeOfCodeExcludingMacros(chainedSourceCode); // before
																															// renum
		BasicLineNumberLinearMapping mapping = renum(chainedSourceCode, chainedLineNumberOffset, 1, chainedSession);
		stagedMapping.union(mapping, chainedCodeScope);
	}

	private void bridgePrograms(BasicSourceCode sourceCode, int chainedLineNumberOffset,
			StagedBasicProgramLoaderSession session) throws BasicException {
		ProgramBridgeMacro bridgeMacro = session.getMacroAdded(ProgramBridgeMacro.class);
		substituteLineNumberReference(bridgeMacro.getLineNumberFrom(), chainedLineNumberOffset, sourceCode);
		session.removeMacro(bridgeMacro);
	}

	private void removeDeletionLines(ChainMergeCommand command, BasicSourceCode sourceCode) {
		if (command.hasDeletion()) {
			int lnFrom = command.getDeletionLineNumberFrom();
			int lnTo = command.getDeletionLineNumberTo();
			sourceCode.removeLineNumberRange(lnFrom, lnTo);
		}
	}

	private BasicPreprocessorBatch getPreprocessorsForMergedCode(StagedBasicProgramLoaderSession session) {
		BasicPreprocessorBatch batch = new BasicPreprocessorBatch();
		Iterator<BasicPreprocessor> it = session.getLoader().getPreprocessors();
		while (it.hasNext()) {
			BasicPreprocessor preprocessor = it.next();
			if (preprocessor instanceof StagedBasicPreprocessor) {
				if (((StagedBasicPreprocessor) preprocessor).isApplicableToMergedCode()) {
					batch.add(preprocessor);
				}
			}
		}
		return batch;
	}

	private void resumeWithNewSourceCode(int resumeLineNumber, BasicSourceCode newSourceCode,
			StagedBasicProgramLoaderSession session) throws BasicException {
		ChainMergeMacro macro = session.getMacroAdded(ChainMergeMacro.class);
		substituteLineNumberReference(macro.getLineNumberTo(), resumeLineNumber, newSourceCode);
		resumeWithNewSourceCode(newSourceCode, macro, session);
	}

	private int getResumeLineNumber(ChainMergeCommand command, BasicSourceCode sourceCode) {
		if (command.hasStartingLineNumber()) {
			return command.getStartingLineNumber();
		} else {
			return sourceCode.getSmallestLineNumber();
		}
	}

	private boolean isProgramAlreadyChained(AmstradProgram chainedProgram, StagedBasicProgramLoaderSession session) {
		if (chainedProgram instanceof AmstradBasicProgramFile) {
			File chainedFile = ((AmstradBasicProgramFile) chainedProgram).getFile();
			for (AmstradProgram program : session.getChainedPrograms()) {
				if (program instanceof AmstradBasicProgramFile) {
					File file = ((AmstradBasicProgramFile) program).getFile();
					if (file.equals(chainedFile))
						return true;
				}
			}
		}
		return false;
	}

	public static class ChainMergeMacro extends FileCommandMacro {

		public ChainMergeMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

	private class ChainMergeRuntimeListener extends FileCommandRuntimeListener {

		public ChainMergeRuntimeListener(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
				int memoryTrapAddress) {
			super(sourceCode, session, memoryTrapAddress);
		}

		@Override
		protected ChainMergeMacroHandler createMacroHandler(FileCommandResolver resolver) {
			ChainMergeMacro macro = getSession().getMacroAdded(ChainMergeMacro.class);
			return new ChainMergeMacroHandler(macro, getSourceCode(), getSession(), resolver);
		}

	}

	private class ChainMergeMacroHandler extends FileCommandMacroHandler {

		public ChainMergeMacroHandler(ChainMergeMacro macro, BasicSourceCode sourceCode,
				StagedBasicProgramLoaderSession session, FileCommandResolver resolver) {
			super(macro, sourceCode, session, resolver);
		}

		@Override
		protected void execute(FileCommand command, FileReference fileReference) {
			AmstradProgram chainedProgram = getReferencedProgram(fileReference);
			handleChainMerge((ChainMergeCommand) command, chainedProgram, getSourceCode(), getSession());
		}

	}

	private static class ChainedInterruptBasicPreprocessor extends InterruptBasicPreprocessor {

		private BasicSourceTokenSequence interruptSequence;

		public ChainedInterruptBasicPreprocessor(BasicSourceTokenSequence interruptSequence) {
			this.interruptSequence = interruptSequence;
		}

		@Override
		protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
				throws BasicException {
			repeatInterruptMacroAfterClear(sourceCode, session);
		}

		@Override
		protected BasicSourceTokenSequence getInterruptSequence(BasicSourceCode sourceCode,
				StagedBasicProgramLoaderSession session) throws BasicException {
			return getInterruptSequence();
		}

		private BasicSourceTokenSequence getInterruptSequence() {
			return interruptSequence;
		}

	}

}