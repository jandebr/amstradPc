package org.maia.amstrad.gui.sprite.animation;

import org.maia.amstrad.gui.sprite.SpriteImage;

public class SpriteLook {

	private SpriteImage image;

	private int imageOffsetX;

	private int imageOffsetY;

	private int imageOrientationX = 1; // draw left-to-right (+1) or right-to-left (-1)

	private int imageOrientationY = 1; // draw top-to-bottom (+1) or bottom-to-top (-1)

	public SpriteLook(SpriteImage image) {
		this(image, 0, 0);
	}

	public SpriteLook(SpriteImage image, int imageOffsetX, int imageOffsetY) {
		this(image, imageOffsetX, imageOffsetY, 1, 1);
	}

	public SpriteLook(SpriteImage image, int imageOffsetX, int imageOffsetY, int imageOrientationX,
			int imageOrientationY) {
		this.image = image;
		this.imageOffsetX = imageOffsetX;
		this.imageOffsetY = imageOffsetY;
		this.imageOrientationX = imageOrientationX;
		this.imageOrientationY = imageOrientationY;
	}

	public boolean isImageMirroredX() {
		return getImageOrientationX() < 0;
	}

	public boolean isImageMirroredY() {
		return getImageOrientationY() < 0;
	}

	public SpriteImage getImage() {
		return image;
	}

	public int getImageOffsetX() {
		return imageOffsetX;
	}

	public int getImageOffsetY() {
		return imageOffsetY;
	}

	public int getImageOrientationX() {
		return imageOrientationX;
	}

	public int getImageOrientationY() {
		return imageOrientationY;
	}

}