package org.maia.amstrad.program.image;

import java.io.File;

import org.maia.image.ImageInfoImpl;
import org.maia.image.pool.PooledImageSourcedByFile;

public class AmstradProgramImageSourcedByFile extends PooledImageSourcedByFile implements AmstradProgramImage {

	public AmstradProgramImageSourcedByFile(File file, String title) {
		super(file, AmstradProgramImagePool.getInstance(), new ImageInfoImpl(title));
	}

	@Override
	public String getCaption() {
		return getImageInfo().getTitle();
	}

}