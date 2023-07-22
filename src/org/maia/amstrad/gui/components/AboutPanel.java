package org.maia.amstrad.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class AboutPanel extends JPanel {

	private AmstradGraphicsContext graphicsContext;

	private String versionInfo;

	public AboutPanel(AmstradGraphicsContext graphicsContext, String versionInfo) {
		super(new BorderLayout());
		add(new JLabel(UIResources.aboutBackdrop), BorderLayout.CENTER);
		this.graphicsContext = graphicsContext;
		this.versionInfo = versionInfo;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHints(
				new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		paintAttribution(g2);
	}

	private void paintAttribution(Graphics2D g2) {
		Font mainFont = getGraphicsContext().getSystemFont();
		Font subFont = mainFont.deriveFont(10f).deriveFont(Font.ITALIC);
		Font versionFont = mainFont.deriveFont(8f);
		Color mainColor = Color.WHITE;
		Color subColor = new Color(237, 124, 124);
		Color versionColor = new Color(194, 176, 233);
		g2.setFont(subFont);
		g2.setColor(subColor);
		paintTextCentered(g2, "created by", 180);
		g2.setFont(mainFont);
		g2.setColor(mainColor);
		paintTextCentered(g2, "Jan De Beer", 200);
		g2.setFont(subFont);
		g2.setColor(subColor);
		paintTextCentered(g2, "based on JavaCPC", 242);
		g2.setColor(mainColor);
		paintTextCentered(g2, "by Markus Hohmann", 258);
		g2.setColor(subColor);
		paintTextCentered(g2, "based on JEMU", 302);
		g2.setColor(mainColor);
		paintTextCentered(g2, "by Richard Wilson", 318);
		if (getVersionInfo() != null) {
			g2.setFont(versionFont);
			g2.setColor(versionColor);
			paintTextCentered(g2, getVersionInfo(), 360);
		}
	}

	private void paintTextCentered(Graphics2D g2, String text, int yBaseline) {
		FontMetrics fm = g2.getFontMetrics();
		int textWidth = fm.stringWidth(text);
		int panelWidth = getWidth();
		int x0 = (panelWidth - textWidth) / 2;
		g2.drawString(text, x0, yBaseline);
	}

	private AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

	private String getVersionInfo() {
		return versionInfo;
	}

}