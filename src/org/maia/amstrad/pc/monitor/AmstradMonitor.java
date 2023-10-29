package org.maia.amstrad.pc.monitor;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradDevice;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.monitor.display.source.AmstradImageDisplaySource;
import org.maia.util.GenericListenerList;

public abstract class AmstradMonitor extends AmstradDevice {

	private GenericListenerList<AmstradMonitorListener> monitorListeners;

	private boolean showSystemStats;

	protected AmstradMonitor(AmstradPc amstradPc) {
		super(amstradPc);
		this.monitorListeners = new GenericListenerList<AmstradMonitorListener>();
	}

	public abstract AmstradGraphicsContext getGraphicsContext();

	public abstract JComponent getDisplayComponent();

	public AmstradMonitorMode getMode() {
		return AmstradFactory.getInstance().getAmstradContext().getUserSettings().getMonitorMode();
	}

	public abstract void setMode(AmstradMonitorMode mode);

	public abstract boolean isMonitorEffectOn();

	public abstract void setMonitorEffect(boolean monitorEffect);

	public abstract boolean isScanLinesEffectOn();

	public abstract void setScanLinesEffect(boolean scanLinesEffect);

	public abstract boolean isBilinearEffectOn();

	public abstract void setBilinearEffect(boolean bilinearEffect);

	public abstract boolean isFullGateArray();

	public abstract void setFullGateArray(boolean full);

	public abstract boolean isSingleSize();

	public abstract void setSingleSize();

	public abstract boolean isDoubleSize();

	public abstract void setDoubleSize();

	public abstract boolean isTripleSize();

	public abstract void setTripleSize();

	public abstract boolean isFullscreen();

	public abstract void toggleFullscreen();

	public void makeFullscreen() {
		if (!isFullscreen())
			toggleFullscreen();
	}

	public abstract boolean isWindowAlwaysOnTop();

	public abstract void setWindowAlwaysOnTop(boolean alwaysOnTop);

	public boolean isShowSystemStats() {
		return showSystemStats;
	}

	public void setShowSystemStats(boolean show) {
		if (show != showSystemStats) {
			showSystemStats = show;
			fireShowSystemStatsChangedEvent();
		}
	}

	public abstract BufferedImage makeScreenshot(boolean monitorEffect);

	public void freezeFrame() {
		if (isPrimaryDisplaySourceShowing()) {
			swapDisplaySource(AmstradImageDisplaySource.createFreezeFrame(this));
		}
	}

	public void unfreezeFrame() {
		if (AmstradFactory.getInstance().getAmstradContext().isImageShowing(getAmstradPc())) {
			resetDisplaySource();
		}
	}

	public abstract void swapDisplaySource(AmstradAlternativeDisplaySource displaySource);

	public abstract void resetDisplaySource();

	public abstract AmstradAlternativeDisplaySource getCurrentAlternativeDisplaySource();

	public abstract boolean isAlternativeDisplaySourceShowing();

	public boolean isPrimaryDisplaySourceShowing() {
		return !isAlternativeDisplaySourceShowing();
	}

	public abstract void setCustomDisplayOverlay(AmstradDisplayOverlay overlay);

	public abstract void resetCustomDisplayOverlay();

	public void addMonitorListener(AmstradMonitorListener listener) {
		getMonitorListeners().addListener(listener);
	}

	public void removeMonitorListener(AmstradMonitorListener listener) {
		getMonitorListeners().removeListener(listener);
	}

	protected void fireMonitorModeChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradMonitorModeChanged(this);
	}

	protected void fireMonitorEffectChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradMonitorEffectChanged(this);
	}

	protected void fireMonitorScanLinesEffectChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradMonitorScanLinesEffectChanged(this);
	}

	protected void fireMonitorBilinearEffectChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradMonitorBilinearEffectChanged(this);
	}

	protected void fireMonitorGateArraySizeChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradMonitorGateArraySizeChanged(this);
	}

	protected void fireMonitorSizeChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradMonitorSizeChanged(this);
	}

	protected void fireMonitorFullscreenChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradMonitorFullscreenChanged(this);
	}

	protected void fireWindowAlwaysOnTopChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradWindowAlwaysOnTopChanged(this);
	}

	protected void fireShowSystemStatsChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradShowSystemStatsChanged(this);
	}

	protected void fireDisplaySourceChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradDisplaySourceChanged(this);
	}

	protected GenericListenerList<AmstradMonitorListener> getMonitorListeners() {
		return monitorListeners;
	}

}