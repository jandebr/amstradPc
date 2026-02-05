package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.swing.animate.wave.PixelatedWavesComponent;
import org.maia.swing.animate.wave.Wave;
import org.maia.swing.animate.wave.WaveDynamics;
import org.maia.swing.animate.wave.WavesComponent;
import org.maia.swing.animate.wave.WavesOverlayAdapter;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.AgitationLevel;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.TimeRange;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics.ValueRange;

public class CarouselPixelatedWavesAnimation extends CarouselPortholeStartupAnimation {

	private int pixelSize;

	private WavesComponent wavesComponent;

	public CarouselPixelatedWavesAnimation() {
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setPixelSize(derivePixelSize());
		setWavesComponent(createWavesComponent());
	}

	@Override
	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		getWavesComponent().getUI().paint(g);
	}

	protected int derivePixelSize() {
		return Math.max(1, (int) Math.floor(getPortholeSize().getWidth() / 64.0));
	}

	protected WavesComponent createWavesComponent() {
		PixelatedWavesComponent comp = new PixelatedWavesComponent(getPortholeSize(), new Color(10, 10, 10),
				getPixelSize(), getPixelSize(), 1.0, 3000L);
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(10, 19, 26)));
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(15, 27, 37)));
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(14, 31, 44)));
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(14, 46, 83)));
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(13, 56, 108)));
		comp.setWaveDynamics(createWaveDynamics(comp));
		comp.addWavesOverlay(new SpriteOverlay());
		comp.setAntialiasingPixels(true);
		comp.setRepaintClientDriven(true);
		return comp;
	}

	protected WaveDynamics createWaveDynamics(WavesComponent comp) {
		AgitatedWaveDynamics dynamics = new AgitatedWaveDynamics(comp, 0.7f);
		dynamics.clearAgitationLevels();
		dynamics.addAgitationLevel(new AgitationLevel(new ValueRange(0, 0.1f), new TimeRange(3000L, 6000L)));
		dynamics.addAgitationLevel(new AgitationLevel(new ValueRange(0.1f, 0.2f), new TimeRange(3000L, 6000L)));
		dynamics.addAgitationLevel(new AgitationLevel(new ValueRange(0.2f, 0.3f), new TimeRange(2000L, 4000L)));
		dynamics.setAgitationLevelProgression(AgitatedWaveDynamics.createRandomLevelProgression(false));
		dynamics.setElevationMaximum(0.2f);
		dynamics.setPerspectiveLiftMaximum(0.1f);
		dynamics.getWavelengthRange().setRange(4f, 10f);
		return dynamics;
	}

	protected int getPixelSize() {
		return pixelSize;
	}

	private void setPixelSize(int pixelSize) {
		this.pixelSize = pixelSize;
	}

	protected WavesComponent getWavesComponent() {
		return wavesComponent;
	}

	private void setWavesComponent(WavesComponent component) {
		this.wavesComponent = component;
	}

	private class SpriteOverlay extends WavesOverlayAdapter {

		public SpriteOverlay() {
		}

		@Override
		public void paintOverWave(Graphics2D g, int waveIndex, WavesComponent component) {
			if (waveIndex == component.getWaveCount() - 2) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(component.getWidth() / 2, component.getHeight() / 2); // center in porthole
				g2.scale(getPixelSize(), getPixelSize());
				g2.setColor(Color.WHITE);
				g2.drawLine(-16, -12, 16, -12);
				g2.drawLine(16, -12, 16, 12);
				g2.drawLine(16, 12, -16, 12);
				g2.drawLine(-16, 12, -16, -12);
				g2.dispose();
			}
		}

	}

}