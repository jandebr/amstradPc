package org.maia.amstrad.basic.locomotive;

import java.util.HashMap;
import java.util.Map;

public class LocomotiveBasicOperators {

	private Map<Integer, LocomotiveBasicOperator> byteCodeMap;

	private Map<String, LocomotiveBasicOperator> sourceFormMap;

	private static LocomotiveBasicOperators instance;

	public static LocomotiveBasicOperators getInstance() {
		if (instance == null) {
			setInstance(new LocomotiveBasicOperators());
		}
		return instance;
	}

	private static synchronized void setInstance(LocomotiveBasicOperators operators) {
		if (instance == null) {
			instance = operators;
		}
	}

	private LocomotiveBasicOperators() {
		this.byteCodeMap = new HashMap<Integer, LocomotiveBasicOperator>(20);
		this.sourceFormMap = new HashMap<String, LocomotiveBasicOperator>(20);
		loadOperators();
	}

	private void loadOperators() {
		register(new LocomotiveBasicOperator((byte) 0xee, ">"));
		register(new LocomotiveBasicOperator((byte) 0xef, "="));
		register(new LocomotiveBasicOperator((byte) 0xf0, ">="));
		register(new LocomotiveBasicOperator((byte) 0xf1, "<"));
		register(new LocomotiveBasicOperator((byte) 0xf2, "<>"));
		register(new LocomotiveBasicOperator((byte) 0xf3, "<="));
		register(new LocomotiveBasicOperator((byte) 0xf4, "+"));
		register(new LocomotiveBasicOperator((byte) 0xf5, "-"));
		register(new LocomotiveBasicOperator((byte) 0xf6, "*"));
		register(new LocomotiveBasicOperator((byte) 0xf7, "/"));
		register(new LocomotiveBasicOperator((byte) 0xf8, "^"));
		register(new LocomotiveBasicOperator((byte) 0xf9, "\\"));
		register(new LocomotiveBasicOperator((byte) 0xfa, "AND"));
		register(new LocomotiveBasicOperator((byte) 0xfb, "MOD"));
		register(new LocomotiveBasicOperator((byte) 0xfc, "OR"));
		register(new LocomotiveBasicOperator((byte) 0xfd, "XOR"));
		register(new LocomotiveBasicOperator((byte) 0xfe, "NOT"));
	}

	private void register(LocomotiveBasicOperator operator) {
		getByteCodeMap().put(getOperatorIndex(operator), operator);
		getSourceFormMap().put(operator.getSourceForm(), operator);
	}

	public boolean hasOperator(String sourceForm) {
		return getSourceFormMap().containsKey(sourceForm.toUpperCase());
	}

	public LocomotiveBasicOperator getOperator(String sourceForm) {
		return getSourceFormMap().get(sourceForm.toUpperCase());
	}

	public LocomotiveBasicOperator getOperator(byte codeByte) {
		return getByteCodeMap().get(getOperatorIndex(codeByte));
	}

	private Integer getOperatorIndex(LocomotiveBasicOperator operator) {
		return getOperatorIndex(operator.getCodeByte());
	}

	private Integer getOperatorIndex(byte codeByte) {
		return codeByte & 0xff;
	}

	private Map<Integer, LocomotiveBasicOperator> getByteCodeMap() {
		return byteCodeMap;
	}

	private Map<String, LocomotiveBasicOperator> getSourceFormMap() {
		return sourceFormMap;
	}

}