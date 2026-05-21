package org.maia.amstrad.gui.browser.carousel.animation.startup.ninja;

import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.animation.SpriteLook;

public class NinjaLook extends SpriteLook {

	private NinjaPose pose;

	private NinjaLook mirroredLook;

	public NinjaLook(NinjaPose pose, SpriteImage image, int imageOffsetX, int imageOffsetY,
			NinjaOrientation orientation) {
		super(image, imageOffsetX, imageOffsetY, NinjaOrientation.RIGHT_FACING.equals(orientation) ? 1 : -1, 1);
		this.pose = pose;
	}

	public NinjaPose getPose() {
		return pose;
	}

	public NinjaLook getMirroredLook() {
		return mirroredLook;
	}

	void setMirroredLook(NinjaLook mirroredLook) {
		this.mirroredLook = mirroredLook;
	}

}