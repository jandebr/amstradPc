package org.maia.amstrad.program.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;

public class OpeninCommand extends FileCommand {

	private OpeninCommand(LiteralQuotedToken filenameToken) {
		super(filenameToken);
	}

	public static OpeninCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		OpeninCommand command = null;
		int i = sequence.getFirstIndexOf(LiteralQuotedToken.class);
		if (i >= 0) {
			command = new OpeninCommand((LiteralQuotedToken) sequence.get(i));
		}
		return command;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OpeninCommand '").append(getSourceFilenameWithoutFlags()).append("'");
		return sb.toString();
	}

}