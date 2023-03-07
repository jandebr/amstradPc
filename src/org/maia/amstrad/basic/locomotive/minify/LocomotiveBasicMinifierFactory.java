package org.maia.amstrad.basic.locomotive.minify;

import org.maia.amstrad.basic.BasicMinifier;
import org.maia.amstrad.basic.BasicMinifierBatch;

public class LocomotiveBasicMinifierFactory {

	public static final int LEVEL_NONE = 0;

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
		BasicMinifierBatch batch = new BasicMinifierBatch();
		int boundedLevel = Math.max(Math.min(level, LEVEL_ULTRA), LEVEL_NONE);
		if (boundedLevel > LEVEL_NONE)
			batch.add(new LocomotiveBasicRemarksMinifier());
		return batch;
	}

}