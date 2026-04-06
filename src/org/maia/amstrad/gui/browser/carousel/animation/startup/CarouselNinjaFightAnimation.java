package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class CarouselNinjaFightAnimation extends CarouselPortholePixelatedAnimation {

	public CarouselNinjaFightAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
	}

	@Override
	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		super.renderInPorthole(g, elapsedTimeMillis);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.scale(getPixelSize(), getPixelSize());
		renderNinjas(g2, elapsedTimeMillis);
		g2.dispose();
	}

	protected void renderNinjas(Graphics2D g, long elapsedTimeMillis) {
		// TODO
	}

	@Override
	protected Panorama createPanorama() {
		Landscape landscape = new Landscape(toMonitorColors(loadPixelatedImage("animations/ninja/ninja478x478.png")),
				1.0f, 1.0f);
		return new Panorama(toMonitorColor(new Color(197, 195, 198)),
				toMonitorColors(loadPixelatedImage("animations/ninja/ninja-sky8x150.png")), landscape);
	}

}