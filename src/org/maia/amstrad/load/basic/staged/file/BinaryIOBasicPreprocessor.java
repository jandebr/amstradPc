package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.load.basic.staged.StagedBasicPreprocessor;

public abstract class BinaryIOBasicPreprocessor extends StagedBasicPreprocessor {

	protected BinaryIOBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 0;
	}

}