package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.NumericToken;

public class ChainMergeCommand extends FileCommand {

	private int startingLineNumber = -1;

	private int deletionLineNumberFrom = -1;

	private int deletionLineNumberTo = -1;

	private ChainMergeCommand(LiteralQuotedToken filenameToken) {
		super(filenameToken);
	}

	public static ChainMergeCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		ChainMergeCommand command = null;
		int i = sequence.getFirstIndexOf(LiteralQuotedToken.class);
		if (i >= 0) {
			command = new ChainMergeCommand((LiteralQuotedToken) sequence.get(i));
			BasicKeywordToken DELETE = LocomotiveBasicSourceTokenFactory.getInstance().createBasicKeyword("DELETE");
			int di = sequence.getFirstIndexOf(DELETE);
			// starting line
			BasicSourceTokenSequence sub = sequence.subSequence(i + 1, di >= 0 ? di : sequence.size());
			int j = sub.getFirstIndexOf(NumericToken.class);
			if (j >= 0) {
				command.setStartingLineNumber(FileCommand.parseAsIntegerNumber(sub.get(j)));
			}
			// deletion
			if (di >= 0) {
				sub = sequence.subSequence(di + 1, sequence.size());
				j = sub.getFirstIndexOf(NumericToken.class);
				if (j >= 0) {
					command.setDeletionLineNumberFrom(FileCommand.parseAsIntegerNumber(sub.get(j)));
					if (command.getDeletionLineNumberFrom() >= 0) {
						int k = sub.getNextIndexOf(NumericToken.class, j + 1);
						if (k >= 0) {
							command.setDeletionLineNumberTo(FileCommand.parseAsIntegerNumber(sub.get(k)));
						}
						if (command.getDeletionLineNumberTo() < 0) {
							command.setDeletionLineNumberTo(command.getDeletionLineNumberFrom());
						}
					}
				}
			}
		}
		return command;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChainMergeCommand '").append(getSourceFilenameWithoutFlags()).append("'");
		if (hasStartingLineNumber()) {
			sb.append(" starting at ");
			sb.append(getStartingLineNumber());
		}
		if (hasDeletion()) {
			sb.append(" deleting between ");
			sb.append(getDeletionLineNumberFrom());
			sb.append(" and ");
			sb.append(getDeletionLineNumberTo());
		}
		return sb.toString();
	}

	public boolean hasStartingLineNumber() {
		return getStartingLineNumber() >= 0;
	}

	public boolean hasDeletion() {
		return getDeletionLineNumberFrom() >= 0;
	}

	public int getStartingLineNumber() {
		return startingLineNumber;
	}

	private void setStartingLineNumber(int startingLineNumber) {
		this.startingLineNumber = startingLineNumber;
	}

	public int getDeletionLineNumberFrom() {
		return deletionLineNumberFrom;
	}

	private void setDeletionLineNumberFrom(int deletionLineNumberFrom) {
		this.deletionLineNumberFrom = deletionLineNumberFrom;
	}

	public int getDeletionLineNumberTo() {
		return deletionLineNumberTo;
	}

	private void setDeletionLineNumberTo(int deletionLineNumberTo) {
		this.deletionLineNumberTo = deletionLineNumberTo;
	}

}