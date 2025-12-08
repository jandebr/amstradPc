package org.maia.amstrad.gui.browser.carousel.cursor;

import java.awt.Graphics2D;

import org.maia.amstrad.gui.AmstradSymbolRenderer;
import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;

public class CarouselCursorSymbolRenderer implements CarouselCursorRenderer {

	private CarouselHost host;

	private AmstradSymbolRenderer symbolRenderer;

	public CarouselCursorSymbolRenderer(CarouselHost host) {
		this.host = host;
		this.symbolRenderer = new AmstradSymbolRenderer(host.getGraphicsContext(), null);
	}

	@Override
	public void render(Graphics2D gCentered, int maximumCursorWidth, int maximumCursorHeight) {
		if (!isHidden()) {
			int scale = Math.max(1, Math.floorDiv(maximumCursorHeight, 8));
			AmstradSymbolRenderer sym = getSymbolRenderer();
			sym.replaceGraphics2D(gCentered);
			sym.color(getHost().getTheme().getCarouselCursorColor());
			sym.scale(scale);
			sym.drawChr(213, -8 * scale, -4 * scale);
			sym.drawChr(212, 0, -4 * scale);
		}
	}

	protected boolean isHidden() {
		if (getHost().getRunProgramActionInProgress() != null)
			return true;
		if (getHost().getEnterFolderActionInProgress() != null)
			return true;
		return false;
	}

	protected CarouselHost getHost() {
		return host;
	}

	private AmstradSymbolRenderer getSymbolRenderer() {
		return symbolRenderer;
	}

}