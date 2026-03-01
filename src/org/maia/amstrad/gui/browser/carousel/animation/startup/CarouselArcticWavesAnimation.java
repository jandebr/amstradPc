package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.amstrad.gui.browser.carousel.animation.sprite.Sprite;
import org.maia.amstrad.gui.browser.carousel.animation.sprite.SpriteColorMap;
import org.maia.amstrad.gui.browser.carousel.animation.sprite.SpriteColorMapImpl;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D.ControlValueGenerator;

public class CarouselArcticWavesAnimation extends CarouselWavesAnimation {

	private OrcaFin orcaFin;

	private static int orcaFinAboveWaveIndex = 1;

	public CarouselArcticWavesAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setOrcaFin(new OrcaFin());
	}

	protected SpriteColorMap createOrcaFinColors() {
		SpriteColorMapImpl colors = new SpriteColorMapImpl();
		colors.setColor(0, toMonitorColor(new Color(10, 10, 10)));
		return colors;
	}

	@Override
	protected Panorama createPanorama() {
		float bl = getWavesBaseline();
		Landscape landscape = new Landscape(
				toMonitorColors(loadPixelatedImage("animations/arctic-mountains800x150.png")), bl,
				Math.max(bl - 0.2f, 0.1f));
		return new Panorama(toMonitorColor(new Color(130, 167, 190)),
				toMonitorColors(loadPixelatedImage("animations/arctic-sky8x150.png")), landscape);
	}

	@Override
	protected void renderPixelatedWaveOverlay(Graphics2D g, int waveIndex, long elapsedTimeMillis) {
		super.renderPixelatedWaveOverlay(g, waveIndex, elapsedTimeMillis);
		if (waveIndex == orcaFinAboveWaveIndex) {
			renderOrcaFin(g, elapsedTimeMillis);
		}
	}

	protected void renderOrcaFin(Graphics2D g, long elapsedTimeMillis) {
		getOrcaFin().update(elapsedTimeMillis);
		getOrcaFin().draw(g);
	}

	private OrcaFin getOrcaFin() {
		return orcaFin;
	}

	private void setOrcaFin(OrcaFin orcaFin) {
		this.orcaFin = orcaFin;
	}

	private class OrcaFin extends Sprite {

		private PerpetualApproximatingFunction2D descendFunction;

		private PerpetualApproximatingFunction2D positionFunction;

		public OrcaFin() {
			super(getSpriteImageCatalog().getOrcaFinSmall(), createOrcaFinColors());
			this.descendFunction = createDescendFunction();
			this.positionFunction = createPositionFunction();
		}

		public void update(long elapsedTimeMillis) {
			double t = elapsedTimeMillis / 500.0;
			int yUpper = getWavePixelBottom(orcaFinAboveWaveIndex + 1) - getHeight();
			int yDescend = (int) Math.round(getDescendFunction().evaluate(t) * getHeight() / 2.0);
			int y = yUpper + yDescend;
			int xOld = getX();
			int x = (int) Math.round(getPositionFunction().evaluate(t) * getPortholePixelWidth());
			if ((x > xOld && !isMirroredX()) || (x < xOld && isMirroredX())) {
				flipX();
			}
			move(x, y);
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

}