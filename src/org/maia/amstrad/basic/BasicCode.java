package org.maia.amstrad.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicCode implements Cloneable {

	protected BasicCode() {
	}

	public abstract BasicLanguage getLanguage();

	public boolean isEmpty() {
		return getLineCount() == 0;
	}

	public int getLineCount() {
		return getLineNumbers().size();
	}

	public int getSmallestLineNumber() {
		List<Integer> lineNumbers = getLineNumbers();
		if (lineNumbers.isEmpty()) {
			return 0;
		} else {
			return lineNumbers.get(0);
		}
	}

	public int getLargestLineNumber() {
		List<Integer> lineNumbers = getLineNumbers();
		if (lineNumbers.isEmpty()) {
			return 0;
		} else {
			return lineNumbers.get(lineNumbers.size() - 1);
		}
	}

	public boolean containsLineNumber(int lineNumber) {
		return getLineNumbers().contains(lineNumber);
	}

	public abstract List<Integer> getLineNumbers();

	public BasicLineNumberLinearMapping renum() throws BasicException {
		return renum(10, 10);
	}

	public BasicLineNumberLinearMapping renum(int lineNumberStart, int lineNumberStep) throws BasicException {
		List<Integer> lineNumbers = getLineNumbers();
		Map<Integer, Integer> mapping = new HashMap<Integer, Integer>(lineNumbers.size());
		for (int i = 0; i < lineNumbers.size(); i++) {
			int currentLineNumber = lineNumbers.get(i);
			int newLineNumber = lineNumberStart + i * lineNumberStep;
			mapping.put(currentLineNumber, newLineNumber);
		}
		BasicLineNumberLinearMapping lineNumberMapping = new BasicLineNumberLinearMappingImpl(mapping);
		if (!lineNumberMapping.isEmpty()) {
			renum(lineNumberMapping);
		}
		return lineNumberMapping;
	}

	public abstract void renum(BasicLineNumberLinearMapping mapping) throws BasicException;

	private static class BasicLineNumberLinearMappingImpl implements BasicLineNumberLinearMapping {

		private Map<Integer, Integer> mapping;

		public BasicLineNumberLinearMappingImpl(Map<Integer, Integer> mapping) {
			this.mapping = mapping;
		}

		@Override
		public boolean isEmpty() {
			return getMapping().isEmpty();
		}

		@Override
		public boolean isMapped(int oldLineNumber) {
			return getMapping().containsKey(oldLineNumber);
		}

		@Override
		public int getNewLineNumber(int oldLineNumber) {
			Integer result = getMapping().get(oldLineNumber);
			if (result != null) {
				return result.intValue();
			} else {
				return 0;
			}
		}

		private Map<Integer, Integer> getMapping() {
			return mapping;
		}

	}

}