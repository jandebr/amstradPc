package org.maia.amstrad.basic.locomotive.minify;

import java.util.Map;

import org.maia.amstrad.basic.BasicMinifier;
import org.maia.amstrad.basic.BasicMinifierBatch;
import org.maia.amstrad.basic.locomotive.token.VariableToken;

public class LocomotiveBasicMinifierFactory {

	public static final int LEVEL_NONE = 0;

	public static final int LEVEL_NON_INVASIVE = 2;

	public static final int LEVEL_ULTRA = 10;

	private static LocomotiveBasicMinifierFactory instance;

	public static LocomotiveBasicMinifierFactory getInstance() {
		if (instance == null) {
			setInstance(new LocomotiveBasicMinifierFactory());
		}
		return instance;
	}

	private static synchronized void setInstance(LocomotiveBasicMinifierFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

	private LocomotiveBasicMinifierFactory() {
	}

	public BasicMinifier createMinifier(int level) {
		return createMinifier(level, null);
	}

	public BasicMinifier createMinifier(int level, Map<VariableToken, VariableToken> variableRenameMap) {
		BasicMinifierBatch batch = new BasicMinifierBatch();
		int boundedLevel = Math.max(Math.min(level, LEVEL_ULTRA), LEVEL_NONE);
		if (boundedLevel > LEVEL_NONE) {
			batch.add(new LocomotiveBasicRemarksMinifier());
		}
		if (boundedLevel > LEVEL_NONE + 1) {
			batch.add(new LocomotiveBasicWhitespaceMinifier());
		}
		if (boundedLevel > LEVEL_NONE + 2) {
			if (variableRenameMap != null) {
				batch.add(new LocomotiveBasicVariableNameMinifier(variableRenameMap));
			} else {
				batch.add(new LocomotiveBasicVariableNameMinifier());
			}
		}
		return batch;
	}

}