package org.maia.amstrad.gui.browser.carousel.animation.startup.dragon;

import org.maia.amstrad.gui.sprite.SpriteColorMapAlphaComposite;
import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;

public class AnimatedDragon extends AnimatedSprite {

	public AnimatedDragon(SpriteColorMapAlphaComposite colorMap) {
		super(colorMap);
	}

	void translate(int dx, int dy) {
		if (getCurrentAnimation() != null) {
			setAnimationOffsetX(getAnimationOffsetX() + dx);
			setAnimationOffsetY(getAnimationOffsetY() + dy);
		} else {
			getTarget().move(getX() + dx, getY() + dy);
		}
	}

	void turn() {
		if (getLook() != null) {
			changeLook(getLook().getMirroredLook());
		}
	}

	@Override
	public SpriteColorMapAlphaComposite getColorMap() {
		return (SpriteColorMapAlphaComposite) super.getColorMap();
	}

	@Override
	public DragonLook getLook() {
		return (DragonLook) super.getLook();
	}

	public DragonOrientation getOrientation() {
		if (getLook() != null && getLook().isImageMirroredX()) {
			return DragonOrientation.RIGHT_FACING;
		} else {
			return DragonOrientation.LEFT_FACING;
		}
	}

	public boolean isLeftFacing() {
		return DragonOrientation.LEFT_FACING.equals(getOrientation());
	}

	public boolean isRightFacing() {
		return DragonOrientation.RIGHT_FACING.equals(getOrientation());
	}

}