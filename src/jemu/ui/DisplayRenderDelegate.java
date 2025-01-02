package jemu.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * This abstract class is responsible for rendering a <code>Display</code> onto a <code>Graphics</code> context
 * 
 * <p>
 * Implementations may vary in the rendering strategy employed, for example trading off performance for rendering
 * quality
 * </p>
 */
public abstract class DisplayRenderDelegate {

	private Display display;

	private boolean painted;

	private String name;

	protected DisplayRenderDelegate(String name) {
		this.name = name;
	}

	public void init(Display display) {
		setDisplay(display);
	}

	public void dispose() {
		// Subclasses can override
	}

	public boolean isDoubleBufferingEnabled() {
		return false; // Subclasses can override
	}

	public void displayChangedSize(int width, int height) {
		System.out.println("Display size " + width + "x" + height);
	}

	public void displayImageChangedSize(int imageWidth, int imageHeight) {
		System.out.println("Display image size " + imageWidth + "x" + imageHeight);
	}

	public abstract void displayPixelsReadyForPainting();

	public abstract void refreshDisplay();

	public abstract void paintDisplayOnscreen(Graphics g, boolean monitorEffect);

	public abstract void paintDisplayOffscreen(Graphics g, boolean monitorEffect);

	protected void repaintDisplayImmediately() {
		if (isDisplayShowing()) {
			getDisplay().paintImmediately(getImageRect());
		}
	}

	protected void repaintDisplay(boolean wait) {
		if (isDisplayShowing()) {
			setPainted(false);
			Rectangle imageRect = getImageRect();
			getDisplay().repaint(0, imageRect.x, imageRect.y, imageRect.width, imageRect.height);
			if (wait) {
				waitPainted();
			}
		}
	}

	protected void waitPainted() {
		if (isDisplayShowing()) {
			long timeoutTime = System.currentTimeMillis() + 500L;
			boolean timeout = false;
			while (!isPainted() && !timeout) {
				try {
					Thread.sleep(1L);
				} catch (InterruptedException e) {
				}
				timeout = System.currentTimeMillis() > timeoutTime;
			}
		}
	}

	protected void paintDisplayOverlays(Graphics g, boolean offscreenImage, boolean monitorEffect) {
		Rectangle imageRect = getImageRect();
		MonitorMask monitorMask = monitorEffect ? getMonitorMask() : null;
		DisplayView displayView = new GraphicsDisplayView((Graphics2D) g);
		if (getCustomDisplayOverlay() != null) {
			getCustomDisplayOverlay().renderOntoDisplay(displayView, imageRect, monitorMask, offscreenImage);
		}
		getSystemDisplayOverlay().renderOntoDisplay(displayView, imageRect, monitorMask, offscreenImage);
	}

	protected boolean isDisplayShowing() {
		if (getImageRect().width == 0 || getImageRect().height == 0)
			return false;
		Display display = getDisplay();
		if (!display.isShowing())
			return false;
		try {
			Point p = display.getLocationOnScreen();
			if (p.x + display.getWidth() < 0 || p.y + display.getHeight() < 0)
				return false;
		} catch (IllegalComponentStateException e) {
			// not showing on screen
			return false;
		}
		return true;
	}

	protected boolean isPaintScanLinesVertically() {
		return isPaintScanLines() && Switches.bilinear && (Switches.monitormode == 0 || Switches.monitormode == 1);
	}

	protected boolean isPaintScanLinesHorizontally() {
		return isPaintScanLines() && !isPaintScanLinesVertically();
	}

	protected boolean isPaintScanLines() {
		return isScanLinesEnabled() && (isLargeDisplay() || Switches.bilinear);
	}

	protected boolean isLowPerformance() {
		return Display.lowperformance;
	}

	protected boolean isMonitorEffectEnabled() {
		return getDisplay().isMonitorEffectEnabled();
	}

	protected boolean isScanLinesEnabled() {
		return getDisplay().isScanLinesEnabled();
	}

	protected boolean isBilinearEnabled() {
		return getDisplay().isBilinearEnabled();
	}

	protected boolean isLargeDisplay() {
		return getDisplay().isLargeDisplay();
	}

	protected int getImageWidth() {
		return getDisplay().getImageWidth();
	}

	protected int getImageHeight() {
		return getDisplay().getImageHeight();
	}

	protected Rectangle getImageRect() {
		return Display.imageRect;
	}

	protected MonitorMask getMonitorMask() {
		return getDisplay().getMonitorMask();
	}

	protected DisplayOverlay getCustomDisplayOverlay() {
		return getDisplay().getCustomDisplayOverlay();
	}

	protected DisplayOverlay getSystemDisplayOverlay() {
		return getDisplay().getSystemDisplayOverlay();
	}

	protected boolean hasSecondaryDisplaySource() {
		return getDisplay().hasSecondaryDisplaySource();
	}

	protected SecondaryDisplaySource getSecondaryDisplaySource() {
		return getDisplay().getSecondaryDisplaySource();
	}

	protected int[] getDisplayPixels() {
		return getDisplay().getPixels();
	}

	protected Font getDisplayFont() {
		return getDisplay().getDisplayFont();
	}

	protected Display getDisplay() {
		return display;
	}

	private void setDisplay(Display display) {
		this.display = display;
	}

	protected boolean isPainted() {
		return painted;
	}

	public void setPainted(boolean painted) {
		this.painted = painted;
	}

	public String getName() {
		return name;
	}

	protected static class GraphicsDisplayView implements DisplayView {

		private Graphics2D graphics2D;

		public GraphicsDisplayView(Graphics2D g2) {
			this.graphics2D = g2;
		}

		@Override
		public Graphics2D createDisplayViewport(int x, int y, int width, int height) {
			return (Graphics2D) getGraphics2D().create(x, y, width, height);
		}

		@Override
		public FontMetrics getFontMetrics(Font font) {
			return getGraphics2D().getFontMetrics(font);
		}

		private Graphics2D getGraphics2D() {
			return graphics2D;
		}

	}

}