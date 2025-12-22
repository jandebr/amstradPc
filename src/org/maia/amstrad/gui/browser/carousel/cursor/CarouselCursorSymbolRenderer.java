package org.maia.amstrad.gui.browser.carousel.cursor;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.amstrad.gui.AmstradSymbolRenderer;
import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;
import org.maia.swing.animate.itemslide.SlidingItem;
import org.maia.swing.animate.itemslide.SlidingItemListAdapter;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.util.ColorUtils;

public class CarouselCursorSymbolRenderer extends SlidingItemListAdapter implements CarouselCursorRenderer {

	private CarouselHost host;

	private Color inFocusColor;

	private Color outFocusColor;

	private AmstradSymbolRenderer symbolRenderer;

	private long pulseStartTimeMillis = Long.MAX_VALUE;

	private long pulseIntervalTimeMillis = 2200L;

	private long pulseDelayTimeMillis = 1200L;

	private int minimumPulseAmplitude = 3;

	private int maximumPulseAmplitude = 5;

	public CarouselCursorSymbolRenderer(CarouselHost host, Color inFocusColor) {
		this(host, inFocusColor, ColorUtils.adjustSaturationAndBrightness(inFocusColor, -0.8f, -0.8f));
	}

	public CarouselCursorSymbolRenderer(CarouselHost host, Color inFocusColor, Color outFocusColor) {
		this.host = host;
		this.inFocusColor = inFocusColor;
		this.outFocusColor = outFocusColor;
		this.symbolRenderer = new AmstradSymbolRenderer(host.getGraphicsContext(), null);
		host.getCarouselComponent().addListener(this);
	}

	@Override
	public void render(Graphics2D gCentered, int maximumCursorWidth, int maximumCursorHeight) {
		if (!isInFocus()) {
			setPulseStartTimeMillis(System.currentTimeMillis());
		}
		if (!isHidden()) {
			int dy = 0;
			int scale = Math.max(1, Math.floorDiv(maximumCursorHeight - 2 * getMinimumPulseAmplitude(), 8));
			float pulseOffset = getRelativePulseOffset();
			if (pulseOffset > 0) {
				int pulseAmplitude = Math.min(Math.floorDiv(Math.max(maximumCursorHeight - scale * 8, 0), 2),
						getMaximumPulseAmplitude());
				dy = (int) Math.round(pulseAmplitude * Math.sin(pulseOffset * 2.0 * Math.PI));
			}
			AmstradSymbolRenderer sym = getSymbolRenderer();
			sym.replaceGraphics2D(gCentered);
			sym.color(getInFocusColor());
			sym.scale(scale);
			sym.drawChr(213, -8 * scale, -4 * scale + dy);
			sym.drawChr(212, 0, -4 * scale + dy);
		}
	}

	@Override
	public void notifyItemSelectionLanded(SlidingItemListComponent component, SlidingItem selectedItem,
			int selectedItemIndex) {
		setPulseStartTimeMillis(System.currentTimeMillis() + getPulseDelayTimeMillis());
	}

	@Override
	public void notifyStartSliding(SlidingItemListComponent component) {
		setPulseStartTimeMillis(Long.MAX_VALUE);
	}

	private float getRelativePulseOffset() {
		long tp = getPulseStartTimeMillis();
		long t = System.currentTimeMillis();
		if (t < tp) {
			return 0;
		} else {
			return ((t - tp) % getPulseIntervalTimeMillis()) / (float) getPulseIntervalTimeMillis();
		}
	}

	protected boolean isHidden() {
		if (getHost().getRunProgramActionInProgress() != null)
			return true;
		if (getHost().getEnterFolderActionInProgress() != null)
			return true;
		if (!isInFocus())
			return true;
		return false;
	}

	protected boolean isInFocus() {
		return getHost().isFocusOnCarousel();
	}

	public CarouselHost getHost() {
		return host;
	}

	public Color getInFocusColor() {
		return inFocusColor;
	}

	public Color getOutFocusColor() {
		return outFocusColor;
	}

	private AmstradSymbolRenderer getSymbolRenderer() {
		return symbolRenderer;
	}

	private long getPulseStartTimeMillis() {
		return pulseStartTimeMillis;
	}

	private void setPulseStartTimeMillis(long timeMillis) {
		this.pulseStartTimeMillis = timeMillis;
	}

	public long getPulseIntervalTimeMillis() {
		return pulseIntervalTimeMillis;
	}

	public void setPulseIntervalTimeMillis(long intervalTimeMillis) {
		this.pulseIntervalTimeMillis = intervalTimeMillis;
	}

	public long getPulseDelayTimeMillis() {
		return pulseDelayTimeMillis;
	}

	public void setPulseDelayTimeMillis(long delayTimeMillis) {
		this.pulseDelayTimeMillis = delayTimeMillis;
	}

	public int getMinimumPulseAmplitude() {
		return minimumPulseAmplitude;
	}

	public void setMinimumPulseAmplitude(int amplitude) {
		this.minimumPulseAmplitude = amplitude;
	}

	public int getMaximumPulseAmplitude() {
		return maximumPulseAmplitude;
	}

	public void setMaximumPulseAmplitude(int amplitude) {
		this.maximumPulseAmplitude = amplitude;
	}

}