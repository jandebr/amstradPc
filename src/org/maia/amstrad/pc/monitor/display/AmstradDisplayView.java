package org.maia.amstrad.pc.monitor.display;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public interface AmstradDisplayView {

	Graphics2D createDisplayViewport(int x, int y, int width, int height);

	FontMetrics getFontMetrics(Font font);

}