package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.ErrorOutCodes;
import org.maia.amstrad.load.basic.staged.StagedBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public abstract class FileCommandBasicPreprocessor extends StagedBasicPreprocessor implements ErrorOutCodes {

	protected FileCommandBasicPreprocessor() {
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