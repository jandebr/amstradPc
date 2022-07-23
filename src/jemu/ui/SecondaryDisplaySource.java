package jemu.ui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

public interface SecondaryDisplaySource {

	void init(JComponent displayComponent);

	void renderOntoDisplay(Graphics2D g2, Rectangle displayBounds);

	void dispose(JComponent displayComponent);

}