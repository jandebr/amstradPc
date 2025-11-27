package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

public class CarouselComponentCursor extends JComponent {

	public CarouselComponentCursor() {
		// TODO !getSelectedItem().isExecutable() => grayed out
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

}