package org.maia.amstrad.gui.browser.carousel.animation.startup.waves;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.SpriteImageRLE;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D.ControlValueGenerator;

public class CarouselArcticWavesAnimation extends CarouselWavesAnimation {

	private SpriteImage orcaFinImage;

	private SpriteImage penguinFrontImage;

	private SpriteImage penguinLeftImage;

	private OrcaFin orcaFin;

	private Map<Integer, Penguin> penguins = new HashMap<Integer, Penguin>(); // indexed by wave index

	private static int orcaFinWaveIndex = 1;

	public CarouselArcticWavesAnimation(AmstradGraphicsContext graphicsContext) {
		super(graphicsContext);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setOrcaFin(new OrcaFin());
		initPenguins();
	}

	protected void initPenguins() {
		int n = getPenguinCount();
		for (int waveIndex = 0; waveIndex < n; waveIndex++) {
			Penguin penguin = new Penguin(waveIndex);
			getPenguins().put(waveIndex, penguin);
		}
	}

	protected SpriteColorMap createOrcaFinColors() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, new Color(10, 10, 10));
		return toMonitorColors(colorMap);
	}

	protected SpriteColorMap createPenguinColors() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, new Color(0, 0, 0));
		colorMap.setColor(1, new Color(240, 240, 247));
		return toMonitorColors(colorMap);
	}

	@Override
	protected Panorama createPanorama() {
		float bl = getWavesBaseline();
		Landscape landscape = new Landscape(
				toMonitorColors(loadPixelatedImage("animations/waves/arctic-landscape-800x150.png")), bl,
				Math.max(bl - 0.2f, 0.1f));
		return new Panorama(toMonitorColor(new Color(130, 167, 190)),
				toMonitorColors(loadPixelatedImage("animations/waves/arctic-sky-8x150.png")), landscape);
	}

	@Override
	protected void renderPixelatedWaveOverlay(Graphics2D g, int waveIndex, long elapsedTimeMillis) {
		Penguin penguin = getPenguins().get(waveIndex);
		if (penguin != null) {
			renderPenguin(g, penguin, elapsedTimeMillis);
		}
		if (waveIndex == orcaFinWaveIndex) {
			renderOrcaFin(g, getOrcaFin(), elapsedTimeMillis);
		}
	}

	protected void renderPenguin(Graphics2D g, Penguin penguin, long elapsedTimeMillis) {
		penguin.update(elapsedTimeMillis);
		penguin.draw(g);
	}

	protected void renderOrcaFin(Graphics2D g, OrcaFin orcaFin, long elapsedTimeMillis) {
		orcaFin.update(elapsedTimeMillis);
		orcaFin.draw(g);
	}

	protected int getPenguinCount() {
		return 2;
	}

	private SpriteImage getOrcaFinImage() {
		if (orcaFinImage == null) {
			orcaFinImage = new SpriteImageRLE(23, 16,
					new int[] { -1, 12, 0, 3, -2, -1, 10, 0, 5, -2, -1, 9, 0, 6, -2, -1, 8, 0, 7, -2, -1, 7, 0, 9, -2,
							-1, 6, 0, 10, -2, -1, 6, 0, 10, -2, -1, 5, 0, 11, -2, -1, 5, 0, 11, -2, -1, 4, 0, 12, -2,
							-1, 4, 0, 12, -2, -1, 3, 0, 14, -2, -1, 3, 0, 15, -2, -1, 2, 0, 17, -2, -1, 2, 0, 19, -2, 0,
							23 });
		}
		return orcaFinImage;
	}

	private SpriteImage getPenguinFrontImage() {
		if (penguinFrontImage == null) {
			penguinFrontImage = new SpriteImageRLE(16, 24,
					new int[] { -1, 6, 0, 4, -2, -1, 5, 0, 6, -2, -1, 4, 0, 2, 1, 4, 0, 2, -2, -1, 4, 0, 1, 1, 1, 0, 1,
							1, 2, 0, 1, 1, 1, 0, 1, -2, -1, 4, 0, 1, 1, 6, 0, 1, -2, -1, 4, 0, 2, 1, 1, 0, 2, 1, 1, 0,
							2, -2, -1, 5, 0, 1, 1, 4, 0, 1, -2, -1, 4, 0, 8, -2, -1, 3, 0, 10, -2, -1, 2, 0, 12, -2, -1,
							2, 0, 4, 1, 4, 0, 4, -2, -1, 1, 0, 5, 1, 4, 0, 5, -2, -1, 1, 0, 4, 1, 6, 0, 4, -2, 0, 5, 1,
							6, 0, 5, -2, -1, 3, 0, 2, 1, 6, 0, 2, -2, -1, 3, 0, 2, 1, 6, 0, 2, -2, -1, 3, 0, 2, 1, 6, 0,
							2, -2, -1, 3, 0, 2, 1, 6, 0, 2, -2, -1, 4, 0, 1, 1, 6, 0, 1, -2, -1, 4, 0, 1, 1, 6, 0, 1,
							-2, -1, 4, 0, 2, 1, 4, 0, 2, -2, -1, 5, 0, 1, 1, 4, 0, 1, -2, -1, 3, 0, 10, -2, -1, 1, 0, 5,
							-1, 4, 0, 5 });
		}
		return penguinFrontImage;
	}

	private SpriteImage getPenguinLeftImage() {
		if (penguinLeftImage == null) {
			penguinLeftImage = new SpriteImageRLE(8, 24,
					new int[] { -1, 3, 0, 3, -2, -1, 2, 0, 5, -2, -1, 1, 0, 1, 1, 3, 0, 3, -2, 0, 2, 1, 1, 0, 1, 1, 2,
							0, 2, -2, 0, 2, 1, 4, 0, 2, -2, -1, 2, 0, 1, 1, 2, 0, 3, -2, -1, 3, 0, 4, -2, -1, 3, 0, 4,
							-2, -1, 2, 0, 5, -2, -1, 2, 0, 6, -2, -1, 1, 0, 7, -2, -1, 1, 0, 2, 1, 1, 0, 2, 1, 1, 0, 1,
							-2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 1, 1, 0, 1, -2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2,
							-2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, -2, 0, 1, 1, 2, 0, 1, 1, 1, 0, 3, -2, 0, 1, 1,
							2, 0, 5, -2, -1, 1, 0, 1, 1, 1, 0, 5, -2, -1, 1, 0, 1, 1, 1, 0, 5, -2, -1, 2, 0, 5, -2, -1,
							2, 0, 5, -2, -1, 2, 0, 5, -2, -1, 1, 0, 5, -2, 0, 6 });
		}
		return penguinLeftImage;
	}

	protected OrcaFin getOrcaFin() {
		return orcaFin;
	}

	private void setOrcaFin(OrcaFin orcaFin) {
		this.orcaFin = orcaFin;
	}

	protected Map<Integer, Penguin> getPenguins() {
		return penguins;
	}

	protected class OrcaFin extends Sprite {

		private PerpetualApproximatingFunction2D descendFunction;

		private PerpetualApproximatingFunction2D positionFunction;

		public OrcaFin() {
			super(getOrcaFinImage(), createOrcaFinColors());
			this.descendFunction = createDescendFunction();
			this.positionFunction = createPositionFunction();
		}

		public void update(long elapsedTimeMillis) {
			double t = elapsedTimeMillis / 500.0;
			int yUpper = getWavePixelBottom(orcaFinWaveIndex + 1) - getHeight();
			int yDescend = (int) Math.round(getDescendFunction().evaluate(t) * getHeight() / 2.0);
			int y = yUpper + yDescend;
			int xOld = getX();
			int x = (int) Math.round(getPositionFunction().evaluate(t) * getPortholePixelWidth());
			if ((x > xOld && !isMirroredX()) || (x < xOld && isMirroredX())) {
				flipX();
			}
			move(x, y);
		}

		public int getCenterX() {
			return getX() + getImage().getWidth() / 2;
		}

		private PerpetualApproximatingFunction2D createDescendFunction() {
			return PerpetualApproximatingFunction2D.createQuadraticApproximatingFunction(new ControlValueGenerator() {

				@Override
				public double generateControlValue() {
					return getRandomizer().drawDoubleUnitNumber();
				}
			});
		}

		private PerpetualApproximatingFunction2D createPositionFunction() {
			return PerpetualApproximatingFunction2D
					.createQuadraticApproximatingFunction(new OrcaFinPositionGenerator());
		}

		private PerpetualApproximatingFunction2D getDescendFunction() {
			return descendFunction;
		}

		private PerpetualApproximatingFunction2D getPositionFunction() {
			return positionFunction;
		}

	}

	private class OrcaFinPositionGenerator implements ControlValueGenerator {

		private double previousPosition;

		private double previousDirection;

		private double targetPosition;

		private boolean targetPositionDefined;

		public OrcaFinPositionGenerator() {
			setPreviousPosition(0.5);
			setPreviousDirection(drawDirection());
		}

		@Override
		public double generateControlValue() {
			double pos = getPreviousPosition();
			double dir = getPreviousDirection();
			double delta = 0.1 + 0.2 * getRandomizer().drawDoubleUnitNumber();
			if ((pos <= -0.15 || pos >= 1.1) && !isTargetPositionDefined()) {
				setTargetPosition(0.3 + 0.4 * getRandomizer().drawDoubleUnitNumber());
				setTargetPositionDefined(true);
			}
			if (isTargetPositionDefined()) {
				// move towards target
				double target = getTargetPosition();
				dir = Math.signum(target - pos);
				pos += dir * delta;
				if (pos == target || dir == Math.signum(pos - target)) {
					setTargetPositionDefined(false);
				}
			} else {
				if (getRandomizer().drawBoolean())
					dir = drawDirection();
				pos += dir * delta;
			}
			setPreviousPosition(pos);
			setPreviousDirection(dir);
			return pos;
		}

		private double drawDirection() {
			return getRandomizer().drawBoolean() ? -1.0 : 1.0;
		}

		private double getPreviousPosition() {
			return previousPosition;
		}

		private void setPreviousPosition(double position) {
			this.previousPosition = position;
		}

		private double getPreviousDirection() {
			return previousDirection;
		}

		private void setPreviousDirection(double direction) {
			this.previousDirection = direction;
		}

		private double getTargetPosition() {
			return targetPosition;
		}

		private void setTargetPosition(double position) {
			this.targetPosition = position;
		}

		private boolean isTargetPositionDefined() {
			return targetPositionDefined;
		}

		private void setTargetPositionDefined(boolean defined) {
			this.targetPositionDefined = defined;
		}

	}

	protected class Penguin extends Sprite {

		private int waveIndex;

		private boolean submerged;

		private PerpetualApproximatingFunction2D orientationFunction;

		private PerpetualApproximatingFunction2D descendFunction;

		public Penguin(int waveIndex) {
			super(getPenguinFrontImage(), createPenguinColors());
			this.waveIndex = waveIndex;
			this.orientationFunction = PerpetualApproximatingFunction2D
					.createLinearInterpolatingFunction(new ControlValueGenerator() {

						@Override
						public double generateControlValue() {
							return (0.1 + 0.8 * getRandomizer().drawIntegerNumber(0, 2) * 0.5)
									+ (getRandomizer().drawDoubleUnitNumber() * 0.2 - 0.1);
						}
					});
			this.descendFunction = PerpetualApproximatingFunction2D
					.createCubicApproximatingFunction(new ControlValueGenerator() {

						@Override
						public double generateControlValue() {
							return 2.0 * getRandomizer().drawDoubleUnitNumber() - 1.0;
						}
					});
			setX(drawX());
		}

		public void update(long elapsedTimeMillis) {
			updatePosition(elapsedTimeMillis);
			updateOrientation(elapsedTimeMillis);
		}

		private void updatePosition(long elapsedTimeMillis) {
			float dist = getUnitDistanceToOrcaFin();
			int yPrevious = getY();
			int yBase = getWavePixelY(getWaveIndex() + 1, getCenterX());
			int yDiveMax = 14;
			int yDive = (int) Math.round((1.0 - Math.min(Math.pow(dist / 0.6, 2.0), 1.0)) * yDiveMax);
			int yDescend = Math.round(3f * (float) getDescendFunction().evaluate(elapsedTimeMillis / 500.0));
			int yNew = yBase - 10 + yDive + yDescend;
			setY((yNew + yPrevious) / 2); // smoothing to avoid tremor
			if (isSubmerged() && yDive < yDiveMax) {
				setX(drawX()); // change position
			}
			setSubmerged(yDive == yDiveMax);
		}

		private void updateOrientation(long elapsedTimeMillis) {
			int cx = getCenterX();
			float r = (float) getOrientationFunction().evaluate(elapsedTimeMillis / 500.0);
			if (r < 0.33f) {
				lookLeft();
			} else if (r > 0.66f) {
				lookRight();
			} else {
				lookForward();
			}
			translateX(cx - getCenterX());
		}

		private int drawX() {
			return Math.round(getPortholePixelWidth() * (getRandomizer().drawFloatUnitNumber() * 1.1f - 0.05f));
		}

		private float getUnitDistanceToOrcaFin() {
			return Math.abs(getCenterX() - getOrcaFin().getCenterX()) / (float) getPortholePixelWidth();
		}

		public int getCenterX() {
			return getX() + getImage().getWidth() / 2;
		}

		private void lookForward() {
			changeImage(getPenguinFrontImage());
			if (isMirroredX())
				flipX();
		}

		private void lookLeft() {
			changeImage(getPenguinLeftImage());
			if (isMirroredX())
				flipX();
		}

		private void lookRight() {
			changeImage(getPenguinLeftImage());
			if (!isMirroredX())
				flipX();
		}

		private boolean isLookingForward() {
			return Objects.equals(getImage(), getPenguinFrontImage());
		}

		private boolean isLookingLeft() {
			return !isLookingForward() && !isMirroredX();
		}

		private boolean isLookingRight() {
			return !isLookingForward() && isMirroredX();
		}

		public int getWaveIndex() {
			return waveIndex;
		}

		private boolean isSubmerged() {
			return submerged;
		}

		private void setSubmerged(boolean submerged) {
			this.submerged = submerged;
		}

		private PerpetualApproximatingFunction2D getOrientationFunction() {
			return orientationFunction;
		}

		private PerpetualApproximatingFunction2D getDescendFunction() {
			return descendFunction;
		}

	}

}