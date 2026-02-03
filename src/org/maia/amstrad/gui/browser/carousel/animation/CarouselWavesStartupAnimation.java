package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.UIResources;
import org.maia.swing.animate.wave.PixelatedWavesComponent;
import org.maia.swing.animate.wave.Wave;
import org.maia.swing.animate.wave.WaveDynamics;
import org.maia.swing.animate.wave.WavesComponent;
import org.maia.swing.animate.wave.WavesOverlayAdapter;
import org.maia.swing.animate.wave.impl.AgitatedWaveDynamics;

public class CarouselWavesStartupAnimation extends CarouselStartupAnimation {

	private WavesComponent wavesComponent;

	private int pixelSize;

	private static PortholeMaskOverlay maskOverlay = new PortholeMaskOverlay();

	public CarouselWavesStartupAnimation() {
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		Dimension size = deriveWavesComponentSize(displayWidth, displayHeight);
		setPixelSize(derivePixelSize(size));
		setWavesComponent(createWavesComponent(size));
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		float alpha = Math.min((elapsedTimeMillis - 200L) / 1000f, 1f);
		if (alpha > 0f) {
			WavesComponent comp = getWavesComponent();
			int x = (displayWidth - comp.getWidth()) / 2;
			int y = (displayHeight - comp.getHeight()) / 2;
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(x, y); // center in display
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			comp.getUI().paint(g2);
			g2.dispose();
		}
	}

	private Dimension deriveWavesComponentSize(int displayWidth, int displayHeight) {
		int s = 40 + (int) Math.round(Math.sqrt(displayHeight) * 6.0);
		s = Math.min(Math.min(s, displayHeight - 8), displayWidth - 8);
		return new Dimension(s, s);
	}

	private int derivePixelSize(Dimension size) {
		return Math.max(1, (int) Math.floor(size.getWidth() / 64.0));
	}

	private WavesComponent createWavesComponent(Dimension size) {
		int ps = getPixelSize();
		PixelatedWavesComponent comp = new PixelatedWavesComponent(size, new Color(10, 10, 10), ps, ps, 1.0, 3000L);
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(10, 19, 26)));
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(15, 27, 37)));
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(14, 31, 44)));
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(14, 46, 83)));
		comp.addWave(new Wave(0f, 0.5f, 1.0f, 0.2f, new Color(13, 56, 108)));
		comp.setWaveDynamics(createWaveDynamics(comp));
		comp.addWavesOverlay(new SpriteOverlay());
		comp.addWavesOverlay(maskOverlay);
		comp.setAntialiasingPixels(true);
		comp.setRepaintClientDriven(true);
		return comp;
	}

	private WaveDynamics createWaveDynamics(WavesComponent comp) {
		AgitatedWaveDynamics dynamics = new AgitatedWaveDynamics(comp, 0.7f);
		dynamics.setAgitationLevelProgression(AgitatedWaveDynamics.createRandomLevelProgression());
		dynamics.setPerspectiveLiftMaximum(0.1f);
		dynamics.getWavelengthRange().setRange(4f, 10f);
		return dynamics;
	}

	@Override
	public Color getDisplayBackgroundColor() {
		return Color.BLACK;
	}

	private WavesComponent getWavesComponent() {
		return wavesComponent;
	}

	private void setWavesComponent(WavesComponent component) {
		this.wavesComponent = component;
	}

	private int getPixelSize() {
		return pixelSize;
	}

	private void setPixelSize(int pixelSize) {
		this.pixelSize = pixelSize;
	}

	private static class PortholeMaskOverlay extends WavesOverlayAdapter {

		private BufferedImage mask;

		public PortholeMaskOverlay() {
			this.mask = UIResources.loadImage("porthole-gradientmask400.png");
		}

		@Override
		public void paintOverWave(Graphics2D g, int waveIndex, WavesComponent component) {
			if (waveIndex == component.getWaveCount() - 1) {
				g.setComposite(AlphaComposite.SrcOver);
				g.drawImage(getMask(), 0, 0, component.getWidth(), component.getHeight(), null);
			}
		}

		private BufferedImage getMask() {
			return mask;
		}

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