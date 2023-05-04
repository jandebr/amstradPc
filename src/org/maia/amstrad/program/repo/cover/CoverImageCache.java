package org.maia.amstrad.program.repo.cover;

import java.awt.Image;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.util.KeyedCacheLRU;

public class CoverImageCache extends KeyedCacheLRU<Node, Image> {

	private static CoverImageCache instance;

	private static final String SETTING_CACHE_CAPACITY = "program_browser.cover_images.cache_capacity";

	private CoverImageCache() {
		super(Integer.parseInt(
				AmstradFactory.getInstance().getAmstradContext().getUserSettings().get(SETTING_CACHE_CAPACITY, "10")));
	}

	@Override
	protected void evicted(Node key, Image value) {
		super.evicted(key, value);
		value.flush();
		// System.out.println("Disposed cover image for " + key.getName());
	}

	public static CoverImageCache getInstance() {
		if (instance == null) {
			setInstance(new CoverImageCache());
		}
		return instance;
	}

	private static synchronized void setInstance(CoverImageCache cache) {
		if (instance == null) {
			instance = cache;
		}
	}

}