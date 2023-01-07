package jemu.ui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

public interface SecondaryDisplaySource {

	void init(JComponent displayComponent);

	void renderOntoDisplay(Graphics2D display, Rectangle displayBounds);

	void dispose(JComponent displayComponent);

	boolean canShow(OnDisplayIndicator indicator);

	public static enum OnDisplayIndicator {

		AUTO_TYPE,

		PAUSE;

	}

}