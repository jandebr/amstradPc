package org.maia.amstrad.gui.browser.carousel.animation.startup.ninja;

import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;
import org.maia.amstrad.gui.sprite.animation.SpriteAnimation;

public abstract class NinjaAnimation extends SpriteAnimation {

	protected NinjaAnimation() {
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public final void animate(AnimatedSprite sprite, float unitTime, long animationDurationMillis) {
		animateNinja((Ninja) sprite, unitTime);
	}

	protected abstract void animateNinja(Ninja ninja, float unitTime);

	protected void changePose(Ninja ninja, NinjaPose pose) {
		changeLook(ninja, pose.getLook(ninja.getOrientation()));
	}

	public abstract boolean isApplicable(Ninja ninja);

	public boolean isJump() {
		return false; // subclasses may override this
	}

	public boolean isCombative() {
		return false; // subclasses may override this
	}

	public final boolean isDefensive() {
		return !isCombative();
	}

	public int getAdvancementX(Ninja ninja) {
		return 0; // subclasses may override this
	}

	public abstract long getDurationMillis();

	public abstract NinjaLook getEndLook(Ninja ninja);

	public abstract String getName();

}