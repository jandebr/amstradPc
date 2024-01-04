package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.Integer8BitDecimalToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.program.load.AmstradProgramLoaderSession;
import org.maia.amstrad.program.load.basic.BasicPreprocessor;
import org.maia.amstrad.program.load.basic.staged.WaitResumeBasicPreprocessor.WaitResumeMacro;
import org.maia.util.SystemUtils;

public abstract class StagedBasicPreprocessor extends BasicPreprocessor {

	private static final long DELAYMILLIS_ENTER_MACRO_WAIT_LOOP = 100L;

	protected StagedBasicPreprocessor() {
	}

	public abstract int getDesiredPreambleLineCount();

	public abstract boolean isApplicableToMergedCode();

	public abstract Collection<BasicKeywordToken> getKeywordsActedOn();

	@Override
	public final void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
			throws BasicException {
		stage(sourceCode, (StagedBasicProgramLoaderSession) session);
	}

	protected abstract void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException;

	protected BasicLineNumberLinearMapping renum(BasicSourceCode sourceCode, int lineNumberStart, int lineNumberStep,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicLineNumberLinearMapping mapping = sourceCode.renum(lineNumberStart, lineNumberStep);
		session.renumMacros(mapping); // keep line numbers in sync
		return mapping;
	}

	protected boolean originalCodeContainsKeyword(BasicSourceCode sourceCode, String keyword,
			StagedBasicProgramLoaderSession session) throws BasicException {
		return codeContainsKeyword(sourceCode, session.getSnapshotScopeOfCodeExcludingMacros(sourceCode), keyword);
	}

	protected void substituteErrorCode(int errorCode, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicSourceTokenSequence sequence = sourceCode.getLineByLineNumber(session.getErrorOutMacroLineNumber())
				.parse();
		BasicSourceToken ERROR = createKeywordToken(sourceCode.getLanguage(), "ERROR");
		int i = sequence.getFirstIndexOf(ERROR);
		if (i >= 0) {
			sequence.replace(i + 2, new Integer8BitDecimalToken(errorCode));
			addCodeLine(sourceCode, sequence);
		}
	}

	protected void substituteLineNumberReference(int lineNumber, int lineNumberReference, BasicSourceCode sourceCode)
			throws BasicException {
		BasicSourceTokenSequence sequence = sourceCode.getLineByLineNumber(lineNumber).parse();
		int i = sequence.getFirstIndexOf(LineNumberReferenceToken.class);
		if (i >= 0) {
			sequence.replace(i, new LineNumberReferenceToken(lineNumberReference));
			addCodeLine(sourceCode, sequence);
		}
	}

	protected BasicSourceTokenSequence createWaitResumeMacroInvocationSequence(StagedBasicProgramLoaderSession session,
			int macroHandlerMemoryAddress, int macroHandlerMemoryValue) throws BasicSyntaxException {
		return createGosubMacroInvocationSequence(session.getMacroAdded(WaitResumeMacro.class),
				macroHandlerMemoryAddress, macroHandlerMemoryValue);
	}

	protected BasicSourceTokenSequence createGosubMacroInvocationSequence(StagedBasicMacro macro,
			int macroHandlerMemoryAddress, int macroHandlerMemoryValue) throws BasicSyntaxException {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		return createMacroHandlerInvocationSequence(macroHandlerMemoryAddress, macroHandlerMemoryValue).append(
				stf.createInstructionSeparator(), stf.createBasicKeyword("GOSUB"), stf.createLiteral(" "),
				stf.createLineNumberReference(macro.getLineNumberFrom()));
	}

	protected BasicSourceTokenSequence createGotoMacroInvocationSequence(StagedBasicMacro macro,
			int macroHandlerMemoryAddress, int macroHandlerMemoryValue) throws BasicSyntaxException {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		return createMacroHandlerInvocationSequence(macroHandlerMemoryAddress, macroHandlerMemoryValue).append(
				stf.createInstructionSeparator(), stf.createBasicKeyword("GOTO"), stf.createLiteral(" "),
				stf.createLineNumberReference(macro.getLineNumberFrom()));
	}

	private BasicSourceTokenSequence createMacroHandlerInvocationSequence(int macroHandlerMemoryAddress,
			int macroHandlerMemoryValue) throws BasicSyntaxException {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		return new BasicSourceTokenSequence().append(stf.createBasicKeyword("POKE"), stf.createLiteral(" "),
				stf.createPositiveInteger16BitHexadecimal(macroHandlerMemoryAddress), stf.createLiteral(","),
				stf.createPositiveInteger8BitDecimal(macroHandlerMemoryValue));
	}

	protected void endWithError(int errorCode, BasicSourceCode sourceCode, WaitResumeMacro macro,
			StagedBasicProgramLoaderSession session) {
		System.err.println("Staged Basic program ended with ERROR " + errorCode);
		try {
			substituteErrorCode(errorCode, sourceCode, session);
			addCodeLine(sourceCode, macro.getLineNumberTo(), "GOTO " + session.getErrorOutMacroLineNumber());
			waitUntilBasicInterpreterInWaitLoop(); // save to swap code
			resumeWithNewSourceCode(sourceCode, macro, session);
		} catch (BasicException e) {
			e.printStackTrace();
		}
	}

	protected void waitUntilBasicInterpreterInWaitLoop() {
		SystemUtils.sleep(DELAYMILLIS_ENTER_MACRO_WAIT_LOOP);
	}

	protected void resumeWithNewSourceCode(BasicSourceCode newSourceCode, WaitResumeMacro macro,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicByteCode newByteCode = prepareCodeForSwapping(newSourceCode, session);
		session.getBasicRuntime().swap(newByteCode);
		resumeRun(macro, session);
	}

	protected void resumeRun(WaitResumeMacro macro, StagedBasicProgramLoaderSession session) {
		session.getBasicRuntime().poke(macro.getResumeMemoryAddress(), (byte) 1);
	}

	private BasicByteCode prepareCodeForSwapping(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		BasicByteCode byteCode = session.getBasicRuntime().getCompiler().compile(sourceCode);
		if (byteCode instanceof LocomotiveBasicByteCode) {
			// Keep the macro code bitwise identical so there can be no issues with the running Basic interpreter
			((LocomotiveBasicByteCode) byteCode).updateLineReferencesToPointers();
		}
		return byteCode;
	}

}