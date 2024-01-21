package org.maia.amstrad.gui.carousel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.source.AmstradAbstractDisplaySource;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;

public class ProgramCarouselDisplaySource extends AmstradAbstractDisplaySource {

	public ProgramCarouselDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		int width = displayBounds.width;
		int height = displayBounds.height;
		Graphics2D g = (Graphics2D) display.create(displayBounds.x, displayBounds.y, width, height);
		renderContent(g, width, height);
		g.dispose();
	}

	protected void renderContent(Graphics2D g, int width, int height) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		// TODO
	}

	@Override
	public AmstradAlternativeDisplaySourceType getType() {
		return AmstradAlternativeDisplaySourceType.PROGRAM_CAROUSEL;
	}

	@Override
	public boolean isStretchToFullscreen() {
		return true;
	}

}