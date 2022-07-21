package org.maia.amstrad.pc.display;

import java.awt.Graphics2D;

public interface AmstradPcAlternativeDisplaySource {

	void notifyPrimaryDisplaySourceResolution(int width, int height);

	void renderOntoDisplay(Graphics2D g2, int width, int height, AmstradPcGraphicsContext graphicsContext);

	void dispose();

}