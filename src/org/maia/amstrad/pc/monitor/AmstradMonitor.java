package org.maia.amstrad.pc.monitor;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.AmstradDevice;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradAlternativeDisplaySource;

public abstract class AmstradMonitor extends AmstradDevice {

	private List<AmstradMonitorListener> monitorListeners;

	protected AmstradMonitor(AmstradPc amstradPc) {
		super(amstradPc);
		this.monitorListeners = new Vector<AmstradMonitorListener>();
	}

	public abstract Component getDisplayPane();

	public AmstradMonitorMode getMonitorMode() {
		return AmstradFactory.getInstance().getAmstradContext().getUserSettings().getMonitorMode();
	}

	public abstract void setMonitorMode(AmstradMonitorMode mode);

	public abstract boolean isMonitorEffectOn();

	public abstract void setMonitorEffect(boolean monitorEffect);

	public abstract boolean isMonitorScanLinesEffectOn();

	public abstract void setMonitorScanLinesEffect(boolean scanLinesEffect);

	public abstract boolean isMonitorBilinearEffectOn();

	public abstract void setMonitorBilinearEffect(boolean bilinearEffect);

	public abstract boolean isWindowFullscreen();

	public abstract void toggleWindowFullscreen();

	public abstract boolean isWindowAlwaysOnTop();

	public abstract void setWindowAlwaysOnTop(boolean alwaysOnTop);

	public abstract boolean isWindowTitleDynamic();

	public abstract void setWindowTitleDynamic(boolean dynamicTitle);

	public abstract BufferedImage makeScreenshot(boolean monitorEffect);

	public abstract void swapDisplaySource(AmstradAlternativeDisplaySource displaySource);

	public abstract void resetDisplaySource();

	public abstract AmstradAlternativeDisplaySource getCurrentAlternativeDisplaySource();

	public boolean isAlternativeDisplaySourceShowing() {
		return getCurrentAlternativeDisplaySource() != null;
	}

	public boolean isPrimaryDisplaySourceShowing() {
		return !isAlternativeDisplaySourceShowing();
	}

	public void addMonitorListener(AmstradMonitorListener listener) {
		getMonitorListeners().add(listener);
	}

	public void removeMonitorListener(AmstradMonitorListener listener) {
		getMonitorListeners().remove(listener);
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

	protected void fireWindowFullscreenChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradWindowFullscreenChanged(this);
	}

	protected void fireWindowAlwaysOnTopChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradWindowAlwaysOnTopChanged(this);
	}

	protected void fireWindowTitleDynamicChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradWindowTitleDynamicChanged(this);
	}

	protected void fireDisplaySourceChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradDisplaySourceChanged(this);
	}

	protected List<AmstradMonitorListener> getMonitorListeners() {
		return monitorListeners;
	}

}