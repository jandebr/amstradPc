package org.maia.amstrad.basic;

import java.util.HashMap;
import java.util.Map;

public class BasicLineNumberLinearMappingImpl implements BasicLineNumberLinearMapping {

	private Map<Integer, Integer> mapping;

	public BasicLineNumberLinearMappingImpl() {
		this.mapping = new HashMap<Integer, Integer>(100);
	}

	public void addMapping(int oldLineNumber, int newLineNumber) {
		getMapping().put(oldLineNumber, newLineNumber);
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
			return -1;
		}
	}

	private Map<Integer, Integer> getMapping() {
		return mapping;
	}

}