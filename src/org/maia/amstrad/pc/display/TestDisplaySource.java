package org.maia.amstrad.pc.display;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class TestDisplaySource implements AmstradPcAlternativeDisplaySource {

	private Icon icon;

	private Dimension primaryResolution;

	public TestDisplaySource() {
		icon = new ImageIcon("resources/images/amstrad5.png");
	}

	@Override
	public void notifyPrimaryDisplaySourceResolution(int width, int height) {
		primaryResolution = new Dimension(width, height);
	}

	@Override
	public void renderOntoDisplay(Graphics2D g2, int width, int height, AmstradPcGraphicsContext graphicsContext) {
		setupScaling(g2, width, height);
		g2.setColor(graphicsContext.getSystemColors().getDefaultPaperColor());
		g2.fillRect(0, 0, width, height);
		g2.setColor(graphicsContext.getSystemColors().getDefaultPenColor());
		g2.setFont(graphicsContext.getSystemFont());
		g2.drawString("Hello", 30, 40);
		icon.paintIcon(null, g2, 30, 50);
	}

	private void setupScaling(Graphics2D g2, int targetWidth, int targetHeight) {
		int primaryWidth = primaryResolution.width;
		int primaryHeight = primaryResolution.height;
		double sx = targetWidth / (double) primaryWidth;
		double sy = targetHeight / (double) primaryHeight;
		g2.scale(sx, sy);
	}

	@Override
	public void dispose() {
	}

}