package org.maia.amstrad.program.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;

public class OpenoutCommand extends FileCommand {

	private OpenoutCommand(LiteralQuotedToken filenameToken) {
		super(filenameToken);
	}

	public static OpenoutCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		OpenoutCommand command = null;
		int i = sequence.getFirstIndexOf(LiteralQuotedToken.class);
		if (i >= 0) {
			command = new OpenoutCommand((LiteralQuotedToken) sequence.get(i));
		}
		return command;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OpenoutCommand '").append(getSourceFilenameWithoutFlags()).append("'");
		return sb.toString();
	}

}