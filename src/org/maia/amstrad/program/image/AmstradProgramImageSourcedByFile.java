package org.maia.amstrad.program.image;

import java.io.File;

import org.maia.amstrad.AmstradFactory;
import org.maia.graphics2d.image.ImageInfoImpl;
import org.maia.graphics2d.image.pool.PooledImageSourcedByFile;

public class AmstradProgramImageSourcedByFile extends PooledImageSourcedByFile implements AmstradProgramImage {

	public AmstradProgramImageSourcedByFile(File file, String caption) {
		super(file, AmstradFactory.getInstance().getAmstradContext().getSharedImagePool(), new ImageInfoImpl(caption));
	}

	@Override
	public String getCaption() {
		return getImageInfo().getTitle();
	}

}