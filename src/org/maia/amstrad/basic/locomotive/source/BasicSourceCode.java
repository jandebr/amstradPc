package org.maia.amstrad.basic.locomotive.source;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.util.StringUtils;

public class BasicSourceCode implements Iterable<BasicSourceCodeLine> {

	private List<BasicSourceCodeLine> lines; // ordered by increasing line number

	public BasicSourceCode() {
		setLines(new Vector<BasicSourceCodeLine>(100));
	}

	public BasicSourceCode(CharSequence sourceCode) throws BasicSyntaxException {
		this();
		load(sourceCode);
	}

	public synchronized void load(CharSequence sourceCode) throws BasicSyntaxException {
		clear(); // disassociating any previous code lines
		List<BasicSourceCodeLine> lines = new Vector<BasicSourceCodeLine>(100);
		boolean strictlyIncreasingLineNumbers = true;
		StringTokenizer st = new StringTokenizer(sourceCode.toString(), "\n\r");
		while (st.hasMoreTokens()) {
			String text = st.nextToken();
			if (!StringUtils.isBlank(text)) {
				BasicSourceCodeLine line = new BasicSourceCodeLine(text);
				line.setParentSourceCode(this);
				lines.add(line);
				if (strictlyIncreasingLineNumbers && lines.size() > 1) {
					strictlyIncreasingLineNumbers = line.compareTo(lines.get(lines.size() - 2)) > 0;
				}
			}
		}
		if (!strictlyIncreasingLineNumbers) {
			Map<Integer, BasicSourceCodeLine> indexedLines = new HashMap<Integer, BasicSourceCodeLine>(lines.size());
			for (BasicSourceCodeLine line : lines) {
				BasicSourceCodeLine overwrittenLine = indexedLines.put(line.getLineNumber(), line);
				if (overwrittenLine != null) {
					overwrittenLine.setParentSourceCode(null);
				}
			}
			lines.clear();
			lines.addAll(indexedLines.values());
			Collections.sort(lines); // by increasing line number
		}
		setLines(lines);
	}

	public synchronized void clear() {
		if (!isEmpty()) {
			for (BasicSourceCodeLine line : getLines()) {
				line.setParentSourceCode(null);
			}
			getLines().clear();
		}
	}

	public synchronized int getLineCount() {
		return getLines().size();
	}

	public synchronized boolean isEmpty() {
		return getLineCount() == 0;
	}

	public synchronized int getSmallestLineNumber() {
		if (!isEmpty()) {
			return getLineByIndex(0).getLineNumber();
		} else {
			return 0;
		}
	}

	public synchronized int getLargestLineNumber() {
		if (!isEmpty()) {
			return getLineByIndex(getLineCount() - 1).getLineNumber();
		} else {
			return 0;
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

	public synchronized boolean containsLineNumber(int lineNumber) {
		return getLineByLineNumber(lineNumber) != null;
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

	public synchronized void addLine(BasicSourceCodeLine line) {
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

	private int getLineNumberInsertionIndex(int lineNumber) {
		List<BasicSourceCodeLine> lines = getLines();
		int j = lines.size() - 1;
		if (j < 0)
			return 0;
		if (lineNumber <= lines.get(0).getLineNumber())
			return 0;
		if (lineNumber >= lines.get(j).getLineNumber())
			return j;
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
		for (BasicSourceCodeLine line : getLines()) {
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

	public synchronized String toStringInParsedForm() {
		StringBuilder sb = new StringBuilder(256 * getLineCount());
		for (BasicSourceCodeLine line : getLines()) {
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