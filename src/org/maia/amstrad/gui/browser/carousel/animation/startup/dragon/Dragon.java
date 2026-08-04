package org.maia.amstrad.gui.browser.carousel.animation.startup.dragon;

import java.awt.Graphics2D;
import java.awt.Point;

import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapAlphaComposite;
import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;
import org.maia.amstrad.gui.sprite.animation.AnimatedSpriteAdapter;
import org.maia.amstrad.gui.sprite.animation.SpriteAnimation;
import org.maia.swing.SwingUtils;
import org.maia.util.Randomizer;

public class Dragon extends Sprite {

	private DragonState state;

	private AnimatedDragon globalAnimation;

	private AnimatedDragon localAnimation;

	private DragonLook look;

	private SpriteColorMap fireColorMap;

	private long fireStartTimeMillis;

	private long fireEndTimeMillis;

	private Randomizer randomizer;

	public Dragon(SpriteColorMap colorMap, SpriteColorMap fireColorMap, DragonLook look, int x, int y) {
		super(new SpriteColorMapAlphaComposite(colorMap));
		this.globalAnimation = createGlobalAnimation(getColorMap(), look, x, y);
		this.localAnimation = createLocalAnimation(getColorMap(), look);
		this.fireColorMap = fireColorMap;
		this.randomizer = new Randomizer();
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
			drawFireBreathing(g2, look);
			g2.dispose();
		} else {
			super.draw(g);
		}
	}

	private void drawFireBreathing(Graphics2D g, DragonLook look) {
		Point mouthPosition = look.getMouthPosition();
		if (mouthPosition != null && isFireBreathingState(getState())) {
			long now = System.currentTimeMillis();
			if (now >= getFireEndTimeMillis()) {
				prepareForNextFireBreathing();
			} else if (now > getFireStartTimeMillis()) {
				float unitMagnitude = Math.min((now - getFireStartTimeMillis()) / 200f, 1f);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(getX(), getY());
				if (isMirroredX())
					g2.translate(getWidth(), 0);
				if (isMirroredY())
					g2.translate(0, getHeight());
				g2.scale(look.getImageOrientationX(), look.getImageOrientationY());
				g2.translate(mouthPosition.x, mouthPosition.y);
				g2.scale(-1.0, 1.0);
				drawFireBreathing(g2, unitMagnitude);
				g2.dispose();
			}
		}
	}

	private void drawFireBreathing(Graphics2D g, float unitMagnitude) {
		SpriteColorMap colors = getFireColorMap();
		Randomizer rnd = getRandomizer();
		for (int i = 0; i < 40; i++) {
			double alpha = SwingUtils.degreesToRadians(12.0 * (rnd.drawDoubleUnitNumber() * 2.0 - 1.0));
			double r = rnd.drawDoubleUnitNumber();
			double radius = 30.0 * unitMagnitude * r;
			int x0 = (int) Math.round(radius * Math.cos(alpha));
			int y0 = (int) Math.round(radius * Math.sin(alpha) * (0.5 + r * 0.5));
			int dx = Math.min(4, Math.max(1, (int) Math.round(rnd.drawGaussian(4.0 - r * 3.0, 0.5))));
			int ci = rnd.drawIntegerNumber(0, colors.getMaxColorIndex());
			g.setColor(colors.getColor(ci));
			g.fillRect(x0, y0, dx, 1);
		}
	}

	private void prepareForNextFireBreathing() {
		float r = getRandomizer().drawFloatUnitNumber();
		long delay = 300L + Math.round(r * r * 2000L);
		long duration = getRandomizer().drawLongIntegerNumber(500L, 1000L);
		setFireStartTimeMillis(System.currentTimeMillis() + delay);
		setFireEndTimeMillis(getFireStartTimeMillis() + duration);
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

	public Point getMouthLocation() {
		Point location = null;
		DragonLook look = getLook();
		if (look != null) {
			Point mouthPosition = look.getMouthPosition();
			if (mouthPosition != null) {
				int x = getX() + look.getImageOffsetX();
				int y = getY() + look.getImageOffsetY();
				if (isMirroredX()) {
					x += getWidth() - mouthPosition.x;
				} else {
					x += mouthPosition.x;
				}
				if (isMirroredY()) {
					y += getHeight() - mouthPosition.y;
				} else {
					y += mouthPosition.y;
				}
				location = new Point(x, y);
			}
		}
		return location;
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

	private boolean isFireBreathingState(DragonState state) {
		return DragonState.HOVERING.equals(state) || DragonState.MOUNTING.equals(state)
				|| DragonState.UNMOUNTING.equals(state);
	}

	public DragonState getState() {
		return state;
	}

	public void setState(DragonState state) {
		if (!isFireBreathingState(this.state) && isFireBreathingState(state)) {
			prepareForNextFireBreathing();
		}
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

	private SpriteColorMap getFireColorMap() {
		return fireColorMap;
	}

	private long getFireStartTimeMillis() {
		return fireStartTimeMillis;
	}

	private void setFireStartTimeMillis(long timeMillis) {
		this.fireStartTimeMillis = timeMillis;
	}

	private long getFireEndTimeMillis() {
		return fireEndTimeMillis;
	}

	private void setFireEndTimeMillis(long timeMillis) {
		this.fireEndTimeMillis = timeMillis;
	}

	private Randomizer getRandomizer() {
		return randomizer;
	}

}