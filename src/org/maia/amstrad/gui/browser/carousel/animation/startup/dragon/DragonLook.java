package org.maia.amstrad.gui.browser.carousel.animation.startup.dragon;

import java.awt.Point;

import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.animation.SpriteLook;

public class DragonLook extends SpriteLook {

	private DragonPose pose;

	private DragonLook mirroredLook;

	private Point mouthPosition; // image coordinates

	public DragonLook(DragonPose pose, SpriteImage image, int imageOffsetX, int imageOffsetY,
			DragonOrientation orientation) {
		this(pose, image, imageOffsetX, imageOffsetY, orientation, 1);
	}

	public DragonLook(DragonPose pose, SpriteImage image, int imageOffsetX, int imageOffsetY,
			DragonOrientation orientation, int imageOrientationY) {
		super(image, imageOffsetX, imageOffsetY, DragonOrientation.LEFT_FACING.equals(orientation) ? 1 : -1,
				imageOrientationY);
		this.pose = pose;
	}

	public DragonPose getPose() {
		return pose;
	}

	public DragonLook getMirroredLook() {
		return mirroredLook;
	}

	void setMirroredLook(DragonLook mirroredLook) {
		this.mirroredLook = mirroredLook;
	}

	public Point getMouthPosition() {
		return mouthPosition;
	}

	public void setMouthPosition(Point position) {
		this.mouthPosition = position;
	}

}