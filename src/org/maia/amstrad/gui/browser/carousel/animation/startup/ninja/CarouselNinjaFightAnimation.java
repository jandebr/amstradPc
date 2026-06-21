package org.maia.amstrad.gui.browser.carousel.animation.startup.ninja;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.browser.carousel.animation.startup.CarouselPortholePixelatedAnimation;
import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;
import org.maia.amstrad.gui.sprite.animation.AnimatedSpriteAdapter;
import org.maia.amstrad.gui.sprite.animation.SpriteAnimation;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D.ControlValueGenerator;
import org.maia.util.ColorUtils;

public class CarouselNinjaFightAnimation extends CarouselPortholePixelatedAnimation {

	private NinjaCatalog catalog;

	private PerpetualApproximatingFunction2D firstNinjaCombatFunction;

	private PerpetualApproximatingFunction2D secondNinjaCombatFunctionModulator;

	private FightingNinja firstNinja;

	private FightingNinja secondNinja;

	private Rectangle firstHealthBarBounds;

	private Rectangle secondHealthBarBounds;

	private SpriteColorMap healthBarColors;

	public CarouselNinjaFightAnimation(AmstradGraphicsContext graphicsContext) {
		super(graphicsContext);
		this.catalog = new NinjaCatalog();
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setFirstNinja(new FightingNinja(createFirstNinjaColors()));
		setSecondNinja(new FightingNinja(createSecondNinjaColors()));
		initNinjas();
		initCombatFunctions();
		initHealthBars();
	}

	protected SpriteColorMap createFirstNinjaColors() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, new Color(0, 0, 0));
		colorMap.setColor(1, new Color(80, 80, 80));
		colorMap.setColor(2, new Color(234, 240, 251));
		colorMap.setColor(3, new Color(241, 241, 223));
		return toMonitorColors(colorMap);
	}

	protected SpriteColorMap createSecondNinjaColors() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, new Color(252, 252, 250));
		colorMap.setColor(1, new Color(160, 160, 160));
		colorMap.setColor(2, new Color(0, 0, 0));
		colorMap.setColor(3, new Color(0, 0, 0));
		return toMonitorColors(colorMap);
	}

	protected SpriteColorMap createHealthBarColors() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, new Color(154, 128, 166, 200)); // border
		colorMap.setColor(1, new Color(221, 187, 237, 150)); // background
		colorMap.setColor(2, new Color(0, 0, 0, 200)); // ninja1
		colorMap.setColor(3, new Color(255, 255, 255, 200)); // ninja2
		return toMonitorColors(colorMap);
	}

	protected void initNinjas() {
		if (getRandomizer().drawBoolean()) {
			initNinjasAtHomebase();
		} else {
			initNinjasMidFight();
		}
	}

	private void initNinjasAtHomebase() {
		NinjaPose standardPose = getCatalog().getStandardPose();
		getFirstNinja().reset(standardPose.getLookRightFacing(), 20, 86);
		getFirstNinja().appendAnimationRepeating(getCatalog().getFreezeAnimation(), 3);
		getFirstNinja().appendAnimation(getCatalog().getBowAnimation());
		getSecondNinja().reset(standardPose.getLookLeftFacing(), getPortholePixelWidth() - 20 - 13, 86);
		getSecondNinja().appendAnimationRepeating(getCatalog().getFreezeAnimation(), 3);
		getSecondNinja().appendAnimation(getCatalog().getBowAnimation());
	}

	private void initNinjasMidFight() {
		int x1 = getRandomizer().drawIntegerNumber(0, getPortholePixelWidth() - 13);
		int x2 = 0, dx = 0;
		do {
			x2 = getRandomizer().drawIntegerNumber(0, getPortholePixelWidth() - 13);
			dx = Math.abs(x2 - x1);
		} while (dx < 20 || dx > getPortholePixelWidth() / 3 * 2);
		NinjaPose standardPose = getCatalog().getStandardPose();
		getFirstNinja().reset(
				standardPose.getLook(
						getRandomizer().drawBoolean() ? NinjaOrientation.RIGHT_FACING : NinjaOrientation.LEFT_FACING),
				x1, 86);
		getSecondNinja().reset(
				standardPose.getLook(
						getRandomizer().drawBoolean() ? NinjaOrientation.RIGHT_FACING : NinjaOrientation.LEFT_FACING),
				x2, 86);
		List<NinjaAnimation> animations = new Vector<NinjaAnimation>(getCatalog().getInGameAnimations());
		getFirstNinja().appendAnimation(drawRandomAnimation(animations));
		getSecondNinja().appendAnimation(drawRandomAnimation(animations));
		getFirstNinja().setHealthLevel(0.5f + 0.5f * getRandomizer().drawFloatUnitNumber());
		getSecondNinja().setHealthLevel(0.5f + 0.5f * getRandomizer().drawFloatUnitNumber());
	}

	private void initCombatFunctions() {
		setFirstNinjaCombatFunction(
				PerpetualApproximatingFunction2D.createLinearInterpolatingFunction(new ControlValueGenerator() {

					@Override
					public double generateControlValue() {
						return getRandomizer().drawDoubleUnitNumber();
					}
				}));
		setSecondNinjaCombatFunctionModulator(
				PerpetualApproximatingFunction2D.createLinearInterpolatingFunction(new ControlValueGenerator() {

					@Override
					public double generateControlValue() {
						return 0.25 * (2.0 * getRandomizer().drawDoubleUnitNumber() - 1.0);
					}
				}));
	}

	private void initHealthBars() {
		int w = getPortholePixelWidth() / 4;
		setFirstHealthBarBounds(new Rectangle(w / 2, 26, w, 6));
		setSecondHealthBarBounds(new Rectangle(w * 5 / 2, 26, w, 6));
		setHealthBarColors(createHealthBarColors());
	}

	@Override
	protected Panorama createPanorama() {
		Landscape landscape = new Landscape(
				toMonitorColors(loadPixelatedImage("animations/ninja/ninja-landscape-478x478.png")), 1.0f, 1.0f);
		return new Panorama(toMonitorColor(new Color(197, 195, 198)),
				toMonitorColors(loadPixelatedImage("animations/ninja/ninja-sky-8x150.png")), landscape);
	}

	@Override
	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		super.renderInPorthole(g, elapsedTimeMillis);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.scale(getPixelSize(), getPixelSize());
		renderNinjas(g2);
		renderNinjaHealthBars(g2, elapsedTimeMillis);
		g2.dispose();
	}

	protected void renderNinjas(Graphics2D g) {
		renderNinja(g, getFirstNinja());
		renderNinja(g, getSecondNinja());
	}

	protected void renderNinja(Graphics2D g, FightingNinja ninja) {
		ninja.drawUpdated(g);
	}

	protected void renderNinjaHealthBars(Graphics2D g, long elapsedTimeMillis) {
		renderNinjaHealthBar(g, getFirstNinja(), getFirstHealthBarBounds(), elapsedTimeMillis);
		renderNinjaHealthBar(g, getSecondNinja(), getSecondHealthBarBounds(), elapsedTimeMillis);
	}

	protected void renderNinjaHealthBar(Graphics2D g, FightingNinja ninja, Rectangle bounds, long elapsedTimeMillis) {
		float h = ninja.getHealthLevel();
		int w = Math.round(h * bounds.width);
		if (w > 0) {
			Color c = getHealthBarColors().getColor(ninja.isFirstNinja() ? 2 : 3);
			if (h < 0.4f) {
				float r = (float) Math.pow((Math.sin(elapsedTimeMillis / 100.0) + 1.0) / 2.0, 2.0);
				float t1 = ColorUtils.getTransparency(c);
				float t2 = t1 + 0.5f * (1f - t1);
				c = ColorUtils.setTransparency(c, t1 * (1f - r) + r * t2);
			}
			g.setColor(c);
			g.fillRect(bounds.x, bounds.y, w, bounds.height);
		}
		if (w < bounds.width) {
			g.setColor(getHealthBarColors().getColor(1));
			g.fillRect(bounds.x + w, bounds.y, bounds.width - w, bounds.height);
		}
		g.setColor(getHealthBarColors().getColor(0));
		g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	protected void animateNext(FightingNinja ninja, NinjaAnimation previousAnimation) {
		List<NinjaAnimation> animations = getApplicableInGameAnimations(ninja);
		returnIntoSight(ninja, animations);
		if (ninja.hasQueuedAnimations())
			return;
		FightingNinja otherNinja = getOtherNinja(ninja);
		keepDistance(ninja, otherNinja, animations);
		if (ninja.hasQueuedAnimations())
			return;
		faceOpponent(ninja, otherNinja);
		if (ninja.hasQueuedAnimations())
			return;
		if (ninja.isCombative()) {
			combat(ninja, otherNinja, animations);
		} else {
			defend(ninja, otherNinja, animations);
		}
		if (!ninja.hasQueuedAnimations()) {
			ninja.appendAnimation(drawRandomAnimation(animations)); // improvise
		}
	}

	private void returnIntoSight(FightingNinja ninja, List<NinjaAnimation> animations) {
		int w = ninja.getLook().getImage().getWidth();
		int x1 = ninja.getX() + ninja.getLook().getImageOffsetX();
		int x2 = x1 + w - 1;
		boolean outLeft = x1 < 0;
		boolean outRight = x2 >= getPortholePixelWidth();
		if (outLeft || outRight) {
			// orient towards field
			if ((outLeft && ninja.isLeftFacing()) || (outRight && ninja.isRightFacing())) {
				ninja.turn();
			} else {
				// define target position
				int tx = getPortholePixelWidth() / 2; // center
				if (outLeft) {
					tx = getRandomizer().drawIntegerNumber(0, tx);
				} else if (outRight) {
					tx = getRandomizer().drawIntegerNumber(tx, 2 * tx);
				}
				// animate towards target position
				List<NinjaAnimation> candidateAnimations = selectForwardAdvancingAnimations(animations, ninja);
				do {
					NinjaAnimation animation = drawRandomAnimation(candidateAnimations);
					ninja.appendAnimation(animation);
					x1 += animation.getAdvancementX(ninja);
				} while ((outLeft && x1 < tx) || (outRight && x1 + w > tx));
			}
		}
	}

	private void keepDistance(FightingNinja ninja, FightingNinja otherNinja, List<NinjaAnimation> animations) {
		if (ninja.isOverlapping(otherNinja)) {
			List<NinjaAnimation> candidateAnimations = selectDistanceIncreasingAnimations(animations, ninja,
					otherNinja);
			candidateAnimations.remove(getCatalog().getJumpRollAnimation()); // less jumping
			if (!candidateAnimations.isEmpty()) {
				ninja.appendAnimation(drawRandomAnimation(candidateAnimations));
			}
		}
	}

	private void faceOpponent(FightingNinja ninja, FightingNinja otherNinja) {
		if (ninja.getX() < otherNinja.getX() && ninja.isLeftFacing()) {
			ninja.turn();
		} else if (ninja.getX() > otherNinja.getX() && ninja.isRightFacing()) {
			ninja.turn();
		}
	}

	private void combat(FightingNinja ninja, FightingNinja otherNinja, List<NinjaAnimation> animations) {
		List<NinjaAnimation> candidateAnimations = null;
		if (ninja.getSpace(otherNinja) > 8) {
			// approach opponent (no overlap)
			candidateAnimations = selectAnimationsWithoutOverlap(selectForwardAdvancingAnimations(animations, ninja),
					ninja, otherNinja);
			candidateAnimations.remove(getCatalog().getJumpRollAnimation()); // less jumping
		} else {
			// combat
			candidateAnimations = selectCombativeAnimations(animations);
		}
		if (!candidateAnimations.isEmpty()) {
			ninja.appendAnimation(drawRandomAnimation(candidateAnimations));
		}
	}

	private void defend(FightingNinja ninja, FightingNinja otherNinja, List<NinjaAnimation> animations) {
		// any non-combative action (no overlap)
		List<NinjaAnimation> candidateAnimations = selectAnimationsWithoutOverlap(selectDefensiveAnimations(animations),
				ninja, otherNinja);
		if (!candidateAnimations.isEmpty()) {
			ninja.appendAnimation(drawRandomAnimation(candidateAnimations));
		}
	}

	private List<NinjaAnimation> getApplicableInGameAnimations(FightingNinja ninja) {
		List<NinjaAnimation> animations = new Vector<NinjaAnimation>();
		for (NinjaAnimation animation : getCatalog().getInGameAnimations()) {
			if (animation.isApplicable(ninja)) {
				animations.add(animation);
			}
		}
		return animations;
	}

	private List<NinjaAnimation> selectForwardAdvancingAnimations(List<NinjaAnimation> animations,
			FightingNinja ninja) {
		List<NinjaAnimation> selectAnimations = new Vector<NinjaAnimation>(animations.size());
		for (NinjaAnimation animation : animations) {
			if ((ninja.isRightFacing() && animation.getAdvancementX(ninja) > 0)
					|| (ninja.isLeftFacing() && animation.getAdvancementX(ninja) < 0)) {
				selectAnimations.add(animation);
			}
		}
		return selectAnimations;
	}

	private List<NinjaAnimation> selectDistanceIncreasingAnimations(List<NinjaAnimation> animations,
			FightingNinja ninja, FightingNinja otherNinja) {
		int space = ninja.getSpace(otherNinja);
		List<NinjaAnimation> selectAnimations = new Vector<NinjaAnimation>(animations.size());
		for (NinjaAnimation animation : animations) {
			if (ninja.getSpaceFollowingAnimation(animation, otherNinja) > space) {
				selectAnimations.add(animation);
			}
		}
		return selectAnimations;
	}

	private List<NinjaAnimation> selectAnimationsWithoutOverlap(List<NinjaAnimation> animations, FightingNinja ninja,
			FightingNinja otherNinja) {
		List<NinjaAnimation> selectAnimations = new Vector<NinjaAnimation>(animations.size());
		for (NinjaAnimation animation : animations) {
			if (ninja.getSpaceFollowingAnimation(animation, otherNinja) >= 0) {
				selectAnimations.add(animation);
			}
		}
		return selectAnimations;
	}

	private List<NinjaAnimation> selectCombativeAnimations(List<NinjaAnimation> animations) {
		List<NinjaAnimation> selectAnimations = new Vector<NinjaAnimation>(animations.size());
		for (NinjaAnimation animation : animations) {
			if (animation.isCombative()) {
				selectAnimations.add(animation);
			}
		}
		return selectAnimations;
	}

	private List<NinjaAnimation> selectDefensiveAnimations(List<NinjaAnimation> animations) {
		List<NinjaAnimation> selectAnimations = new Vector<NinjaAnimation>(animations.size());
		for (NinjaAnimation animation : animations) {
			if (animation.isDefensive()) {
				selectAnimations.add(animation);
			}
		}
		return selectAnimations;
	}

	private NinjaAnimation drawRandomAnimation(List<NinjaAnimation> animations) {
		if (animations.isEmpty()) {
			System.err.println("No animations to draw from");
			return null;
		} else {
			NinjaAnimation animation = animations.get(getRandomizer().drawIntegerNumber(0, animations.size() - 1));
			while (getCatalog().getJumpRollAnimation().equals(animation)) {
				// less jumping
				if (getRandomizer().drawFloatUnitNumber() < 0.25f) {
					return animation;
				} else {
					animation = animations.get(getRandomizer().drawIntegerNumber(0, animations.size() - 1));
				}
			}
			return animation;
		}
	}

	private NinjaCatalog getCatalog() {
		return catalog;
	}

	private PerpetualApproximatingFunction2D getFirstNinjaCombatFunction() {
		return firstNinjaCombatFunction;
	}

	private void setFirstNinjaCombatFunction(PerpetualApproximatingFunction2D func) {
		this.firstNinjaCombatFunction = func;
	}

	private PerpetualApproximatingFunction2D getSecondNinjaCombatFunctionModulator() {
		return secondNinjaCombatFunctionModulator;
	}

	private void setSecondNinjaCombatFunctionModulator(PerpetualApproximatingFunction2D modFunc) {
		this.secondNinjaCombatFunctionModulator = modFunc;
	}

	protected FightingNinja getFirstNinja() {
		return firstNinja;
	}

	private void setFirstNinja(FightingNinja ninja) {
		this.firstNinja = ninja;
	}

	protected FightingNinja getSecondNinja() {
		return secondNinja;
	}

	private void setSecondNinja(FightingNinja ninja) {
		this.secondNinja = ninja;
	}

	protected FightingNinja getOtherNinja(FightingNinja ninja) {
		return ninja.isFirstNinja() ? getSecondNinja() : getFirstNinja();
	}

	private Rectangle getFirstHealthBarBounds() {
		return firstHealthBarBounds;
	}

	private void setFirstHealthBarBounds(Rectangle bounds) {
		this.firstHealthBarBounds = bounds;
	}

	private Rectangle getSecondHealthBarBounds() {
		return secondHealthBarBounds;
	}

	private void setSecondHealthBarBounds(Rectangle bounds) {
		this.secondHealthBarBounds = bounds;
	}

	protected SpriteColorMap getHealthBarColors() {
		return healthBarColors;
	}

	private void setHealthBarColors(SpriteColorMap colors) {
		this.healthBarColors = colors;
	}

	protected class FightingNinja extends Ninja {

		private FightingNinjaShadow shadow;

		private long startTimeMillis;

		private float healthLevel = 1f; // between 0 and 1

		public FightingNinja(SpriteColorMap colorMap) {
			super(colorMap);
			this.shadow = new FightingNinjaShadow();
			this.startTimeMillis = System.currentTimeMillis();
			addSpriteListener(new FightingNinjaController(this));
		}

		public void turn() {
			appendAnimation(getCatalog().getTurnAnimation());
		}

		@Override
		public void draw(Graphics2D g) {
			NinjaLook look = getLook();
			if (look != null) {
				drawUpdatedShadow(g, look);
				// Target (x,y) is the left-bottom coordinate, regardless of orientation
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(look.getImageOffsetX(), look.getImageOffsetY() - look.getImage().getHeight());
				getTarget().draw(g2);
				g2.dispose();
			}
		}

		private void drawUpdatedShadow(Graphics2D g, NinjaLook look) {
			updateShadow(look);
			drawShadow(g, look);
		}

		private void updateShadow(NinjaLook look) {
			FightingNinjaShadow shadow = getShadow();
			shadow.changeImage(look.getImage());
			if (look.isImageMirroredX() ^ shadow.isMirroredX())
				shadow.flipX();
		}

		private void drawShadow(Graphics2D g, NinjaLook look) {
			Graphics2D g2 = (Graphics2D) g.create();
			if (isJumping()) {
				g2.translate(getX(), getAnimationOffsetY() + Math.round(0.4f * (getAnimationOffsetY() - getY())));
			} else {
				g2.translate(getX(), getY());
			}
			g2.translate(look.getImageOffsetX(), -look.getImageOffsetY());
			g2.shear(-1.0, 0);
			g2.scale(1.0, 0.4);
			g2.translate(0, look.getImage().getHeight());
			g2.scale(1.0, -1.0);
			getShadow().draw(g2);
			g2.dispose();
		}

		public boolean isFirstNinja() {
			return getFirstNinja().equals(this);
		}

		public boolean isCombative() {
			double t = (System.currentTimeMillis() - getStartTimeMillis()) / 500.0;
			double c = getFirstNinjaCombatFunction().evaluate(t);
			if (isFirstNinja()) {
				return c >= 0.5;
			} else {
				return (1.0 - c) + getSecondNinjaCombatFunctionModulator().evaluate(t) >= 0.5;
			}
		}

		public boolean isOverlapping(FightingNinja otherNinja) {
			return !isJumping() && !otherNinja.isJumping() && getSpace(otherNinja) < 0;
		}

		public int getSpace(FightingNinja otherNinja) {
			return getSpace(getX(), getLook(), otherNinja.getX(), otherNinja.getLook());
		}

		public int getSpaceFollowingAnimation(NinjaAnimation animation, FightingNinja otherNinja) {
			int x1 = getX() + animation.getAdvancementX(this);
			int x2 = otherNinja.getX();
			return getSpace(x1, animation.getEndLook(this), x2, otherNinja.getLook());
		}

		private int getSpace(int x1, NinjaLook look1, int x2, NinjaLook look2) {
			x1 += look1.getImageOffsetX();
			x2 += look2.getImageOffsetX();
			if (x1 <= x2) {
				return x2 - x1 - look1.getImage().getWidth();
			} else {
				return x1 - x2 - look2.getImage().getWidth();
			}
		}

		private FightingNinjaShadow getShadow() {
			return shadow;
		}

		private long getStartTimeMillis() {
			return startTimeMillis;
		}

		public float getHealthLevel() {
			return healthLevel;
		}

		public void setHealthLevel(float level) {
			this.healthLevel = level;
		}

	}

	private static class FightingNinjaShadow extends Sprite {

		public FightingNinjaShadow() {
			super(new SpriteColorMapImpl(new Color(100, 100, 100, 100)));
		}

	}

	private class FightingNinjaController extends AnimatedSpriteAdapter {

		private FightingNinja ninja;

		public FightingNinjaController(FightingNinja ninja) {
			this.ninja = ninja;
		}

		@Override
		public void animationEnded(AnimatedSprite sprite, SpriteAnimation animation) {
			NinjaAnimation previousAnimation = null;
			if (animation instanceof NinjaAnimation) {
				previousAnimation = (NinjaAnimation) animation;
				if (previousAnimation.isCombative()) {
					updateHealthLevelsFollowingCombat();
				}
			}
			if (!sprite.hasQueuedAnimations()) {
				animateNext(getNinja(), previousAnimation);
			}
		}

		private void updateHealthLevelsFollowingCombat() {
			FightingNinja opponent = getOtherNinja(getNinja());
			if (getNinja().getSpace(opponent) <= 6) {
				// Hurt opponent
				float health = opponent.getHealthLevel();
				float updatedHealth = health * (0.95f - 0.1f * getRandomizer().drawFloatUnitNumber());
				opponent.setHealthLevel(updatedHealth);
				// Reset self
				if (getNinja().getHealthLevel() < 0.05f && getRandomizer().drawBoolean()) {
					getNinja().setHealthLevel(1f);
				}
			}
		}

		private FightingNinja getNinja() {
			return ninja;
		}

	}

}