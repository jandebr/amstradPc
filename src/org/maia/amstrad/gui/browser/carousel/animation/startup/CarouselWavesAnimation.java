package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.swing.animate.wave.PixelatedWavesComponent;
import org.maia.swing.animate.wave.Wave;
import org.maia.swing.animate.wave.WaveDynamics;
import org.maia.swing.animate.wave.WavesComponent;
import org.maia.swing.animate.wave.WavesOverlayAdapter;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.AgitationLevel;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.TimeRange;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.ValueRange;

public abstract class CarouselWavesAnimation extends CarouselPortholePixelatedAnimation {

	private WavesComponent wavesComponent;

	private long elapsedTimeMillis;

	protected CarouselWavesAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setWavesComponent(createWavesComponent());
		setElapsedTimeMillis(0L);
	}

	@Override
	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		super.renderInPorthole(g, elapsedTimeMillis);
		setElapsedTimeMillis(elapsedTimeMillis);
		getWavesComponent().getUI().paint(g);
	}

	protected void renderPixelatedWaveOverlay(Graphics2D g, int waveIndex, long elapsedTimeMillis) {
		// Subclasses may extend
	}

	protected WavesComponent createWavesComponent() {
		PixelatedWavesComponent comp = new PixelatedWavesComponent(getPortholeSize(), null, getPixelSize(),
				getPixelSize(), 0, 3000L);
		List<Color> waveColors = createWaveColors();
		for (Color waveColor : waveColors) {
			comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, waveColor));
		}
		comp.setWaveDynamics(createWaveDynamics(comp));
		comp.addWavesOverlay(new PixelatedWavesOverlay());
		comp.setAntialiasingPixels(true);
		comp.setRepaintClientDriven(true);
		return comp;
	}

	protected WaveDynamics createWaveDynamics(WavesComponent comp) {
		AgitatedWaveDynamics dynamics = new AgitatedWaveDynamics(comp, getWavesBaseline(), createWaveAgitationLevels(),
				AgitatedWaveDynamics.createRandomLevelProgression(), 1000L);
		dynamics.setElevationMaximum(0.2f);
		dynamics.setPerspectiveLiftMaximum(0.05f);
		dynamics.getWavelengthRange().setRange(4f, 10f);
		dynamics.getAmplitudeRange().setRange(0.05f, 0.2f);
		return dynamics;
	}

	protected List<Color> createWaveColors() {
		List<Color> colors = new Vector<Color>(5);
		colors.add(toMonitorColor(new Color(10, 19, 26)));
		colors.add(toMonitorColor(new Color(15, 27, 37)));
		colors.add(toMonitorColor(new Color(14, 31, 44)));
		colors.add(toMonitorColor(new Color(14, 46, 83)));
		colors.add(toMonitorColor(new Color(13, 56, 108)));
		return colors;
	}

	protected List<AgitationLevel> createWaveAgitationLevels() {
		List<AgitationLevel> levels = new Vector<AgitationLevel>(3);
		levels.add(new AgitationLevel(new ValueRange(0, 0.1f), new TimeRange(4000L, 4000L)));
		levels.add(new AgitationLevel(new ValueRange(0.1f, 0.2f), new TimeRange(4000L, 4000L)));
		levels.add(new AgitationLevel(new ValueRange(0.2f, 0.3f), new TimeRange(4000L, 4000L)));
		return levels;
	}

	protected float getWavesBaseline() {
		return 0.7f;
	}

	protected int getWavePixelTop() {
		int top = getWavePixelTop(0);
		for (int i = 1; i < getWaveCount(); i++) {
			top = Math.min(top, getWavePixelTop(i));
		}
		return top;
	}

	protected int getWavePixelTop(int waveIndex) {
		Wave wave = getWavesComponent().getWave(waveIndex);
		return (int) Math.floor((wave.getTranslationY() - wave.getAmplitude()) * getPortholeHeight() / getPixelSize());
	}

	protected int getWavePixelBottom() {
		int bottom = getWavePixelBottom(0);
		for (int i = 1; i < getWaveCount(); i++) {
			bottom = Math.max(bottom, getWavePixelBottom(i));
		}
		return bottom;
	}

	protected int getWavePixelBottom(int waveIndex) {
		Wave wave = getWavesComponent().getWave(waveIndex);
		return (int) Math.floor((wave.getTranslationY() + wave.getAmplitude()) * getPortholeHeight() / getPixelSize());
	}

	@Override
	protected int getTargetPixelWidth() {
		return 96;
	}

	protected int getWaveCount() {
		return getWavesComponent().getWaveCount();
	}

	protected WavesComponent getWavesComponent() {
		return wavesComponent;
	}

	private void setWavesComponent(WavesComponent component) {
		this.wavesComponent = component;
	}

	private long getElapsedTimeMillis() {
		return elapsedTimeMillis;
	}

	private void setElapsedTimeMillis(long elapsedTimeMillis) {
		this.elapsedTimeMillis = elapsedTimeMillis;
	}

	private class PixelatedWavesOverlay extends WavesOverlayAdapter {

		public PixelatedWavesOverlay() {
		}

		@Override
		public void paintOverWave(Graphics2D g, int waveIndex, WavesComponent component) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.scale(getPixelSize(), getPixelSize());
			renderPixelatedWaveOverlay(g2, waveIndex, getElapsedTimeMillis());
			g2.dispose();
		}

	}

}