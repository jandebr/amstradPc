package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.ProgramBrowserListener;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.source.AmstradAbstractDisplaySource;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.util.GenericListenerList;

public class CarouselProgramBrowserDisplaySource extends AmstradAbstractDisplaySource
		implements ProgramBrowserDisplaySource {

	private GenericListenerList<ProgramBrowserListener> browserListeners;

	public CarouselProgramBrowserDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
		this.browserListeners = new GenericListenerList<ProgramBrowserListener>();
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
	public void addListener(ProgramBrowserListener listener) {
		getBrowserListeners().addListener(listener);
	}

	@Override
	public void removeListener(ProgramBrowserListener listener) {
		getBrowserListeners().removeListener(listener);
	}

	private void notifyProgramLoaded(AmstradProgram program) {
		for (ProgramBrowserListener listener : getBrowserListeners()) {
			listener.programLoadedFromBrowser(this, program);
		}
	}

	private void notifyProgramRun(AmstradProgram program) {
		for (ProgramBrowserListener listener : getBrowserListeners()) {
			listener.programRunFromBrowser(this, program);
		}
	}

	@Override
	public AmstradAlternativeDisplaySourceType getType() {
		return AmstradAlternativeDisplaySourceType.PROGRAM_CAROUSEL;
	}

	@Override
	public boolean isStretchToFullscreen() {
		return true;
	}

	private GenericListenerList<ProgramBrowserListener> getBrowserListeners() {
		return browserListeners;
	}

}