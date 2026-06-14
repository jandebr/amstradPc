package org.maia.amstrad.gui.sprite.animation;

public abstract class SpriteAnimation {

	protected SpriteAnimation() {
	}

	public abstract void animate(AnimatedSprite sprite, float unitTime, long animationDurationMillis);

	protected void changeLook(AnimatedSprite sprite, SpriteLook look) {
		sprite.changeLook(look);
	}

	protected void move(AnimatedSprite sprite, int x, int y) {
		sprite.moveAnimated(x, y);
	}

	protected void rotate(AnimatedSprite sprite, float rotationDegrees) {
		sprite.rotateAnimated(rotationDegrees);
	}

}