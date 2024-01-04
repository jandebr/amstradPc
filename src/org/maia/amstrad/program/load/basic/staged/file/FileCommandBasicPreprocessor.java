package org.maia.amstrad.program.load.basic.staged.file;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace;
import org.maia.amstrad.pc.tape.AmstradTape;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.load.basic.staged.ErrorOutCodes;
import org.maia.amstrad.program.load.basic.staged.StagedBasicPreprocessor;
import org.maia.amstrad.program.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.util.SystemUtils;

public abstract class FileCommandBasicPreprocessor extends StagedBasicPreprocessor
		implements ErrorOutCodes, FileCommandDelays {

	private static final String SETTING_DELAYS = "basic_staging.delayFileOperations";

	protected FileCommandBasicPreprocessor() {
	}

	protected LocomotiveBasicVariableSpace getRuntimeVariables(StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (session.getBasicRuntime() instanceof LocomotiveBasicRuntime) {
			return ((LocomotiveBasicRuntime) session.getBasicRuntime()).getVariableSpace();
		} else {
			throw new BasicException("Cannot retrieve Basic runtime variables");
		}
	}

	protected void startFileOperation(StagedBasicProgramLoaderSession session, FileReference fileReference,
			boolean write, boolean suppressMessages) {
		AmstradTape tape = session.getAmstradPc().getTape();
		String filename = fileReference.getSourceFilename();
		if (write) {
			tape.notifyTapeWriting(filename, suppressMessages);
		} else {
			tape.notifyTapeReading(filename, suppressMessages);
		}
	}

	protected void stopFileOperation(StagedBasicProgramLoaderSession session) {
		AmstradTape tape = session.getAmstradPc().getTape();
		if (tape.isWriting()) {
			tape.notifyTapeStoppedWriting();
		} else if (tape.isReading()) {
			tape.notifyTapeStoppedReading();
		}
	}

	protected void delayFileOperation(long delayMillis) {
		AmstradSettings settings = AmstradFactory.getInstance().getAmstradContext().getUserSettings();
		if (settings.getBool(SETTING_DELAYS, true)) {
			SystemUtils.sleep(delayMillis);
		}
	}

}