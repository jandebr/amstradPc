package org.maia.amstrad.gui.browser.carousel.animation.startup.ninja;

import org.maia.amstrad.gui.sprite.SpriteImage;

public class NinjaPose {

	private NinjaLook lookRightFacing;

	private NinjaLook lookLeftFacing;

	public NinjaPose(SpriteImage image) {
		this(image, 0, 0, 0);
	}

	public NinjaPose(SpriteImage image, int imageOffsetXright, int imageOffsetXleft, int imageOffsetY) {
		this.lookRightFacing = new NinjaLook(this, image, imageOffsetXright, imageOffsetY,
				NinjaOrientation.RIGHT_FACING);
		this.lookLeftFacing = new NinjaLook(this, image, imageOffsetXleft, imageOffsetY, NinjaOrientation.LEFT_FACING);
		getLookRightFacing().setMirroredLook(getLookLeftFacing());
		getLookLeftFacing().setMirroredLook(getLookRightFacing());
	}

	public NinjaLook getLook(NinjaOrientation orientation) {
		if (NinjaOrientation.RIGHT_FACING.equals(orientation)) {
			return getLookRightFacing();
		} else {
			return getLookLeftFacing();
		}
	}

	public NinjaLook getLookRightFacing() {
		return lookRightFacing;
	}

	public NinjaLook getLookLeftFacing() {
		return lookLeftFacing;
	}

}