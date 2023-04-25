package org.maia.amstrad.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileBasedImageProxy extends CacheableImageProxy {

	private File file;

	public FileBasedImageProxy(File file) {
		this.file = file;
	}

	@Override
	protected Image loadImage() throws IOException {
		return ImageIO.read(getFile());
	}

	public File getFile() {
		return file;
	}

}