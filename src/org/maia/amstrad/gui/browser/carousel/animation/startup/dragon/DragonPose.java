package org.maia.amstrad.gui.browser.carousel.animation.startup.dragon;

import org.maia.amstrad.gui.sprite.SpriteImage;

public class DragonPose {

	private DragonLook lookLeftFacing;

	private DragonLook lookRightFacing;

	public DragonPose(SpriteImage image) {
		this(image, 0, 0, 0);
	}

	public DragonPose(SpriteImage image, int imageOffsetXleft, int imageOffsetXright, int imageOffsetY) {
		this(image, imageOffsetXleft, imageOffsetXright, imageOffsetY, 1);
	}

	public DragonPose(SpriteImage image, int imageOffsetXleft, int imageOffsetXright, int imageOffsetY,
			int imageOrientationY) {
		this.lookLeftFacing = new DragonLook(this, image, imageOffsetXleft, imageOffsetY, DragonOrientation.LEFT_FACING,
				imageOrientationY);
		this.lookRightFacing = new DragonLook(this, image, imageOffsetXright, imageOffsetY,
				DragonOrientation.RIGHT_FACING, imageOrientationY);
		getLookRightFacing().setMirroredLook(getLookLeftFacing());
		getLookLeftFacing().setMirroredLook(getLookRightFacing());
	}

	public DragonLook getLook(DragonOrientation orientation) {
		if (DragonOrientation.LEFT_FACING.equals(orientation)) {
			return getLookLeftFacing();
		} else {
			return getLookRightFacing();
		}
	}

	public DragonLook getLookLeftFacing() {
		return lookLeftFacing;
	}

	public DragonLook getLookRightFacing() {
		return lookRightFacing;
	}

}