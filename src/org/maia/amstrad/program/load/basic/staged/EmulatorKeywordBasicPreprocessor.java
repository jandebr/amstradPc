package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;
import java.util.Vector;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.emulator.EmulatorBasicKeywords;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.program.load.basic.staged.WaitResumeBasicPreprocessor.WaitResumeMacro;

public class EmulatorKeywordBasicPreprocessor extends StagedBasicPreprocessor implements EmulatorBasicKeywords {

	private Collection<EmulatorKeywordReference> emulatorKeywordReferences;

	public EmulatorKeywordBasicPreprocessor() {
		this.emulatorKeywordReferences = new Vector<EmulatorKeywordReference>();
		loadEmulatorKeywordReferences();
	}

	private void loadEmulatorKeywordReferences() {
		int ref = 0;
		try {
			LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
			addEmulatorKeywordReference(
					new EmulatorKeywordReference(stf.createBasicKeyword(EmulatorBasicKeywords.REBOOT), ++ref));
		} catch (BasicSyntaxException e) {
			e.printStackTrace();
		}
	}

	private void addEmulatorKeywordReference(EmulatorKeywordReference emulatorKeywordReference) {
		getEmulatorKeywordReferences().add(emulatorKeywordReference);
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
		Collection<BasicKeywordToken> keywords = new Vector<BasicKeywordToken>(getEmulatorKeywordReferences().size());
		for (EmulatorKeywordReference reference : getEmulatorKeywordReferences()) {
			keywords.add(reference.getKeyword());
		}
		return keywords;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		int addrTrap = session.reserveMemory(1);
		EmulatorKeywordRuntimeListener listener = new EmulatorKeywordRuntimeListener(session, addrTrap);
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (EmulatorKeywordReference reference : getEmulatorKeywordReferences()) {
			invokeOnEmulatorKeyword(sourceCode, scope, reference, addrTrap, session);
		}
		listener.install();
	}

	private void invokeOnEmulatorKeyword(BasicSourceCode sourceCode, BasicLineNumberScope scope,
			EmulatorKeywordReference emulatorKeywordReference, int macroHandlerMemoryAddress,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicSourceToken SEP = createInstructionSeparatorToken(sourceCode.getLanguage());
		BasicKeywordToken keyword = emulatorKeywordReference.getKeyword();
		int ref = emulatorKeywordReference.getReferenceNumber();
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(keyword);
				while (i >= 0) {
					// keyword => waitresume macro
					int j = sequence.getNextIndexOf(SEP, i + 1);
					if (j < 0)
						j = sequence.size();
					sequence.replaceRange(i, j,
							createWaitResumeMacroInvocationSequence(session, macroHandlerMemoryAddress, ref));
					i = sequence.getNextIndexOf(keyword, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	protected void handleReboot(StagedBasicProgramLoaderSession session) {
		session.getAmstradPc().reboot(false, false);
		// no need to resume run
	}

	protected void resumeRun(StagedBasicProgramLoaderSession session) {
		WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
		resumeRun(macro, session);
	}

	private EmulatorKeywordReference findEmulatorKeywordReference(int referenceNumber) {
		for (EmulatorKeywordReference reference : getEmulatorKeywordReferences()) {
			if (reference.getReferenceNumber() == referenceNumber)
				return reference;
		}
		return null;
	}

	private Collection<EmulatorKeywordReference> getEmulatorKeywordReferences() {
		return emulatorKeywordReferences;
	}

	private static class EmulatorKeywordReference {

		private BasicKeywordToken keyword;

		private int referenceNumber;

		public EmulatorKeywordReference(BasicKeywordToken keyword, int referenceNumber) {
			this.keyword = keyword;
			this.referenceNumber = referenceNumber;
		}

		public BasicKeywordToken getKeyword() {
			return keyword;
		}

		public int getReferenceNumber() {
			return referenceNumber;
		}

	}

	private class EmulatorKeywordRuntimeListener extends StagedBasicProgramRuntimeListener {

		public EmulatorKeywordRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
			super(session, memoryTrapAddress);
		}

		@Override
		protected EmulatorKeywordMacroHandler createMemoryTrapHandler() {
			WaitResumeMacro macro = getSession().getMacroAdded(WaitResumeMacro.class);
			return new EmulatorKeywordMacroHandler(macro, getSession());
		}

	}

	private class EmulatorKeywordMacroHandler extends StagedBasicMacroHandler {

		public EmulatorKeywordMacroHandler(WaitResumeMacro macro, StagedBasicProgramLoaderSession session) {
			super(macro, session);
		}

		@Override
		public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
			EmulatorKeywordReference reference = findEmulatorKeywordReference(memoryValue);
			if (reference != null) {
				executeEmulatorKeyword(reference.getKeyword());
			}
		}

		private void executeEmulatorKeyword(BasicKeywordToken emulatorKeyword) {
			String keywordStr = emulatorKeyword.getSourceFragment();
			System.out.println("Handling emulator keyword " + keywordStr);
			if (EmulatorBasicKeywords.REBOOT.equals(keywordStr)) {
				handleReboot(getSession());
			}
		}

	}

}