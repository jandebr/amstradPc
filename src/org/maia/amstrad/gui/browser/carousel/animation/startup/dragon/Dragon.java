package org.maia.amstrad.gui.browser.carousel.animation.startup.dragon;

import java.awt.Graphics2D;
import java.awt.Point;

import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapAlphaComposite;
import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;
import org.maia.amstrad.gui.sprite.animation.AnimatedSpriteAdapter;
import org.maia.amstrad.gui.sprite.animation.SpriteAnimation;

public class Dragon extends Sprite {

	private DragonState state;

	private AnimatedDragon globalAnimation;

	private AnimatedDragon localAnimation;

	private DragonLook look;

	public Dragon(SpriteColorMap colorMap, DragonLook look, int x, int y) {
		super(new SpriteColorMapAlphaComposite(colorMap));
		this.globalAnimation = createGlobalAnimation(getColorMap(), look, x, y);
		this.localAnimation = createLocalAnimation(getColorMap(), look);
		move(x, y);
		changeLook(look);
	}

	private AnimatedDragon createGlobalAnimation(SpriteColorMapAlphaComposite colorMap, DragonLook look, int x, int y) {
		AnimatedDragon animated = new AnimatedDragon(colorMap);
		animated.addSpriteListener(new AnimatedSpriteAdapter() {
			@Override
			public void animationEnded(AnimatedSprite sprite, SpriteAnimation animation) {
				if (!sprite.hasQueuedAnimations()) {
					nextGlobalAnimation();
				}
			}
		});
		animated.reset(look, x, y);
		return animated;
	}

	private AnimatedDragon createLocalAnimation(SpriteColorMapAlphaComposite colorMap, DragonLook look) {
		AnimatedDragon animated = new AnimatedDragon(colorMap);
		animated.addSpriteListener(new AnimatedSpriteAdapter() {
			@Override
			public void animationEnded(AnimatedSprite sprite, SpriteAnimation animation) {
				int dx = getLocalAnimation().getX();
				int dy = getLocalAnimation().getY();
				getGlobalAnimation().translate(dx, dy); // carry over local displacement to global
				getLocalAnimation().translate(-dx, -dy); // reset local to origin
				if (!sprite.hasQueuedAnimations()) {
					nextLocalAnimation();
				}
			}
		});
		animated.reset(look);
		return animated;
	}

	public void clearAnimations() {
		getGlobalAnimation().clearAnimations();
		getLocalAnimation().clearAnimations();
	}

	public void appendGlobalAnimation(DragonAnimation animation) {
		getGlobalAnimation().appendAnimation(animation, animation.getDurationMillis());
	}

	public void appendGlobalAnimationRepeating(DragonAnimation animation, int repeats) {
		getGlobalAnimation().appendAnimationRepeating(animation, animation.getDurationMillis(), repeats);
	}

	public void appendLocalAnimation(DragonAnimation animation) {
		getLocalAnimation().appendAnimation(animation, animation.getDurationMillis());
	}

	public void appendLocalAnimationRepeating(DragonAnimation animation, int repeats) {
		getLocalAnimation().appendAnimationRepeating(animation, animation.getDurationMillis(), repeats);
	}

	public void turn() {
		getLocalAnimation().turn();
	}

	public void turnIf(boolean condition) {
		if (condition)
			turn();
	}

	public final void drawUpdated(Graphics2D g) {
		update();
		draw(g);
	}

	public void update() {
		getLocalAnimation().update();
		getGlobalAnimation().update();
		move(getGlobalAnimation().getX() + getLocalAnimation().getX(),
				getGlobalAnimation().getY() + getLocalAnimation().getY());
		changeLook(getLocalAnimation().getLook());
		getColorMap().changeTransparencyFactor(getGlobalAnimation().getColorMap().getTransparencyFactor());
	}

	@Override
	public void draw(Graphics2D g) {
		DragonLook look = getLook();
		if (look != null) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(look.getImageOffsetX(), look.getImageOffsetY());
			super.draw(g2);
			g2.dispose();
		} else {
			super.draw(g);
		}
	}

	protected void nextGlobalAnimation() {
		// Nothing, subclasses can override this
	}

	protected void nextLocalAnimation() {
		// Nothing, subclasses can override this
	}

	private void changeLook(DragonLook look) {
		changeImage(look.getImage());
		if (look.isImageMirroredX() ^ isMirroredX())
			flipX();
		if (look.isImageMirroredY() ^ isMirroredY())
			flipY();
		setLook(look);
	}

	@Override
	public SpriteColorMapAlphaComposite getColorMap() {
		return (SpriteColorMapAlphaComposite) super.getColorMap();
	}

	public Point getCenterLocation() {
		int x = getX();
		int y = getY();
		DragonLook look = getLook();
		if (look != null) {
			x += look.getImageOffsetX() + look.getImage().getWidth() / 2;
			y += look.getImageOffsetY() + look.getImage().getHeight() / 2;
		}
		return new Point(x, y);
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

	protected DragonAnimation getCurrentGlobalAnimation() {
		return (DragonAnimation) getGlobalAnimation().getCurrentAnimation();
	}

	protected DragonAnimation getCurrentLocalAnimation() {
		return (DragonAnimation) getLocalAnimation().getCurrentAnimation();
	}

	public boolean isHit() {
		DragonState state = getState();
		return DragonState.FALLING_DEAD.equals(state) || DragonState.LYING_DEAD.equals(state);
	}

	public DragonState getState() {
		return state;
	}

	public void setState(DragonState state) {
		this.state = state;
	}

	private AnimatedDragon getGlobalAnimation() {
		return globalAnimation;
	}

	private AnimatedDragon getLocalAnimation() {
		return localAnimation;
	}

	public DragonLook getLook() {
		return look;
	}

	private void setLook(DragonLook look) {
		this.look = look;
	}

}