package org.maia.amstrad.pc.display;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

public interface AmstradAlternativeDisplaySource {

	void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext);

	void renderOntoDisplay(Graphics2D g2, Rectangle displayBounds, AmstradGraphicsContext graphicsContext);

	void dispose();

}