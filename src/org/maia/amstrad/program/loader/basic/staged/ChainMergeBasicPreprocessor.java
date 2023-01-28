package org.maia.amstrad.program.loader.basic.staged;

import java.util.List;
import java.util.Vector;

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
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;

public class ChainMergeBasicPreprocessor extends StagedBasicPreprocessor {

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
		int ln1 = session.acquireFirstAvailablePreambleLineNumber();
		int ln2 = session.acquireFirstAvailablePreambleLineNumber();
		int ln3 = session.acquireFirstAvailablePreambleLineNumber();
		int ln4 = session.acquireFirstAvailablePreambleLineNumber();
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
		ChainMergeRuntimeListener listener = new ChainMergeRuntimeListener(session, addrTrap);
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
							int ref = listener.registerSourceInstruction(sequence.subSequence(i, j));
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
					i = sequence.getNextIndexOf(CHAIN, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
		session.getProgramRuntime().addListener(listener);
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

	private class ChainMergeRuntimeListener extends StagedBasicProgramRuntimeListener {

		private int memoryTrapAddress;

		private List<BasicSourceTokenSequence> sourceInstructions;

		public ChainMergeRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
			super(session);
			this.memoryTrapAddress = memoryTrapAddress;
			this.sourceInstructions = new Vector<BasicSourceTokenSequence>();
		}

		public int registerSourceInstruction(BasicSourceTokenSequence sequence) {
			getSourceInstructions().add(sequence);
			return getSourceInstructions().size();
		}

		@Override
		protected void stagedProgramIsRun() {
			ChainMergeMacro macro = getSession().getMacroAdded(ChainMergeMacro.class);
			addMemoryTrap(getMemoryTrapAddress(),
					new ChainMergeMacroHandler(macro, getSession(), new SourceInstructionResolver() {

						@Override
						public BasicSourceTokenSequence resolve(int referenceValue) {
							return getSourceInstructions().get(referenceValue - 1);
						}
					}));
		}

		@Override
		protected void stagedProgramIsDisposed(boolean programRemainsLoaded) {
			removeMemoryTrapsAt(getMemoryTrapAddress());
		}

		private int getMemoryTrapAddress() {
			return memoryTrapAddress;
		}

		private List<BasicSourceTokenSequence> getSourceInstructions() {
			return sourceInstructions;
		}

	}

	private static interface SourceInstructionResolver {

		BasicSourceTokenSequence resolve(int referenceValue);

	}

	private class ChainMergeMacroHandler extends StagedBasicMacroHandler implements AmstradMemoryTrapHandler {

		private SourceInstructionResolver resolver;

		public ChainMergeMacroHandler(ChainMergeMacro macro, StagedBasicProgramLoaderSession session,
				SourceInstructionResolver resolver) {
			super(macro, session);
			this.resolver = resolver;
		}

		@Override
		public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
			BasicSourceTokenSequence sequence = getResolver().resolve(memoryValue);
			System.out.println(sequence);
			// TODO parse sequence for filename etc.
			// TODO handle
		}

		private SourceInstructionResolver getResolver() {
			return resolver;
		}

	}

}