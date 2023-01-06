package org.maia.amstrad.basic;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public abstract class BasicSourceCode extends BasicCode implements Iterable<BasicSourceCodeLine> {

	private List<BasicSourceCodeLine> lines; // ordered by increasing line number

	protected BasicSourceCode() {
		setLines(new Vector<BasicSourceCodeLine>(100));
	}

	protected BasicSourceCode(CharSequence sourceCode) throws BasicSyntaxException {
		setLines(parse(sourceCode));
		for (BasicSourceCodeLine line : this) {
			line.setParentSourceCode(this);
		}
	}

	protected abstract List<BasicSourceCodeLine> parse(CharSequence sourceCode) throws BasicSyntaxException;

	@Override
	public BasicSourceCode clone() {
		BasicSourceCode clone = null;
		try {
			clone = (BasicSourceCode) super.clone();
		} catch (CloneNotSupportedException e) {
			// never the case
		}
		clone.setLines(new Vector<BasicSourceCodeLine>(getLineCount()));
		for (BasicSourceCodeLine line : this) {
			try {
				clone.addLine(line.clone());
			} catch (BasicException e) {
				// never the case
			}
		}
		return clone;
	}

	public synchronized void clear() {
		if (!isEmpty()) {
			for (BasicSourceCodeLine line : this) {
				line.setParentSourceCode(null);
			}
			getLines().clear();
		}
	}

	@Override
	public synchronized int getLineCount() {
		return getLines().size();
	}

	@Override
	public synchronized int getSmallestLineNumber() {
		if (!isEmpty()) {
			return getLineByIndex(0).getLineNumber();
		} else {
			return 0;
		}
	}

	@Override
	public synchronized int getLargestLineNumber() {
		if (!isEmpty()) {
			return getLineByIndex(getLineCount() - 1).getLineNumber();
		} else {
			return 0;
		}
	}

	@Override
	public synchronized boolean containsLineNumber(int lineNumber) {
		return getLineByLineNumber(lineNumber) != null;
	}

	@Override
	public synchronized List<Integer> getLineNumbers() {
		if (isEmpty()) {
			return Collections.emptyList();
		} else {
			List<Integer> lineNumbers = new Vector<Integer>(getLineCount());
			for (BasicSourceCodeLine line : this) {
				lineNumbers.add(line.getLineNumber());
			}
			return lineNumbers;
		}
	}

	public synchronized BasicSourceCodeLine getLineByIndex(int index) {
		return getLines().get(index);
	}

	public synchronized BasicSourceCodeLine getLineByLineNumber(int lineNumber) {
		BasicSourceCodeLine line = null;
		int i = getLineNumberInsertionIndex(lineNumber);
		if (i < getLineCount()) {
			BasicSourceCodeLine candidate = getLineByIndex(i);
			if (candidate.getLineNumber() == lineNumber) {
				line = candidate;
			}
		}
		return line;
	}

	public synchronized void removeLineNumber(int lineNumber) {
		int i = getLineNumberInsertionIndex(lineNumber);
		if (i < getLineCount()) {
			BasicSourceCodeLine line = getLineByIndex(i);
			if (line.getLineNumber() == lineNumber) {
				line.setParentSourceCode(null);
				getLines().remove(i);
			}
		}
	}

	public synchronized void removeLineNumberRange(int lineNumberStart, int lineNumberEnd) {
		Iterator<BasicSourceCodeLine> it = getLines().iterator();
		while (it.hasNext()) {
			BasicSourceCodeLine line = it.next();
			int lineNumber = line.getLineNumber();
			if (lineNumber >= lineNumberStart && lineNumber <= lineNumberEnd) {
				line.setParentSourceCode(null);
				it.remove();
			}
		}
	}

	public synchronized void addLine(BasicSourceCodeLine line) throws BasicException {
		if (!line.getLanguage().equals(getLanguage()))
			throw new BasicException("Basic language mismatch");
		int lineNumber = line.getLineNumber();
		int i = getLineNumberInsertionIndex(lineNumber);
		if (i == getLineCount()) {
			getLines().add(line);
		} else {
			BasicSourceCodeLine otherLine = getLineByIndex(i);
			if (otherLine.getLineNumber() == lineNumber) {
				otherLine.setParentSourceCode(null);
				getLines().remove(i);
			}
			getLines().add(i, line);
		}
		line.setParentSourceCode(this);
	}

	public synchronized void merge(BasicSourceCode sourceCodeToMerge) throws BasicException {
		for (BasicSourceCodeLine line : sourceCodeToMerge) {
			addLine(line.clone());
		}
	}

	private int getLineNumberInsertionIndex(int lineNumber) {
		List<BasicSourceCodeLine> lines = getLines();
		int j = lines.size() - 1;
		if (j < 0)
			return 0;
		if (lineNumber <= lines.get(0).getLineNumber())
			return 0;
		if (lineNumber == lines.get(j).getLineNumber())
			return j;
		if (lineNumber > lines.get(j).getLineNumber())
			return j + 1;
		int i = 0;
		while (i <= j) {
			int k = (i + j) / 2;
			int ln = lines.get(k).getLineNumber();
			if (ln < lineNumber) {
				i = k + 1;
			} else if (ln > lineNumber) {
				j = k - 1;
			} else {
				return k;
			}
		}
		return i;
	}

	public synchronized String getText() {
		StringBuilder sb = new StringBuilder(40 * getLineCount());
		for (BasicSourceCodeLine line : this) {
			if (sb.length() > 0)
				sb.append('\n');
			sb.append(line.getText());
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return getText();
	}

	public synchronized String toStringInParsedForm() throws BasicSyntaxException {
		StringBuilder sb = new StringBuilder(256 * getLineCount());
		for (BasicSourceCodeLine line : this) {
			if (sb.length() > 0)
				sb.append('\n');
			sb.append(line.toStringInParsedForm());
		}
		return sb.toString();
	}

	@Override
	public synchronized Iterator<BasicSourceCodeLine> iterator() {
		return Collections.unmodifiableList(getLines()).iterator();
	}

	private List<BasicSourceCodeLine> getLines() {
		return lines;
	}

	private void setLines(List<BasicSourceCodeLine> lines) {
		this.lines = lines;
	}

}