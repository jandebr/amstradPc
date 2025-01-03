package org.maia.amstrad.gui.browser.carousel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.pc.monitor.display.source.AmstradAwtDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.swing.FillMode;

public class CarouselProgramBrowserDisplaySource extends AmstradAwtDisplaySource
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
	protected void renderContent(Graphics2D g, int width, int height) {
		super.renderContent(g, width, height);
		g.setColor(Color.WHITE);
		g.drawString("Under construction", 100, 100);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new BorderLayout();
	}

	@Override
	protected void buildUI() {
		add(createLabel("Full", new Color(100, 100, 120)), BorderLayout.WEST);
		add(suitableForIncrementalPainting(createLabel("Incremental", new Color(0, 0, 255, 1)), FillMode.FIT),
				BorderLayout.CENTER);
	}

	private JLabel createLabel(String text, Color bg) {
		JLabel label = new JLabel(text);
		label.setOpaque(true);
		label.setBackground(bg);
		label.setForeground(Color.YELLOW);
		label.setFont(label.getFont().deriveFont(40f));
		label.setSize(label.getPreferredSize());
		return label;
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