package org.maia.amstrad.load.basic.staged;

import java.util.Iterator;

import org.maia.amstrad.load.basic.BasicPreprocessingProgramLoader;
import org.maia.amstrad.load.basic.BasicPreprocessor;
import org.maia.amstrad.load.basic.staged.file.BinaryLoadBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.file.BinarySaveBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.file.ChainBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.file.ChainMergeBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.file.RunBasicPreprocessor;
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
		addPreprocessor(new LinkResolveBasicPreprocessor());
		addPreprocessor(new ChainMergeBasicPreprocessor());
		addPreprocessor(new ChainBasicPreprocessor());
		addPreprocessor(new RunBasicPreprocessor());
		addPreprocessor(new BinaryLoadBasicPreprocessor());
		addPreprocessor(new BinarySaveBasicPreprocessor());
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
		Iterator<BasicPreprocessor> it = getPreprocessors();
		while (it.hasNext()) {
			BasicPreprocessor preprocessor = it.next();
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