package org.maia.amstrad.gui.browser.carousel.animation.startup.waves;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.maia.amstrad.gui.AmstradSymbolRenderer;
import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapAlphaComposite;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.SpriteImageRLE;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D.ControlValueGenerator;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.ColorUtils;

public class CarouselTropicWavesGamifiedAnimation extends CarouselTropicWavesAnimation {

	private SpriteImage ringBackImage;

	private SpriteImage ringFrontImage;

	private Map<Integer, ScorePointsGraphic> scorePointsGraphics = new HashMap<Integer, ScorePointsGraphic>();

	private Map<Integer, Ring> rings = new HashMap<Integer, Ring>(); // indexed by wave index

	private List<Ring> hitRingsToAdd = new Vector<Ring>();

	private PerpetualApproximatingFunction2D maxHitRingCountFunction;

	private PerpetualApproximatingFunction2D maxMissedRingCountFunction;

	private PerpetualApproximatingFunction2D scoreTransparencyFunction;

	private int score;

	private long earliestNextMissedRingTimeMillis;

	private static float[] ringHues = new float[] { 0f, 0.55f, 0.14f, 0.31f };

	private static float minHeightForRing = 0.25f; // relative height

	private static float minHeightForHighRingScore = 0.35f; // relative height

	private static int LOW_RING_SCORE = 10;

	private static int HIGH_RING_SCORE = 50;

	private static int LOOPED_RING_SCORE_FACTOR = 2;

	private static int WRAPPED_RING_SCORE = 10000;

	public CarouselTropicWavesGamifiedAnimation(AmstradGraphicsContext graphicsContext) {
		super(graphicsContext);
		this.maxHitRingCountFunction = PerpetualApproximatingFunction2D
				.createLinearInterpolatingFunction(new ControlValueGenerator() {

					@Override
					public double generateControlValue() {
						return 2.0 * getRandomizer().drawDoubleUnitNumber(); // max 2 at any given time
					}
				});
		this.maxMissedRingCountFunction = PerpetualApproximatingFunction2D
				.createLinearInterpolatingFunction(new ControlValueGenerator() {

					@Override
					public double generateControlValue() {
						return getRandomizer().drawDoubleUnitNumber(); // max 1 at any given time
					}
				});
		this.scoreTransparencyFunction = PerpetualApproximatingFunction2D
				.createCubicApproximatingFunction(new ControlValueGenerator() {

					@Override
					public double generateControlValue() {
						return 0.6 + 0.3 * Math.sqrt(getRandomizer().drawDoubleUnitNumber());
					}
				});
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		initScorePointsGraphics();
		setScore(LOW_RING_SCORE * getRandomizer().drawIntegerNumber(0, 10));
	}

	private void initScorePointsGraphics() {
		getScorePointsGraphic(LOW_RING_SCORE);
		getScorePointsGraphic(HIGH_RING_SCORE);
		getScorePointsGraphic(HIGH_RING_SCORE * LOOPED_RING_SCORE_FACTOR);
	}

	@Override
	protected boolean isEncouragedToJumpHigher() {
		return true;
	}

	@Override
	protected int getDolphinCount() {
		return Math.min(getWaveCount() - 1, 2);
	}

	protected SpriteColorMap createRingColors(float hue) {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, ColorUtils.setHue(new Color(170, 85, 85), hue));
		colorMap.setColor(1, ColorUtils.setHue(new Color(255, 85, 85), hue));
		colorMap.setColor(2, ColorUtils.setHue(new Color(170, 0, 0), hue));
		colorMap.setColor(3, ColorUtils.setHue(new Color(255, 170, 170), hue));
		return toMonitorColors(colorMap);
	}

	@Override
	protected void renderPixelatedWaveOverlay(Graphics2D g, int waveIndex, long elapsedTimeMillis) {
		// Back of ring
		Ring ring = getRings().get(waveIndex);
		if (ring != null) {
			if (elapsedTimeMillis <= ring.getFinalTimeMillis()) {
				if (elapsedTimeMillis >= ring.getStartTimeMillis()) {
					ring.update(elapsedTimeMillis);
				}
				ring.drawBack(g);
			} else {
				getRings().remove(waveIndex);
				ring = null;
			}
		}
		// Dolphin at waveIndex
		Dolphin dolphin = getDolphins().get(waveIndex);
		int dolphinXbefore = dolphin != null ? dolphin.getCenterX() : -1;
		super.renderPixelatedWaveOverlay(g, waveIndex, elapsedTimeMillis);
		int dolphinXafter = dolphin != null ? dolphin.getCenterX() : -1;
		// Front of ring
		if (ring != null) {
			ring.drawFront(g);
			if (ring.isTypeHit() && dolphinXbefore < ring.getCenterX() && dolphinXafter >= ring.getCenterX()) {
				ring.hit(dolphin, elapsedTimeMillis);
			}
			if (ring.isHit()) {
				ring.drawScorePoints(g, elapsedTimeMillis);
			}
		}
		// Add new rings
		if (waveIndex == getWaveCount() - 1) {
			addNewRings(elapsedTimeMillis);
			renderScore(g, elapsedTimeMillis);
		}
	}

	protected void renderScore(Graphics2D g, long elapsedTimeMillis) {
		String str = String.valueOf(getScore());
		int y0 = getWavePixelBottom(getWaveCount() - 1);
		int y1 = getPortholePixelHeight() - 10;
		int y = Math.round(y0 + 0.6f * (y1 - y0));
		int x = (getPortholePixelWidth() - 8 * str.length()) / 2;
		float tra = (float) getScoreTransparencyFunction().evaluate(elapsedTimeMillis / 500.0);
		Color waveColor = getWavesComponent().getWave(0).getColor();
		AmstradSymbolRenderer renderer = getSymbolRenderer(g);
		renderer.color(ColorUtils.setTransparency(waveColor, tra));
		renderer.drawStr(str, x, y);
	}

	@Override
	protected int drawJumpCenterX(Jump jump) {
		int attempt = 1;
		int centerX;
		do {
			centerX = super.drawJumpCenterX(jump);
			if (isCenterOutsideMissedRingsReach(centerX, jump))
				return centerX;
		} while (attempt++ < 10);
		return centerX;
	}

	@Override
	protected void announceDolphinJump(Dolphin dolphin, long elapsedTimeMillis) {
		super.announceDolphinJump(dolphin, elapsedTimeMillis);
		if (isCreateRingForDolphinJump(dolphin, elapsedTimeMillis)) {
			Ring ring = createRingForDolphinJump(dolphin, elapsedTimeMillis);
			if (ring != null) {
				getHitRingsToAdd().add(ring);
			}
		}
	}

	protected boolean isCreateRingForDolphinJump(Dolphin dolphin, long elapsedTimeMillis) {
		int max = (int) Math.round(getMaxHitRingCountFunction().evaluate(elapsedTimeMillis / 500.0));
		if (getHitRingsCount() >= max)
			return false;
		if (dolphin.getJump().getTop() < minHeightForRing)
			return false;
		if (dolphin.getJumpCenterX() < getRingFrontImage().getWidth())
			return false;
		if (getPortholePixelWidth() - dolphin.getJumpCenterX() < getRingFrontImage().getWidth())
			return false;
		return true;
	}

	protected Ring createRingForDolphinJump(Dolphin dolphin, long elapsedTimeMillis) {
		Ring ring = null;
		Jump jump = dolphin.getJump();
		float r = 0.4f + 0.2f * getRandomizer().drawFloatUnitNumber();
		Point rLoc = jump.sampleRelativeLocation(r);
		int centerX = dolphin.getJumpCenterX() + rLoc.x;
		if (isCenterOutsideRingsReach(centerX)) {
			int elevationAboveWave = (int) Math
					.round((getRingFrontImage().getHeight() - dolphin.getHeight()) / 2f - rLoc.y);
			long t0 = elapsedTimeMillis;
			long t1 = dolphin.getJumpEndTimeMillis();
			int hueIndex = drawHueIndexForRing();
			int score = -rLoc.y < Math.round(minHeightForHighRingScore * getPortholePixelHeight()) ? LOW_RING_SCORE
					: HIGH_RING_SCORE;
			if (jump.isLooping())
				score *= LOOPED_RING_SCORE_FACTOR;
			ring = new Ring(dolphin.getWaveIndex(), centerX, elevationAboveWave, t0, t1, hueIndex, score);
		}
		return ring;
	}

	private void addNewRings(long elapsedTimeMillis) {
		addNewHitRings();
		addNewMissedRings(elapsedTimeMillis);
	}

	private void addNewHitRings() {
		for (Ring ring : getHitRingsToAdd()) {
			getRings().put(ring.getWaveIndex(), ring);
		}
		getHitRingsToAdd().clear();
	}

	private void addNewMissedRings(long elapsedTimeMillis) {
		if (isCreateMissedRing(elapsedTimeMillis)) {
			Ring ring = createMissedRing(elapsedTimeMillis);
			if (ring != null) {
				getRings().put(ring.getWaveIndex(), ring);
				setEarliestNextMissedRingTimeMillis(
						ring.getEndTimeMillis() + getRandomizer().drawLongIntegerNumber(500L, 1000L));
			}
		}
	}

	protected boolean isCreateMissedRing(long elapsedTimeMillis) {
		if (elapsedTimeMillis < getEarliestNextMissedRingTimeMillis())
			return false;
		int max = (int) Math.round(getMaxMissedRingCountFunction().evaluate(elapsedTimeMillis / 500.0));
		if (getMissedRingsCount() >= max)
			return false;
		if (drawFreeWaveIndexForRing() < 0)
			return false;
		return true;
	}

	protected Ring createMissedRing(long elapsedTimeMillis) {
		Ring ring = null;
		int attempt = 1;
		do {
			int centerX = Math.round((0.1f + 0.8f * getRandomizer().drawFloatUnitNumber()) * getPortholePixelWidth());
			if (isCenterOutsideDolphinsReach(centerX) && isCenterOutsideRingsReach(centerX)) {
				int waveIndex = drawFreeWaveIndexForRing();
				int minElevation = getRingFrontImage().getHeight();
				int maxElevation = Math.round(getWavePixelBottom(waveIndex) * 0.95f);
				int elevationAboveWave = getRandomizer().drawIntegerNumber(minElevation, maxElevation);
				int hueIndex = drawHueIndexForRing();
				long t0 = elapsedTimeMillis;
				long t1 = t0 + getRandomizer().drawLongIntegerNumber(2000L, 3000L);
				ring = new Ring(waveIndex, centerX, elevationAboveWave, t0, t1, hueIndex);
			}
		} while (ring == null && attempt++ < 5);
		return ring;
	}

	protected int drawHueIndexForRing() {
		Set<Integer> indices = new HashSet<Integer>(ringHues.length);
		for (int i = 0; i < ringHues.length; i++)
			indices.add(i);
		for (Ring ring : getRings().values())
			indices.remove(ring.getHueIndex());
		for (Ring ring : getHitRingsToAdd())
			indices.remove(ring.getHueIndex());
		if (indices.isEmpty()) {
			return getRandomizer().drawIntegerNumber(0, ringHues.length - 1);
		} else if (indices.size() == 1) {
			return indices.iterator().next();
		} else {
			List<Integer> indicesList = new Vector<Integer>(indices);
			return indicesList.get(getRandomizer().drawIntegerNumber(0, indicesList.size() - 1));
		}
	}

	private int drawFreeWaveIndexForRing() {
		int n = getWaveCount() - 1;
		if (getRings().size() >= n) {
			return -1; // no free index
		} else {
			List<Integer> freeIndices = new Vector<Integer>(n - getRings().size());
			for (int i = 0; i < n; i++) {
				if (!getRings().containsKey(i))
					freeIndices.add(i);
			}
			return freeIndices.get(getRandomizer().drawIntegerNumber(0, freeIndices.size() - 1));
		}
	}

	private boolean isCenterOutsideDolphinsReach(int centerX) {
		for (Dolphin dolphin : getDolphins().values()) {
			Jump jump = dolphin.getJump();
			if (jump != null && jump.getTop() >= minHeightForRing) {
				int cx = dolphin.getJumpCenterX();
				int minDistance = Math.round(jump.getHorizontalDistance() / 2f * 0.8f * getPortholePixelWidth());
				if (Math.abs(centerX - cx) < minDistance)
					return false;
			}
		}
		return true;
	}

	private boolean isCenterOutsideMissedRingsReach(int centerX, Jump jump) {
		int minDistance = Math.round(jump.getHorizontalDistance() / 2f * 0.8f * getPortholePixelWidth());
		for (Ring ring : getRings().values()) {
			if (ring.isTypeMiss()) {
				if (Math.abs(centerX - ring.getCenterX()) < minDistance)
					return false;
			}
		}
		return true;
	}

	private boolean isCenterOutsideRingsReach(int centerX) {
		int minDistance = Math.round(0.2f * getPortholePixelWidth());
		for (Ring ring : getRings().values()) {
			if (Math.abs(centerX - ring.getCenterX()) < minDistance)
				return false;
		}
		return true;
	}

	private int getHitRingsCount() {
		int n = 0;
		for (Ring ring : getRings().values())
			if (ring.isTypeHit())
				n++;
		return n + getHitRingsToAdd().size();
	}

	private int getMissedRingsCount() {
		int n = 0;
		for (Ring ring : getRings().values())
			if (ring.isTypeMiss())
				n++;
		return n;
	}

	private SpriteImage getRingBackImage() {
		if (ringBackImage == null) {
			ringBackImage = new SpriteImageRLE(19, 43,
					new int[] { -2, -2, -2, -2, -2, -2, -1, 10, 2, 2, 1, 4, -2, -1, 11, 2, 3, 1, 2, -2, -1, 12, 2, 3, 1,
							1, 0, 1, -2, -1, 12, 2, 4, 0, 1, -2, -1, 12, 2, 5, -2, -1, 13, 2, 4, -2, -1, 13, 2, 4, 0, 1,
							-2, -1, 13, 2, 4, 0, 1, -2, -1, 13, 2, 5, -2, -1, 13, 2, 5, -2, -1, 14, 2, 4, -2, -1, 14, 2,
							4, 0, 1, -2, -1, 14, 2, 4, 0, 1, -2, -1, 14, 2, 4, 0, 1, -2, -1, 14, 2, 4, 0, 1, -2, -1, 14,
							2, 4, 0, 1, -2, -1, 14, 2, 4, 0, 1, -2, -1, 14, 2, 4, 0, 1, -2, -1, 14, 2, 4, 0, 1, -2, -1,
							14, 2, 4, 0, 1, -2, -1, 14, 2, 4, 0, 1, -2, -1, 14, 2, 4, -2, -1, 13, 2, 5, -2, -1, 13, 2,
							4, 0, 1, -2, -1, 13, 2, 4, 0, 1, -2, -1, 13, 2, 4, 0, 1, -2, -1, 12, 0, 1, 2, 4, 0, 1, -2,
							-1, 12, 2, 4, 1, 1, -2, -1, 12, 2, 4, 1, 1, -2, -1, 11, 2, 4, 1, 2, -2, -1, 10, 2, 4, 1, 2,
							-2, -2, -2, -2, -2, -2 });
		}
		return ringBackImage;
	}

	private SpriteImage getRingFrontImage() {
		if (ringFrontImage == null) {
			ringFrontImage = new SpriteImageRLE(19, 43,
					new int[] { -1, 8, 0, 2, -2, -1, 6, 0, 2, 1, 2, 0, 2, -2, -1, 5, 2, 1, 1, 3, 3, 3, 0, 1, -2, -1, 4,
							2, 1, 1, 3, 3, 5, 1, 1, -2, -1, 3, 0, 1, 2, 1, 1, 2, 3, 1, 1, 7, -2, -1, 3, 2, 1, 1, 11, -2,
							-1, 3, 2, 1, 1, 6, -2, -1, 2, 2, 1, 1, 5, -2, -1, 2, 2, 1, 1, 5, -2, -1, 1, 0, 1, 2, 1, 1,
							4, -2, -1, 1, 2, 2, 1, 4, -2, -1, 1, 2, 1, 1, 5, -2, -1, 1, 2, 1, 1, 5, -2, 0, 1, 2, 1, 1,
							4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2,
							1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2,
							2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, -2, 2, 2, 1, 4, 0, 1, -2, 0, 1, 2, 2, 1, 3, 0, 1,
							-2, -1, 1, 2, 2, 1, 4, -2, -1, 1, 2, 2, 1, 4, -2, -1, 1, 2, 2, 1, 4, -2, -1, 1, 0, 1, 2, 2,
							1, 4, -2, -1, 2, 2, 2, 1, 4, -2, -1, 2, 2, 2, 1, 5, -2, -1, 2, 0, 1, 2, 2, 1, 5, -2, -1, 3,
							2, 2, 1, 11, -2, -1, 4, 2, 2, 1, 9, -2, -1, 4, 2, 2, 1, 8, 0, 1, -2, -1, 5, 2, 2, 1, 6, 0,
							1, -2, -1, 6, 2, 2, 1, 4, 0, 1, -2, -1, 8, 2, 2, 0, 1 });
		}
		return ringFrontImage;
	}

	private ScorePointsGraphic getScorePointsGraphic(int scorePoints) {
		ScorePointsGraphic graphic = scorePointsGraphics.get(scorePoints);
		if (graphic == null) {
			graphic = new ScorePointsGraphic(scorePoints);
			scorePointsGraphics.put(scorePoints, graphic);
		}
		return graphic;
	}

	private Map<Integer, Ring> getRings() {
		return rings;
	}

	private List<Ring> getHitRingsToAdd() {
		return hitRingsToAdd;
	}

	private PerpetualApproximatingFunction2D getMaxHitRingCountFunction() {
		return maxHitRingCountFunction;
	}

	private PerpetualApproximatingFunction2D getMaxMissedRingCountFunction() {
		return maxMissedRingCountFunction;
	}

	private PerpetualApproximatingFunction2D getScoreTransparencyFunction() {
		return scoreTransparencyFunction;
	}

	protected int getScore() {
		return score;
	}

	private void setScore(int score) {
		this.score = score % WRAPPED_RING_SCORE;
	}

	private long getEarliestNextMissedRingTimeMillis() {
		return earliestNextMissedRingTimeMillis;
	}

	private void setEarliestNextMissedRingTimeMillis(long timeMillis) {
		this.earliestNextMissedRingTimeMillis = timeMillis;
	}

	protected class Ring extends Sprite {

		private int waveIndex;

		private int centerX;

		private int elevationAboveWave; // center of ring

		private long startTimeMillis;

		private long endTimeMillis;

		private long scoreTimeMillis;

		private int scorePoints;

		private int hueIndex;

		private Color baseColor;

		private boolean hit;

		public Ring(int waveIndex, int centerX, int elevationAboveWave, long startTimeMillis, long endTimeMillis,
				int hueIndex) {
			this(waveIndex, centerX, elevationAboveWave, startTimeMillis, endTimeMillis, hueIndex, 0);
		}

		public Ring(int waveIndex, int centerX, int elevationAboveWave, long startTimeMillis, long endTimeMillis,
				int hueIndex, int scorePoints) {
			super(getRingBackImage(), new SpriteColorMapAlphaComposite(createRingColors(ringHues[hueIndex])));
			this.waveIndex = waveIndex;
			this.centerX = centerX;
			this.elevationAboveWave = elevationAboveWave;
			this.startTimeMillis = startTimeMillis;
			this.endTimeMillis = endTimeMillis;
			this.scorePoints = scorePoints;
			this.hueIndex = hueIndex;
			this.baseColor = getColorMap().getColor(0);
			setX(centerX - getImage().getWidth() / 2);
			getColorMap().changeTransparencyFactor(1f);
		}

		public void update(long elapsedTimeMillis) {
			int yb = getWavePixelBottom(getWaveIndex()) + 2;
			int yc = yb - getElevationAboveWave();
			float tra = 0.2f;
			if (elapsedTimeMillis < getStartTimeMillis() + 400L) {
				float s = (elapsedTimeMillis - getStartTimeMillis()) / 400f;
				float sy = (float) Math.sqrt(s);
				setY((int) Math.round((1f - sy) * -getImage().getHeight() + sy * yc));
				tra += (1f - tra) * (1f - s);
			} else if (isTypeMiss() && elapsedTimeMillis > getEndTimeMillis() - 400L) {
				float s = Math.min((elapsedTimeMillis - getEndTimeMillis() + 400L) / 400f, 1f);
				float sy = s * s;
				setY((int) Math.round((1f - sy) * yc + sy * yb));
				setRotationDegrees(2f * Math.min(s, 0.5f) * 90f);
			} else {
				if (isHit()) {
					long dt = elapsedTimeMillis - getScoreTimeMillis();
					tra = 1f - 1f * Math.round(dt / 100f) % 2; // flashing ring
				} else {
					setY(yc + (int) Math.round(3.0 * Math.sin((elapsedTimeMillis - 400L) / 150.0)));
				}
			}
			getColorMap().changeTransparencyFactor(tra);
		}

		public void drawBack(Graphics2D g) {
			changeImage(getRingBackImage());
			super.draw(g);
		}

		public void drawFront(Graphics2D g) {
			changeImage(getRingFrontImage());
			super.draw(g);
		}

		public void drawScorePoints(Graphics2D g, long elapsedTimeMillis) {
			int cx = getCenterX();
			int cy = getY() + getImage().getHeight() / 2;
			float r = Math.max((elapsedTimeMillis - getScoreTimeMillis() - 250L) / 750f, 0f);
			float scale = 1f + r * r * 8f;
			float tra = Math.min(r * r, 1f);
			g.setBackground(ColorUtils
					.setTransparency(toMonitorColor(ColorUtils.adjustBrightness(getBaseColor(), -0.5f)), tra));
			g.setColor(ColorUtils.setTransparency(getBaseColor(), tra));
			getScorePointsGraphic(getScorePoints()).draw(g, cx, cy, scale);
		}

		public void hit(Dolphin dolphin, long elapsedTimeMillis) {
			if (isTypeHit()) {
				setHit(true);
				setScoreTimeMillis(elapsedTimeMillis);
				setScore(getScore() + getScorePoints());
			}
		}

		public long getFinalTimeMillis() {
			long time = getEndTimeMillis();
			if (isHit())
				time = Math.max(time, getScoreTimeMillis() + 1000L);
			return time;
		}

		@Override
		public SpriteColorMapAlphaComposite getColorMap() {
			return (SpriteColorMapAlphaComposite) super.getColorMap();
		}

		public boolean isTypeMiss() {
			return !isTypeHit();
		}

		public boolean isTypeHit() {
			return getScorePoints() > 0;
		}

		public int getWaveIndex() {
			return waveIndex;
		}

		public int getCenterX() {
			return centerX;
		}

		private int getElevationAboveWave() {
			return elevationAboveWave;
		}

		public long getStartTimeMillis() {
			return startTimeMillis;
		}

		public long getEndTimeMillis() {
			return endTimeMillis;
		}

		public long getScoreTimeMillis() {
			return scoreTimeMillis;
		}

		private void setScoreTimeMillis(long timeMillis) {
			this.scoreTimeMillis = timeMillis;
		}

		public int getScorePoints() {
			return scorePoints;
		}

		public int getHueIndex() {
			return hueIndex;
		}

		public Color getBaseColor() {
			return baseColor;
		}

		public boolean isHit() {
			return hit;
		}

		private void setHit(boolean hit) {
			this.hit = hit;
		}

	}

	private class ScorePointsGraphic {

		private int scorePoints;

		private List<Point> dots = new Vector<Point>(8 * 8);

		public ScorePointsGraphic(int scorePoints) {
			this.scorePoints = scorePoints;
			initDots();
		}

		private void initDots() {
			String label = getLabel();
			BufferedImage img = ImageUtils.createImage(8 * label.length(), 8);
			Graphics2D g = img.createGraphics();
			AmstradSymbolRenderer renderer = getSymbolRenderer(g);
			renderer.color(Color.BLACK);
			renderer.drawStr(label, 0, 0);
			g.dispose();
			for (int y = 0; y < img.getHeight(); y++) {
				for (int x = 0; x < img.getWidth(); x++) {
					if (img.getRGB(x, y) != 0) {
						getDots().add(new Point(x, y));
					}
				}
			}
			img.flush();
		}

		public void draw(Graphics2D g, int centerX, int centerY, float scale) {
			Color c0 = g.getBackground();
			Color c1 = g.getColor();
			float x0 = -getLabel().length() * 4 * scale;
			float y0 = -4 * scale;
			for (int i = 0; i < getDots().size(); i++) {
				Point p = getDots().get(i);
				int x = centerX + Math.round(x0 + p.x * scale);
				int y = centerY + Math.round(y0 + p.y * scale);
				g.setColor(c0);
				g.drawRect(x, y, 1, 1);
				g.setColor(c1);
				g.drawRect(x, y, 0, 0);
			}
		}

		public int getScorePoints() {
			return scorePoints;
		}

		private String getLabel() {
			return "+" + String.valueOf(getScorePoints());
		}

		private List<Point> getDots() {
			return dots;
		}

	}

}