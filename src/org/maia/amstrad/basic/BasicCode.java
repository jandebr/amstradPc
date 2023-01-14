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
		return getAscendingLineNumbers().size();
	}

	public int getSmallestLineNumber() {
		List<Integer> lineNumbers = getAscendingLineNumbers();
		if (lineNumbers.isEmpty()) {
			return -1;
		} else {
			return lineNumbers.get(0);
		}
	}

	public int getLargestLineNumber() {
		List<Integer> lineNumbers = getAscendingLineNumbers();
		if (lineNumbers.isEmpty()) {
			return -1;
		} else {
			return lineNumbers.get(lineNumbers.size() - 1);
		}
	}

	public int getNextAvailableLineNumber() {
		return getNextAvailableLineNumber(1);
	}

	public int getNextAvailableLineNumber(int lineNumberStep) {
		if (isEmpty()) {
			return lineNumberStep;
		} else {
			return (getLargestLineNumber() / lineNumberStep + 1) * lineNumberStep;
		}
	}

	public boolean containsLineNumber(int lineNumber) {
		return getAscendingLineNumbers().contains(lineNumber);
	}

	public abstract List<Integer> getAscendingLineNumbers();

	public int getDominantLineNumberStep() {
		List<Integer> lineNumbers = getAscendingLineNumbers();
		if (lineNumbers.isEmpty()) {
			return 10;
		} else if (lineNumbers.size() == 1) {
			return lineNumbers.get(0);
		} else {
			Map<Integer, Integer> stepCount = new HashMap<Integer, Integer>();
			int winnerStep = 0;
			int winnerStepCount = 0;
			for (int i = 0; i < lineNumbers.size() - 1; i++) {
				int step = lineNumbers.get(i + 1) - lineNumbers.get(i);
				int count = stepCount.containsKey(step) ? 1 + stepCount.get(step) : 1;
				stepCount.put(step, count);
				if (count >= winnerStepCount) {
					winnerStepCount = count;
					winnerStep = step;
				}
			}
			return winnerStep;
		}
	}

	public BasicLineNumberLinearMapping renum() throws BasicException {
		return renum(10, 10);
	}

	public BasicLineNumberLinearMapping renum(int lineNumberStart, int lineNumberStep) throws BasicException {
		BasicLineNumberLinearMapping lineNumberMapping = createLineNumberMapping(lineNumberStart, lineNumberStep);
		if (!lineNumberMapping.isEmpty()) {
			renum(lineNumberMapping);
		}
		return lineNumberMapping;
	}

	public void renum(BasicLineNumberLinearMapping mapping) throws BasicException {
		renum(mapping, new BasicLineNumberScope() {

			@Override
			public boolean isInScope(int lineNumber) {
				return true;
			}
		});
	}

	public abstract void renum(BasicLineNumberLinearMapping mapping, BasicLineNumberScope scope) throws BasicException;

	private BasicLineNumberLinearMapping createLineNumberMapping(int lineNumberStart, int lineNumberStep) {
		List<Integer> lineNumbers = getAscendingLineNumbers();
		Map<Integer, Integer> mapping = new HashMap<Integer, Integer>(lineNumbers.size());
		for (int i = 0; i < lineNumbers.size(); i++) {
			int currentLineNumber = lineNumbers.get(i);
			int newLineNumber = lineNumberStart + i * lineNumberStep;
			mapping.put(currentLineNumber, newLineNumber);
		}
		return new BasicLineNumberLinearMappingImpl(mapping);
	}

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