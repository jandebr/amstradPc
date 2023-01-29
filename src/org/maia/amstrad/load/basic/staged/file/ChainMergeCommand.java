package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;

public class ChainMergeCommand extends FileCommand {

	private ChainMergeCommand(String filename) {
		super(filename);
	}

	public static ChainMergeCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		// TODO
		return new ChainMergeCommand("");
	}

}