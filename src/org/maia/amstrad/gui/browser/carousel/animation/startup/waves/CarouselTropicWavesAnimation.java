package org.maia.amstrad.gui.browser.carousel.animation.startup.waves;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.SpriteImageRLE;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.graphics2d.function.Function2D;
import org.maia.graphics2d.function.SigmoidFunction2D;
import org.maia.graphics2d.geometry.Radians;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.AgitationLevel;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.TimeRange;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.ValueRange;
import org.maia.util.ColorUtils;

public class CarouselTropicWavesAnimation extends CarouselWavesAnimation {

	private SpriteImage dolphinImage;

	private Map<Integer, Dolphin> dolphins = new HashMap<Integer, Dolphin>(); // indexed by wave index

	private List<JumpProfile> jumpProfiles = new Vector<JumpProfile>();

	private static Range dolphinBrightnessRange = new Range(-0.05f, 0.05f); // brightness adjustment factor

	private static Range dolphinVelocityRange = new Range(0.7f, 0.9f);

	private static float minHeightForLooping = 0.38f; // relative height

	public CarouselTropicWavesAnimation(AmstradGraphicsContext graphicsContext) {
		super(graphicsContext);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		initDolphins();
		initJumpProfiles();
	}

	protected void initDolphins() {
		int n = getDolphinCount();
		for (int waveIndex = 0; waveIndex < n; waveIndex++) {
			Dolphin dolphin = new Dolphin(waveIndex, drawValue(dolphinVelocityRange));
			getDolphins().put(waveIndex, dolphin);
		}
	}

	protected void initJumpProfiles() {
		float bl = getWavesBaseline();
		addJumpProfile(new JumpProfile(new Range(0.5f * bl, 0.6f * bl), new Range(-0.1f, 0f)));
		addJumpProfile(new JumpProfile(new Range(0.2f, 0.3f), new Range(-0.1f, -0.2f)));
		addJumpProfile(new JumpProfile(new Range(0.1f, 0.15f), new Range(-0.4f, -0.5f)));
		if (!isEncouragedToJumpHigher()) {
			for (int i = 0; i < 3; i++) {
				addJumpProfile(new JumpProfile(new Range(0.1f, 0.1f), new Range(-0.7f, -0.9f)));
			}
		}
	}

	private void addJumpProfile(JumpProfile profile) {
		getJumpProfiles().add(profile);
	}

	protected SpriteColorMap createDolphinColors(float brightness) {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, ColorUtils.adjustBrightness(new Color(16, 29, 51), brightness));
		colorMap.setColor(1, ColorUtils.adjustBrightness(new Color(127, 140, 161), brightness));
		colorMap.setColor(2, ColorUtils.adjustBrightness(new Color(1, 7, 18), brightness));
		return toMonitorColors(colorMap);
	}

	@Override
	protected List<Color> createWaveColors() {
		List<Color> colors = new Vector<Color>(5);
		colors.add(toMonitorColor(new Color(197, 241, 210)));
		colors.add(toMonitorColor(new Color(51, 210, 185)));
		colors.add(toMonitorColor(new Color(26, 126, 167)));
		colors.add(toMonitorColor(new Color(16, 91, 160)));
		colors.add(toMonitorColor(new Color(0, 33, 149)));
		return colors;
	}

	@Override
	protected List<AgitationLevel> createWaveAgitationLevels() {
		List<AgitationLevel> levels = new Vector<AgitationLevel>(1);
		levels.add(new AgitationLevel(new ValueRange(0, 0.15f), new TimeRange(4000L, 4000L)));
		return levels;
	}

	@Override
	protected Panorama createPanorama() {
		float bl = getWavesBaseline();
		Landscape landscape = new Landscape(
				toMonitorColors(loadPixelatedImage("animations/waves/tropic-landscape-645x120.png")), bl,
				Math.max(bl - 0.2f, 0.1f));
		return new Panorama(toMonitorColor(new Color(177, 195, 209)),
				toMonitorColors(loadPixelatedImage("animations/waves/tropic-sky-8x150.png")), landscape);
	}

	@Override
	protected void renderPixelatedWaveOverlay(Graphics2D g, int waveIndex, long elapsedTimeMillis) {
		Dolphin dolphin = getDolphins().get(waveIndex);
		if (dolphin != null) {
			renderDolphin(g, dolphin, elapsedTimeMillis);
		}
	}

	protected void renderDolphin(Graphics2D g, Dolphin dolphin, long elapsedTimeMillis) {
		dolphin.update(elapsedTimeMillis);
		if (dolphin.isShowing()) {
			dolphin.draw(g);
		}
	}

	private Jump createJump() {
		int pi = getRandomizer().drawIntegerNumber(0, getJumpProfiles().size() - 1);
		return createJump(getJumpProfiles().get(pi));
	}

	private Jump createJump(JumpProfile profile) {
		float top = drawValue(profile.getTopRange());
		float base = drawValue(profile.getBaseRange());
		boolean looping = top >= minHeightForLooping && getRandomizer().drawBoolean();
		return new Jump(top, base, looping);
	}

	private float drawValue(Range range) {
		return range.getMin() + getRandomizer().drawFloatUnitNumber() * (range.getMax() - range.getMin());
	}

	protected int drawJumpCenterX(Jump jump) {
		return getRandomizer().drawIntegerNumber(0, getPortholePixelWidth());
	}

	protected void announceDolphinJump(Dolphin dolphin, long elapsedTimeMillis) {
		// Subclasses to extend
	}

	protected boolean isEncouragedToJumpHigher() {
		// Subclasses to override
		return false;
	}

	protected int getDolphinCount() {
		return getWaveCount() - 1;
	}

	@Override
	protected double getColorScalingFunctionLinearity() {
		return 0.2;
	}

	protected SpriteImage getDolphinImage() {
		if (dolphinImage == null) {
			dolphinImage = new SpriteImageRLE(33, 23, new int[] { -1, 23, 0, 1, -2, -1, 22, 0, 3, -2, -1, 22, 0, 4, -2,
					-1, 23, 0, 4, -2, -1, 17, 0, 10, -2, -1, 14, 0, 14, -2, -1, 13, 0, 15, -2, -1, 12, 0, 16, -2, -1,
					11, 0, 18, -2, -1, 1, 0, 3, -1, 6, 0, 20, -2, 0, 6, -1, 3, 0, 4, 1, 4, 0, 14, -2, -1, 1, 0, 6, -1,
					1, 0, 3, 1, 9, 0, 12, -2, -1, 3, 0, 6, 1, 5, -1, 2, 1, 5, 0, 11, -2, -1, 3, 0, 5, 1, 3, -1, 7, 1, 3,
					0, 12, -2, -1, 3, 0, 4, 1, 2, -1, 8, 0, 16, -2, -1, 4, 0, 3, 1, 2, -1, 7, 0, 4, 1, 3, 0, 5, 2, 2, 0,
					3, -2, -1, 3, 0, 3, 1, 2, -1, 7, 0, 3, 1, 7, 0, 3, 2, 2, 0, 3, -2, -1, 3, 0, 3, 1, 2, -1, 8, 1, 2,
					-1, 4, 1, 4, 0, 6, -2, -1, 3, 0, 2, 1, 2, -1, 16, 1, 5, 0, 4, -2, -1, 3, 0, 2, 1, 1, -1, 19, 1, 4,
					0, 4, -2, -1, 2, 0, 3, -1, 22, 1, 3, 0, 3, -2, -1, 3, 0, 1, -1, 24, 1, 4, -2, -1, 30, 1, 2 });
		}
		return dolphinImage;
	}

	protected Map<Integer, Dolphin> getDolphins() {
		return dolphins;
	}

	private List<JumpProfile> getJumpProfiles() {
		return jumpProfiles;
	}

	protected class Dolphin extends Sprite {

		private int waveIndex;

		private float velocity; // portholes per second

		private Jump jump;

		private long jumpStartTimeMillis;

		private long jumpEndTimeMillis;

		private int jumpCenterX;

		private boolean showing;

		public Dolphin(int waveIndex, float velocity) {
			super(getDolphinImage(), createDolphinColors(drawValue(dolphinBrightnessRange)));
			this.waveIndex = waveIndex;
			this.velocity = velocity;
		}

		public void update(long elapsedTimeMillis) {
			setShowing(false);
			Jump jump = getJump();
			if (jump == null || elapsedTimeMillis > getJumpEndTimeMillis()) {
				initJump(elapsedTimeMillis);
			} else if (elapsedTimeMillis >= getJumpStartTimeMillis()) {
				float r = (elapsedTimeMillis - getJumpStartTimeMillis()) / (float) getJumpDurationMillis();
				Point rLoc = jump.sampleRelativeLocation(r);
				setX(getJumpCenterX() - getWidth() / 2 + rLoc.x);
				setY(getWavePixelBottom(getWaveIndex()) + 2 + rLoc.y);
				setRotationDegrees(0.8f * (90f - (float) Radians.radiansToDegrees(jump.sampleAngleRadians(r))) - 20f
						+ jump.sampleLoopDegrees(r));
				setShowing(true);
			}
		}

		private void initJump(long elapsedTimeMillis) {
			setJump(createJump());
			setJumpStartTimeMillis(elapsedTimeMillis + getTimeToNextJumpMillis());
			setJumpEndTimeMillis(
					getJumpStartTimeMillis() + Math.round(getJump().getHorizontalDistance() / getVelocity() * 1000f));
			setJumpCenterX(drawJumpCenterX(getJump()));
			announceDolphinJump(this, elapsedTimeMillis);
		}

		private long getTimeToNextJumpMillis() {
			if (getJump() == null) {
				return getRandomizer().drawLongIntegerNumber(1000L, 2000L); // first jump
			} else {
				return getRandomizer().drawLongIntegerNumber(500L, 1000L);
			}
		}

		public int getCenterX() {
			return getX() + getImage().getWidth() / 2;
		}

		public int getWaveIndex() {
			return waveIndex;
		}

		public float getVelocity() {
			return velocity;
		}

		public Jump getJump() {
			return jump;
		}

		private void setJump(Jump jump) {
			this.jump = jump;
		}

		public long getJumpDurationMillis() {
			return getJumpEndTimeMillis() - getJumpStartTimeMillis();
		}

		public long getJumpStartTimeMillis() {
			return jumpStartTimeMillis;
		}

		private void setJumpStartTimeMillis(long timeMillis) {
			this.jumpStartTimeMillis = timeMillis;
		}

		public long getJumpEndTimeMillis() {
			return jumpEndTimeMillis;
		}

		private void setJumpEndTimeMillis(long timeMillis) {
			this.jumpEndTimeMillis = timeMillis;
		}

		public int getJumpCenterX() {
			return jumpCenterX;
		}

		private void setJumpCenterX(int x) {
			this.jumpCenterX = x;
		}

		public boolean isShowing() {
			return showing;
		}

		private void setShowing(boolean showing) {
			this.showing = showing;
		}

	}

	protected class Jump {

		private float top; // above baseline, positive value, unit length

		private float base; // below baseline, negative value, unit length

		private boolean looping;

		private Function2D horizontalRadiusFt; // relative to vertical radius

		private Function2D loopingFt;

		public Jump(float top, float base, boolean looping) {
			this.top = top;
			this.base = base;
			this.looping = looping;
			this.horizontalRadiusFt = new SigmoidFunction2D(0, 4.0, 1.0, -0.5);
			this.loopingFt = SigmoidFunction2D.createCappedFunction(0, 1.0, 0, 1.0);
		}

		public Point sampleRelativeLocation(float r) {
			float angle = sampleAngleRadians(r);
			float horRadius = getHorizontalRadius();
			float verRadius = getVerticalRadius();
			if (isLooping())
				verRadius *= 1.2f;
			int dx = (int) Math.round(horRadius * Math.cos(angle) * getPortholePixelWidth());
			int dy = -(int) Math.round((getBase() + verRadius * Math.sin(angle)) * getPortholePixelHeight());
			return new Point(dx, dy);
		}

		public float sampleAngleRadians(float r) {
			return getArcRadianOffset() - r * getArcRadians();
		}

		public float sampleLoopDegrees(float r) {
			if (isLooping()) {
				return (float) getLoopingFt().evaluate(r) * 360f;
			} else {
				return 0f;
			}
		}

		public float getHorizontalDistance() {
			return 2f * getHorizontalRadius() * (float) Math.sin(getArcRadians() / 2.0);
		}

		public float getArcRadianOffset() {
			return (float) Math.PI / 2f + getArcRadians() / 2f;
		}

		public float getArcRadians() {
			return (float) (Math.PI - 2.0 * Math.asin(-getBase() / getVerticalRadius()));
		}

		public float getHorizontalRadius() {
			return (float) getHorizontalRadiusFt().evaluate(getVerticalRadius());
		}

		public float getVerticalRadius() {
			return getTop() - getBase();
		}

		public float getTop() {
			return top;
		}

		public float getBase() {
			return base;
		}

		public boolean isLooping() {
			return looping;
		}

		private Function2D getHorizontalRadiusFt() {
			return horizontalRadiusFt;
		}

		private Function2D getLoopingFt() {
			return loopingFt;
		}

	}

	protected static class JumpProfile {

		private Range topRange;

		private Range baseRange;

		public JumpProfile(Range topRange, Range baseRange) {
			this.topRange = topRange;
			this.baseRange = baseRange;
		}

		public Range getTopRange() {
			return topRange;
		}

		public Range getBaseRange() {
			return baseRange;
		}

	}

	protected static class Range {

		private float min;

		private float max;

		public Range(float min, float max) {
			this.min = min;
			this.max = max;
		}

		public float getMin() {
			return min;
		}

		public float getMax() {
			return max;
		}

	}

}