package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.AgitationLevel;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.TimeRange;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.ValueRange;

public class CarouselTropicWavesAnimation extends CarouselWavesAnimation {

	public CarouselTropicWavesAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		// TODO make sprites
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
	protected double getColorScalingFunctionLinearity() {
		return 0.2;
	}

}