package org.maia.amstrad.gui.sprite.animation;

public abstract class AnimatedSpriteAdapter implements AnimatedSpriteListener {

	protected AnimatedSpriteAdapter() {
	}

	@Override
	public void animationStarted(AnimatedSprite sprite, SpriteAnimation animation) {
		// Subclasses can override this
	}

	@Override
	public void animationEnded(AnimatedSprite sprite, SpriteAnimation animation) {
		// Subclasses can override this
	}

	@Override
	public void animationCancelled(AnimatedSprite sprite, SpriteAnimation animation) {
		// Subclasses can override this
	}

}