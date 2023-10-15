package org.maia.amstrad.gui.overlay;

import java.awt.Insets;
import java.awt.Rectangle;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import org.maia.amstrad.pc.monitor.display.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class StackedDisplayOverlay implements AmstradDisplayOverlay {

	private List<AmstradDisplayOverlay> overlays; // in drawing order = descending z-order

	private List<Integer> zOrders;

	public StackedDisplayOverlay() {
		this.overlays = new Vector<AmstradDisplayOverlay>();
		this.zOrders = new Vector<Integer>();
	}

	/**
	 * Adds the specified overlay to this stack
	 * 
	 * @param overlay
	 *            The overlay to add
	 * @param zOrder
	 *            The z-order of the overlay, which defines its depth (drawing order). An overlay with a lower z-order
	 *            is drawn on top of overlays with a higher z-order
	 */
	public void addOverlay(AmstradDisplayOverlay overlay, int zOrder) {
		int i = deriveInsertionIndex(zOrder);
		getOverlays().add(i, overlay);
		getZorders().add(i, zOrder);
	}

	public void removeOverlay(AmstradDisplayOverlay overlay) {
		int i = getOverlays().indexOf(overlay);
		if (i >= 0) {
			getOverlays().remove(i);
			getZorders().remove(i);
		}
	}

	public void removeAllOverlays() {
		getOverlays().clear();
		getZorders().clear();
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		for (AmstradDisplayOverlay overlay : getOverlays()) {
			overlay.init(displayComponent, graphicsContext);
		}
	}

	@Override
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		for (AmstradDisplayOverlay overlay : getOverlays()) {
			overlay.renderOntoDisplay(displayView, displayBounds, monitorInsets, offscreenImage, graphicsContext);
		}
	}

	@Override
	public void dispose(JComponent displayComponent) {
		for (AmstradDisplayOverlay overlay : getOverlays()) {
			overlay.dispose(displayComponent);
		}
	}

	private int deriveInsertionIndex(int zOrder) {
		int n = getZorders().size();
		int i = 0;
		while (i < n && zOrder <= getZorders().get(i))
			i++;
		return i;
	}

	private List<AmstradDisplayOverlay> getOverlays() {
		return overlays;
	}

	private List<Integer> getZorders() {
		return zOrders;
	}

}