package org.maia.amstrad.pc.monitor;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPcDevice;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.monitor.cursor.AmstradMonitorCursorController;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.monitor.display.source.AmstradImageDisplaySource;
import org.maia.util.GenericListenerList;

import jemu.settings.Settings;

public abstract class AmstradMonitor extends AmstradPcDevice {

	private GenericListenerList<AmstradMonitorListener> monitorListeners;

	private GenericListenerList<AmstradMonitorPopupMenuListener> popupMenuListeners;

	private AmstradMonitorCursorController cursorController;

	private boolean showSystemStats;

	protected AmstradMonitor(AmstradPc amstradPc) {
		super(amstradPc);
		this.monitorListeners = new GenericListenerList<AmstradMonitorListener>();
		this.popupMenuListeners = new GenericListenerList<AmstradMonitorPopupMenuListener>();
		this.cursorController = AmstradFactory.getInstance().createCursorController(amstradPc);
		this.cursorController.setAutoHideCursor(isAutoHideCursor());
	}

	public void installPopupMenu(AmstradPopupMenu popupMenu) {
		getDisplayComponent().setComponentPopupMenu(popupMenu);
	}

	public void uninstallPopupMenu() {
		getDisplayComponent().setComponentPopupMenu(null);
	}

	public boolean isPopupMenuInstalled() {
		return getInstalledPopupMenu() != null;
	}

	public boolean isPopupMenuShowing() {
		AmstradPopupMenu popupMenu = getInstalledPopupMenu();
		return popupMenu != null && popupMenu.isShowing();
	}

	public AmstradPopupMenu getInstalledPopupMenu() {
		return (AmstradPopupMenu) getDisplayComponent().getComponentPopupMenu();
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

	public boolean isAutoHideCursor() {
		return Settings.getBoolean(Settings.AUTOHIDE_CURSOR, true);
	}

	public void setAutoHideCursor(boolean autoHide) {
		if (autoHide != isAutoHideCursor()) {
			Settings.setBoolean(Settings.AUTOHIDE_CURSOR, autoHide);
			getCursorController().setAutoHideCursor(autoHide);
			fireMonitorAutoHideCursorChangedEvent();
		}
	}

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

	public void addPopupMenuListener(AmstradMonitorPopupMenuListener listener) {
		getPopupMenuListeners().addListener(listener);
	}

	public void removePopupMenuListener(AmstradMonitorPopupMenuListener listener) {
		getPopupMenuListeners().removeListener(listener);
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

	protected void fireMonitorAutoHideCursorChangedEvent() {
		for (AmstradMonitorListener listener : getMonitorListeners())
			listener.amstradMonitorAutoHideCursorChanged(this);
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

	public void firePopupMenuWillBecomeVisible(AmstradPopupMenu popupMenu) {
		for (AmstradMonitorPopupMenuListener listener : getPopupMenuListeners())
			listener.popupMenuWillBecomeVisible(popupMenu);
	}

	public void firePopupMenuWillBecomeInvisible(AmstradPopupMenu popupMenu) {
		for (AmstradMonitorPopupMenuListener listener : getPopupMenuListeners())
			listener.popupMenuWillBecomeInvisible(popupMenu);
	}

	protected GenericListenerList<AmstradMonitorListener> getMonitorListeners() {
		return monitorListeners;
	}

	protected GenericListenerList<AmstradMonitorPopupMenuListener> getPopupMenuListeners() {
		return popupMenuListeners;
	}

	public AmstradMonitorCursorController getCursorController() {
		return cursorController;
	}

}