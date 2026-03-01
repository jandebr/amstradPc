package org.maia.amstrad.gui.browser.carousel.animation.sprite;

import java.awt.Graphics2D;

public interface SpriteImage {

	void draw(Graphics2D g, SpriteColorMap colorMap);

	int getWidth();

	int getHeight();

}