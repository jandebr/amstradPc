package org.maia.amstrad.gui;

import java.awt.Image;

public abstract class CacheableImageProxy implements ImageProxy {

	private Image image;

	private boolean imageFailedLoading;

	protected CacheableImageProxy() {
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.flush();
			image = null;
			imageFailedLoading = false;
		}
	}

	@Override
	public Image getImage() {
		if (image == null && !imageFailedLoading) {
			try {
				image = loadImage();
			} catch (Exception e) {
				imageFailedLoading = true;
				System.err.println("Failed to load image: " + e.toString());
			}
		}
		return image;
	}

	protected abstract Image loadImage() throws Exception;

}