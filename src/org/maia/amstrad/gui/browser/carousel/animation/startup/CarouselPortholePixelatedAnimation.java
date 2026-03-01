package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.carousel.animation.sprite.SpriteImageCatalog;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.graphics2d.image.ImageUtils;

public abstract class CarouselPortholePixelatedAnimation extends CarouselPortholeStartupAnimation {

	private int pixelSize;

	private SpriteImageCatalog spriteImageCatalog;

	protected CarouselPortholePixelatedAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
		this.spriteImageCatalog = new SpriteImageCatalog();
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setPixelSize(derivePixelSize());
		getPanorama(); // create upfront
	}

	protected int derivePixelSize() {
		return Math.max(1, Math.round(getPortholeWidth() / (float) getTargetPixelWidth()));
	}

	protected int getTargetPixelWidth() {
		return 64;
	}

	protected BufferedImage loadPixelatedImage(String resourceName) {
		return pixelate(UIResources.loadImage(resourceName));
	}

	protected BufferedImage pixelate(BufferedImage image) {
		return ImageUtils.pixelate(image, getPixelSize());
	}

	protected int getPortholePixelWidth() {
		return Math.round(getPortholeWidth() / (float) getPixelSize());
	}

	protected int getPortholePixelHeight() {
		return Math.round(getPortholeHeight() / (float) getPixelSize());
	}

	protected int getPixelSize() {
		return pixelSize;
	}

	private void setPixelSize(int pixelSize) {
		this.pixelSize = pixelSize;
	}

	protected SpriteImageCatalog getSpriteImageCatalog() {
		return spriteImageCatalog;
	}

}