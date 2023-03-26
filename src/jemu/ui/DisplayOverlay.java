package jemu.ui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

public interface DisplayOverlay {

	void init(JComponent displayComponent);

	void renderOntoDisplay(Graphics2D display, Rectangle displayBounds);

	void dispose(JComponent displayComponent);

}