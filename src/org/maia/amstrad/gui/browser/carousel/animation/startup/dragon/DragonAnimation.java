package org.maia.amstrad.gui.browser.carousel.animation.startup.dragon;

import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;
import org.maia.amstrad.gui.sprite.animation.SpriteAnimation;

public abstract class DragonAnimation extends SpriteAnimation {

	protected DragonAnimation() {
	}

	@Override
	public final void animate(AnimatedSprite sprite, float unitTime, long animationDurationMillis) {
		animateDragon((AnimatedDragon) sprite, unitTime, animationDurationMillis);
	}

	protected abstract void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis);

	protected void changePose(AnimatedDragon dragon, DragonPose pose) {
		changeLook(dragon, pose.getLook(dragon.getOrientation()));
	}

	public abstract long getDurationMillis();

	public int getHorizontalDisplacement() {
		return 0;
	}

	public int getVerticalDisplacement() {
		return 0;
	}

}