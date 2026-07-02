package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.amstrad.gui.browser.carousel.animation.CarouselBaseAnimation;

public class RocketTestAnimation extends CarouselBaseAnimation implements CarouselStartupAnimation {

	public RocketTestAnimation() {
		setMinimumDurationMillis(120000L);
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		int w = Math.round(0.6f * displayWidth);
		int h = Math.round(0.2f * w);
		int x = (displayWidth - w) / 2;
		int y = (displayHeight - h) / 2;
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(x, y);
		renderBox(g2, w, h);
		g2.dispose();
	}

	private void renderBox(Graphics2D g, int width, int height) {
		int xc = width / 2;
		int yc = height / 2;
		g.setColor(Color.BLUE);
		g.drawLine(-20, yc, width + 20, yc);
		g.drawLine(xc, -20, xc, height + 20);
		g.setColor(Color.DARK_GRAY);
		g.drawRect(0, 0, width, height);
	}

	@Override
	public Color getDisplayBackgroundColor() {
		return Color.BLACK;
	}

}