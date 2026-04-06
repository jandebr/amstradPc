package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class CarouselDragonFlightAnimation extends CarouselPortholePixelatedAnimation {

	public CarouselDragonFlightAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
	}

	@Override
	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		super.renderInPorthole(g, elapsedTimeMillis);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.scale(getPixelSize(), getPixelSize());
		renderDragon(g2, elapsedTimeMillis);
		g2.dispose();
	}

	protected void renderDragon(Graphics2D g, long elapsedTimeMillis) {
		// TODO
	}

	@Override
	protected Panorama createPanorama() {
		Landscape landscape = new Landscape(toMonitorColors(loadPixelatedImage("animations/dragon/dragon-480x370.png")),
				1.0f, 1.0f);
		return new Panorama(Color.BLACK, null, landscape);
	}

	@Override
	protected int getTargetPixelWidth() {
		return 160;
	}

}