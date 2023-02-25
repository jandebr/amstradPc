package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;

public class CloseoutCommand extends FileCommand {

	private CloseoutCommand() {
	}

	public static CloseoutCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		return new CloseoutCommand();
	}

	@Override
	public String toString() {
		return "CloseoutCommand";
	}

}