package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Graphics2D;

public interface CarouselAnimation {

	void init(int displayWidth, int displayHeight);

	void renderOntoDisplay(Graphics2D displayGraphics, int displayWidth, int displayHeight, long elapsedTimeMillis);

	void dispose();

	long getMinimumDelayMillis();

	long getMinimumDurationMillis();

}