package jemu.ui;

import java.awt.Rectangle;

import javax.swing.JComponent;

public interface DisplayOverlay {

	void init(JComponent displayComponent);

	void renderOntoDisplay(DisplayView displayView, Rectangle displayBounds, MonitorMask monitorMask,
			boolean offscreenImage);

	void dispose(JComponent displayComponent);

}