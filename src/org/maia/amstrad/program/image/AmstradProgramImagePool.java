package org.maia.amstrad.program.image;

import org.maia.amstrad.AmstradFactory;
import org.maia.image.pool.ImagePool;

public class AmstradProgramImagePool extends ImagePool {

	private static AmstradProgramImagePool instance;

	private static final String SETTING_CAPACITY = "images.cache_capacity";

	private static final int DEFAULT_CAPACITY = 20;

	private AmstradProgramImagePool() {
		this(Integer.parseInt(AmstradFactory.getInstance().getAmstradContext().getUserSettings().get(SETTING_CAPACITY,
				String.valueOf(DEFAULT_CAPACITY))));
	}

	private AmstradProgramImagePool(int capacity) {
		super("Amstrad program image pool", capacity);
	}

	public static AmstradProgramImagePool getInstance() {
		if (instance == null) {
			setInstance(new AmstradProgramImagePool());
		}
		return instance;
	}

	private static synchronized void setInstance(AmstradProgramImagePool pool) {
		if (instance == null) {
			instance = pool;
		}
	}

}