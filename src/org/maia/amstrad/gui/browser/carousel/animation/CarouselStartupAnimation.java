package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.swing.SwingUtils;
import org.maia.swing.text.TextLabel;

public class CarouselStartupAnimation extends CarouselAnimation {

	private TextLabel label;

	private static Font font = Font.decode("Arial-PLAIN-20");

	public CarouselStartupAnimation(CarouselHost host) {
		super(host);
	}

	@Override
	public void init() {
		super.init();
		TextLabel label = TextLabel.createCompactLabel("Loading", font);
		label.setForeground(Color.WHITE);
		SwingUtils.fixSize(label, label.getPreferredSize());
		setLabel(label);
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		TextLabel label = getLabel();
		int x = (displayWidth - label.getWidth()) / 2;
		int y = (displayHeight - label.getHeight()) / 2;
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(x, y);
		label.paint(g2);
		g2.dispose();
	}

	private TextLabel getLabel() {
		return label;
	}

	private void setLabel(TextLabel label) {
		this.label = label;
	}

}