package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.browser.carousel.animation.sprite.Sprite;
import org.maia.amstrad.gui.browser.carousel.animation.sprite.SpriteColorMap;
import org.maia.amstrad.gui.browser.carousel.animation.sprite.SpriteColorMapImpl;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.graphics2d.geometry.Radians;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.AgitationLevel;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.TimeRange;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.ValueRange;

public class CarouselTropicWavesAnimation extends CarouselWavesAnimation {

	private Dolphin dolphin;

	public CarouselTropicWavesAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setDolphin(new Dolphin());
	}

	protected SpriteColorMap createDolphinColors() {
		SpriteColorMapImpl colors = new SpriteColorMapImpl();
		colors.setColor(0, toMonitorColor(new Color(16, 29, 51)));
		colors.setColor(1, toMonitorColor(new Color(127, 140, 161)));
		colors.setColor(2, toMonitorColor(new Color(1, 7, 18)));
		return colors;
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
				toMonitorColors(loadPixelatedImage("animations/tropic-mountains645x120.png")), bl,
				Math.max(bl - 0.2f, 0.1f));
		return new Panorama(toMonitorColor(new Color(177, 195, 209)),
				toMonitorColors(loadPixelatedImage("animations/tropic-sky8x150.png")), landscape);
	}

	@Override
	protected void renderPixelatedWaveOverlay(Graphics2D g, int waveIndex, long elapsedTimeMillis) {
		super.renderPixelatedWaveOverlay(g, waveIndex, elapsedTimeMillis);
		if (waveIndex == 1) {
			renderDolphin(g, getDolphin(), elapsedTimeMillis);
		}
	}

	protected void renderDolphin(Graphics2D g, Dolphin dolphin, long elapsedTimeMillis) {
		dolphin.update(elapsedTimeMillis);
		dolphin.draw(g);
	}

	@Override
	protected double getColorScalingFunctionLinearity() {
		return 0.2;
	}

	private Dolphin getDolphin() {
		return dolphin;
	}

	private void setDolphin(Dolphin dolphin) {
		this.dolphin = dolphin;
	}

	private class Dolphin extends Sprite {

		private Jump jump;

		private long jumpStartTimeMillis;

		private long jumpDurationMillis;

		public Dolphin() {
			super(getSpriteImageCatalog().getDolphin(), createDolphinColors());
		}

		public void update(long elapsedTimeMillis) {
			move(20, 60);
			setRotationDegrees(elapsedTimeMillis / 5f);
			// TODO
		}

	}

	private class Jump {

		private float top; // above baseline, positive value

		private float base; // below baseline, negative value

		public Jump(float top, float base) {
			this.top = top;
			this.base = base;
		}

		public float getArcLength() {
			return 2f * (float) Math.PI * getVerticalRadius() / 360f * getArcDegrees();
		}

		public float getArcDegrees() {
			return 180f - 2f * (float) Radians.radiansToDegrees(Math.asin(-getBase() / getVerticalRadius()));
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

	}

	private class JumpProfile {

		private Range topRange;

		private Range baseRange;

		public JumpProfile(Range topRange, Range baseRange) {
			this.topRange = topRange;
			this.baseRange = baseRange;
		}

		public Jump createJump() {
			float top = drawValue(getTopRange());
			float base = drawValue(getBaseRange());
			return new Jump(top, base);
		}

		private float drawValue(Range range) {
			return range.getMin() + getRandomizer().drawFloatUnitNumber() * (range.getMax() - range.getMin());
		}

		public Range getTopRange() {
			return topRange;
		}

		public Range getBaseRange() {
			return baseRange;
		}

	}

	private static class Range {

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