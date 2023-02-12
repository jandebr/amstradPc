package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.NumericToken;

public class BinaryLoadCommand extends FileCommand {

	private int memoryOffset;

	private BinaryLoadCommand(LiteralQuotedToken filenameToken) {
		super(filenameToken);
	}

	public static BinaryLoadCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		BinaryLoadCommand command = null;
		int i = sequence.getFirstIndexOf(LiteralQuotedToken.class);
		if (i >= 0) {
			LiteralQuotedToken filenameToken = (LiteralQuotedToken) sequence.get(i);
			int j = sequence.getNextIndexOf(NumericToken.class, i + 1);
			if (j >= 0) {
				command = new BinaryLoadCommand(filenameToken);
				command.setMemoryOffset(((NumericToken) sequence.get(j)).getInt());
			}
		}
		return command;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BinaryLoadCommand '").append(getSourceFilenameWithoutFlags()).append("'");
		sb.append(" with offset ");
		sb.append(getMemoryOffset());
		return sb.toString();
	}
	
	public int getMemoryOffset() {
		return memoryOffset;
	}

	private void setMemoryOffset(int memoryOffset) {
		this.memoryOffset = memoryOffset;
	}

}