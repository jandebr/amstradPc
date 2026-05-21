package org.maia.amstrad.gui.browser.carousel.animation.startup.ninja;

import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;
import org.maia.amstrad.gui.sprite.animation.SpriteAnimation;

public class Ninja extends AnimatedSprite {

	public Ninja(SpriteColorMap colorMap) {
		super(colorMap);
	}

	public void appendAnimation(NinjaAnimation animation) {
		appendAnimation(animation, animation.getDurationMillis());
	}

	public void appendAnimationRepeating(NinjaAnimation animation, int repeats) {
		appendAnimationRepeating(animation, animation.getDurationMillis(), repeats);
	}

	@Override
	public NinjaLook getLook() {
		return (NinjaLook) super.getLook();
	}

	public boolean hasPose(NinjaPose pose) {
		return pose.equals(getLook().getPose());
	}

	public NinjaOrientation getOrientation() {
		if (getLook() != null && getLook().isImageMirroredX()) {
			return NinjaOrientation.LEFT_FACING;
		} else {
			return NinjaOrientation.RIGHT_FACING;
		}
	}

	public boolean isRightFacing() {
		return NinjaOrientation.RIGHT_FACING.equals(getOrientation());
	}

	public boolean isLeftFacing() {
		return NinjaOrientation.LEFT_FACING.equals(getOrientation());
	}

	public boolean isJumping() {
		SpriteAnimation animation = getCurrentAnimation();
		if (animation instanceof NinjaAnimation) {
			return ((NinjaAnimation) animation).isJump();
		} else {
			return false;
		}
	}

}