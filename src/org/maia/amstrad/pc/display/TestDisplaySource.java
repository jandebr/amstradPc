package org.maia.amstrad.pc.display;

import java.awt.Graphics2D;

public class TestDisplaySource extends AbstractDisplaySource {

	public TestDisplaySource() {
	}

	@Override
	protected void renderContent(Graphics2D g2, AmstradGraphicsContext graphicsContext) {
		AmstradSystemColors colors = graphicsContext.getSystemColors();
		g2.drawString("Hello", 0, 16);
		g2.setColor(colors.getInk(15));
		g2.drawLine(0, 0, 640, 400);
		g2.drawLine(0, 400, 640, 0);
	}

}