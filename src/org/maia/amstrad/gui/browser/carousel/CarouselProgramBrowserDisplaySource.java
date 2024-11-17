package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.source.AmstradAbstractDisplaySource;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;

public class CarouselProgramBrowserDisplaySource extends AmstradAbstractDisplaySource
		implements ProgramBrowserDisplaySource {

	private AmstradProgramBrowser programBrowser;

	public CarouselProgramBrowserDisplaySource(AmstradProgramBrowser programBrowser) {
		super(programBrowser.getAmstradPc());
		this.programBrowser = programBrowser;
		setRestoreMonitorSettingsOnDispose(true); // as this source switches to COLOR
		setAutoPauseResume(true);
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		super.init(displayComponent, graphicsContext);
		getAmstradPc().getMonitor().setMode(AmstradMonitorMode.COLOR);
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
		g.setColor(Color.WHITE);
		g.drawString("Under construction", 100, 100);
		// TODO
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		super.keyboardKeyPressed(e);
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ESCAPE) {
			if (!getSystemSettings().isProgramCentric()) {
				close();
			}
		}
	}

	@Override
	public AmstradProgram getCurrentProgram() {
		// TODO
		return null;
	}

	@Override
	public AmstradAlternativeDisplaySourceType getType() {
		return AmstradAlternativeDisplaySourceType.PROGRAM_BROWSER;
	}

	@Override
	public boolean isStretchToFullscreen() {
		return true;
	}

	@Override
	public AmstradProgramBrowser getProgramBrowser() {
		return programBrowser;
	}

}