package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.load.basic.BasicPreprocessingProgramLoader;
import org.maia.amstrad.load.basic.BasicPreprocessor;
import org.maia.amstrad.load.basic.staged.file.ChainMergeBasicPreprocessor;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgramRuntime;

public class StagedBasicProgramLoader extends BasicPreprocessingProgramLoader {

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	private boolean produceRemarks;

	public StagedBasicProgramLoader(AmstradPc amstradPc, EndingBasicAction endingAction,
			EndingBasicCodeDisclosure codeDisclosure, boolean produceRemarks) {
		super(amstradPc);
		this.endingAction = endingAction;
		this.codeDisclosure = codeDisclosure;
		this.produceRemarks = produceRemarks;
		setupPreprocessors();
	}

	protected void setupPreprocessors() {
		// The order is crucial
		PreambleBasicPreprocessor preamble = new PreambleBasicPreprocessor();
		addPreprocessor(new ProgramBridgeBasicPreprocessor());
		addPreprocessor(preamble);
		addPreprocessor(new PreambleLandingBasicPreprocessor());
		addPreprocessor(new ChainMergeBasicPreprocessor());
		addPreprocessor(new EndingBasicPreprocessor());
		addPreprocessor(new ErrorOutBasicPreprocessor());
		addPreprocessor(new PreambleJumpingBasicPreprocessor());
		addPreprocessor(new InterruptBasicPreprocessor());
		addPreprocessor(new HimemBasicPreprocessor(16));
		// Number of preamble lines needed
		preamble.setPreambleLineCount(getDesiredPreambleLineCount());
	}

	protected int getDesiredPreambleLineCount() {
		int lineCount = 0;
		for (BasicPreprocessor preprocessor : getPreprocessorBatch()) {
			if (preprocessor instanceof StagedBasicPreprocessor) {
				lineCount += ((StagedBasicPreprocessor) preprocessor).getDesiredPreambleLineCount();
			}
		}
		return lineCount;
	}

	@Override
	protected StagedBasicProgramLoaderSession createLoaderSession(AmstradProgramRuntime programRuntime) {
		StagedBasicProgramLoaderSession session = new StagedBasicProgramLoaderSession(this, programRuntime);
		session.setEndingAction(getEndingAction());
		session.setCodeDisclosure(getCodeDisclosure());
		session.setProduceRemarks(produceRemarks());
		return session;
	}

	private EndingBasicAction getEndingAction() {
		return endingAction;
	}

	private EndingBasicCodeDisclosure getCodeDisclosure() {
		return codeDisclosure;
	}

	private boolean produceRemarks() {
		return produceRemarks;
	}

}