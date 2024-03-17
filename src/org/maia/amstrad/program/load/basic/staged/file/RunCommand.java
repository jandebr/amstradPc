package org.maia.amstrad.program.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.NumericToken;

public class RunCommand extends FileCommand {

	private int startingLineNumber = -1;

	private RunCommand() {
	}

	public static RunCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		RunCommand command = null;
		if (!sequence.contains(LiteralQuotedToken.class)) { // not RUN "file" which is handled by ChainRun
			command = new RunCommand();
			// starting line
			int j = sequence.getFirstIndexOf(NumericToken.class);
			if (j >= 0) {
				command.setStartingLineNumber(((NumericToken) sequence.get(j)).getInt());
			}
		}
		return command;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RunCommand");
		if (hasStartingLineNumber()) {
			sb.append(" starting at ");
			sb.append(getStartingLineNumber());
		}
		return sb.toString();
	}

	public boolean hasStartingLineNumber() {
		return getStartingLineNumber() >= 0;
	}

	public int getStartingLineNumber() {
		return startingLineNumber;
	}

	private void setStartingLineNumber(int startingLineNumber) {
		this.startingLineNumber = startingLineNumber;
	}

}