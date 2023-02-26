package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.load.basic.staged.ErrorOutCodes;
import org.maia.amstrad.load.basic.staged.StagedBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.file.WaitResumeBasicPreprocessor.WaitResumeMacro;
import org.maia.amstrad.util.AmstradUtils;

public abstract class FileCommandBasicPreprocessor extends StagedBasicPreprocessor
		implements ErrorOutCodes, FileCommandDelays {

	protected FileCommandBasicPreprocessor() {
	}

	protected BasicSourceTokenSequence createWaitResumeMacroInvocationSequence(StagedBasicProgramLoaderSession session,
			int macroHandlerMemoryAddress, int macroHandlerMemoryValue) throws BasicSyntaxException {
		return createGosubMacroInvocationSequence(session.getMacroAdded(WaitResumeMacro.class),
				macroHandlerMemoryAddress, macroHandlerMemoryValue);
	}

	protected BasicSourceTokenSequence createGosubMacroInvocationSequence(FileCommandMacro macro,
			int macroHandlerMemoryAddress, int macroHandlerMemoryValue) throws BasicSyntaxException {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		return createMacroHandlerInvocationSequence(macroHandlerMemoryAddress, macroHandlerMemoryValue).append(
				stf.createInstructionSeparator(), stf.createBasicKeyword("GOSUB"), stf.createLiteral(" "),
				stf.createLineNumberReference(macro.getLineNumberFrom()));
	}

	protected BasicSourceTokenSequence createGotoMacroInvocationSequence(FileCommandMacro macro,
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

	protected void delay(long delayMillis) {
		AmstradUtils.sleep(delayMillis);
	}

	protected void endWithError(int errorCode, BasicSourceCode sourceCode, FileCommandMacro macro,
			StagedBasicProgramLoaderSession session) {
		System.err.println("FileCommand ended with ERROR " + errorCode);
		try {
			substituteErrorCode(errorCode, sourceCode, session);
			addCodeLine(sourceCode, macro.getLineNumberTo(), "GOTO " + session.getErrorOutMacroLineNumber());
			resumeWithNewSourceCode(sourceCode, macro, session);
		} catch (BasicException e) {
			e.printStackTrace();
		}
	}

	protected void resumeWithNewSourceCode(BasicSourceCode newSourceCode, FileCommandMacro macro,
			StagedBasicProgramLoaderSession session) throws BasicException {
		session.getBasicRuntime().swap(newSourceCode);
		resumeRun(macro, session);
	}

	protected void resumeRun(FileCommandMacro macro, StagedBasicProgramLoaderSession session) {
		session.getBasicRuntime().poke(macro.getResumeMemoryAddress(), (byte) 1);
	}

}