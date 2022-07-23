package org.maia.amstrad.pc.display.browser;

import java.awt.Graphics2D;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.AmstradEmulatedDisplaySource;
import org.maia.amstrad.pc.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.display.AmstradSystemColors;

public class ProgramBrowserDisplaySource extends AmstradEmulatedDisplaySource {

	public ProgramBrowserDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected void renderContent(Graphics2D g2, AmstradGraphicsContext graphicsContext) {
		AmstradSystemColors colors = graphicsContext.getSystemColors();
		g2.drawString("Hello", 0, 16);
		g2.setColor(colors.getColor(15));
		g2.drawLine(0, 0, 640, 400);
		g2.drawLine(0, 400, 640, 0);
	}

}