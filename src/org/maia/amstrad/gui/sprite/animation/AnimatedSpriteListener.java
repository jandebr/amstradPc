package org.maia.amstrad.gui.sprite.animation;

import org.maia.util.GenericListener;

public interface AnimatedSpriteListener extends GenericListener {

	void animationStarted(AnimatedSprite sprite, SpriteAnimation animation);

	void animationEnded(AnimatedSprite sprite, SpriteAnimation animation);

	void animationCancelled(AnimatedSprite sprite, SpriteAnimation animation);

}