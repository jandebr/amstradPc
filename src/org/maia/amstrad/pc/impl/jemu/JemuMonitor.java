package org.maia.amstrad.pc.impl.jemu;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

import jemu.settings.Settings;
import jemu.ui.Display;
import jemu.ui.DisplayOverlay;
import jemu.ui.DisplayView;
import jemu.ui.MonitorMask;
import jemu.ui.SecondaryDisplaySource;
import jemu.ui.Switches;

public abstract class JemuMonitor extends AmstradMonitor implements AmstradPcStateListener {

	private AutonomousDisplayRenderer autonomousDisplayRenderer;

	protected JemuMonitor(JemuAmstradPc amstradPc) {
		super(amstradPc);
		amstradPc.addStateListener(this);
	}

	@Override
	public JComponent getDisplayComponent() {
		return getJemuDisplay();
	}

	protected abstract Display getJemuDisplay();

	@Override
	public final void setMode(AmstradMonitorMode mode) {
		checkNotTerminated();
		if (mode != null && !mode.equals(getMode())) {
			applyMonitorMode(mode);
			fireMonitorModeChangedEvent();
		}
	}

	protected void applyMonitorMode(AmstradMonitorMode mode) {
		if (AmstradMonitorMode.COLOR.equals(mode)) {
			applyMonitorModeColour();
		} else if (AmstradMonitorMode.GREEN.equals(mode)) {
			applyMonitorModeGreen();
		} else if (AmstradMonitorMode.GRAY.equals(mode)) {
			applyMonitorModeGray();
		}
	}

	protected abstract void applyMonitorModeColour();

	protected abstract void applyMonitorModeGreen();

	protected abstract void applyMonitorModeGray();

	@Override
	public boolean isMonitorEffectOn() {
		return Settings.getBoolean(Settings.SCANEFFECT, true);
	}

	@Override
	public void setMonitorEffect(boolean monitorEffect) {
		checkNotTerminated();
		if (monitorEffect != isMonitorEffectOn()) {
			Settings.setBoolean(Settings.SCANEFFECT, monitorEffect);
			Display.scaneffect = monitorEffect;
			fireMonitorEffectChangedEvent();
		}
	}

	@Override
	public boolean isScanLinesEffectOn() {
		return Settings.getBoolean(Settings.SCANLINES, false);
	}

	@Override
	public void setScanLinesEffect(boolean scanLinesEffect) {
		checkNotTerminated();
		if (scanLinesEffect != isScanLinesEffectOn()) {
			Settings.setBoolean(Settings.SCANLINES, scanLinesEffect);
			Switches.ScanLines = scanLinesEffect;
			fireMonitorScanLinesEffectChangedEvent();
		}
	}

	@Override
	public boolean isBilinearEffectOn() {
		return Settings.getBoolean(Settings.BILINEAR, true);
	}

	@Override
	public void setBilinearEffect(boolean bilinearEffect) {
		checkNotTerminated();
		if (bilinearEffect != isBilinearEffectOn()) {
			Settings.setBoolean(Settings.BILINEAR, bilinearEffect);
			Switches.bilinear = bilinearEffect;
			fireMonitorBilinearEffectChangedEvent();
		}
	}

	@Override
	public boolean isFullGateArray() {
		return Settings.getBoolean(Settings.LARGE, true);
	}

	@Override
	public final void setFullGateArray(boolean full) {
		checkNotTerminated();
		if (full != isFullGateArray()) {
			doSetFullGateArray(full);
			centerFrameOnScreen();
			fireMonitorGateArraySizeChangedEvent();
		}
	}

	protected abstract void doSetFullGateArray(boolean full);

	@Override
	public boolean isSingleSize() {
		return !isDoubleSize() && !isTripleSize();
	}

	@Override
	public final void setSingleSize() {
		checkNotTerminated();
		if (!isSingleSize()) {
			doSetSingleSize();
			centerFrameOnScreen();
			fireMonitorSizeChangedEvent();
		}
	}

	protected abstract void doSetSingleSize();

	@Override
	public boolean isDoubleSize() {
		return Settings.getBoolean(Settings.DOUBLE, true);
	}

	@Override
	public final void setDoubleSize() {
		checkNotTerminated();
		if (!isDoubleSize()) {
			doSetDoubleSize();
			centerFrameOnScreen();
			fireMonitorSizeChangedEvent();
		}
	}

	protected abstract void doSetDoubleSize();

	@Override
	public boolean isTripleSize() {
		return Settings.getBoolean(Settings.TRIPLE, true);
	}

	@Override
	public final void setTripleSize() {
		checkNotTerminated();
		if (!isTripleSize()) {
			doSetTripleSize();
			centerFrameOnScreen();
			fireMonitorSizeChangedEvent();
		}
	}

	protected abstract void doSetTripleSize();

	protected void centerFrameOnScreen() {
		if (getAmstradPc().hasFrame())
			getAmstradPc().getFrame().centerOnScreen();
	}

	@Override
	public boolean isFullscreen() {
		return Settings.getBoolean(Settings.FULLSCREEN, false);
	}

	@Override
	public final void toggleFullscreen() {
		checkStarted();
		checkNotTerminated();
		if (isFullscreen()) {
			applyWindowed();
		} else {
			applyFullscreen();
		}
		fireMonitorFullscreenChangedEvent();
	}

	protected abstract void applyFullscreen();

	protected abstract void applyWindowed();

	@Override
	public boolean isWindowAlwaysOnTop() {
		return Settings.getBoolean(Settings.ONTOP, false);
	}

	@Override
	public final void setWindowAlwaysOnTop(boolean alwaysOnTop) {
		checkNotTerminated();
		if (alwaysOnTop != isWindowAlwaysOnTop()) {
			doSetWindowAlwaysOnTop(alwaysOnTop);
			fireWindowAlwaysOnTopChangedEvent();
		}
	}

	protected abstract void doSetWindowAlwaysOnTop(boolean alwaysOnTop);

	@Override
	public BufferedImage makeScreenshot(boolean monitorEffect) {
		BufferedImage image = null;
		synchronized (getAmstradPc()) {
			checkStarted();
			checkNotTerminated();
			image = getJemuDisplay().getImage(monitorEffect);
		}
		return image;
	}

	@Override
	public void swapDisplaySource(AmstradAlternativeDisplaySource displaySource) {
		synchronized (getAmstradPc()) {
			checkStarted();
			checkNotTerminated();
			if (displaySource != null) {
				doSwapDisplaySource(displaySource);
				fireDisplaySourceChangedEvent();
				handleAutonomousDisplayRendering();
			} else {
				resetDisplaySource();
			}
		}
	}

	protected void doSwapDisplaySource(AmstradAlternativeDisplaySource displaySource) {
		getJemuDisplay().installSecondaryDisplaySource(new JemuSecondaryDisplaySourceBridge(displaySource));
	}

	@Override
	public void resetDisplaySource() {
		synchronized (getAmstradPc()) {
			checkStarted();
			checkNotTerminated();
			if (isAlternativeDisplaySourceShowing()) {
				doResetDisplaySource();
				fireDisplaySourceChangedEvent();
				handleAutonomousDisplayRendering();
			}
		}
	}

	protected void doResetDisplaySource() {
		getJemuDisplay().uninstallSecondaryDisplaySource();
	}

	@Override
	public AmstradAlternativeDisplaySource getCurrentAlternativeDisplaySource() {
		AmstradAlternativeDisplaySource altDisplaySource = null;
		if (getAmstradPc().isStarted()) {
			SecondaryDisplaySource sds = getJemuDisplay().getSecondaryDisplaySource();
			if (sds != null && sds instanceof JemuSecondaryDisplaySourceBridge) {
				altDisplaySource = ((JemuSecondaryDisplaySourceBridge) sds).getSource();
			}
		}
		return altDisplaySource;
	}

	@Override
	public boolean isAlternativeDisplaySourceShowing() {
		return getJemuDisplay().getSecondaryDisplaySource() != null;
	}

	@Override
	public AmstradDisplayOverlay getCustomDisplayOverlay() {
		DisplayOverlay overlay = getJemuDisplay().getCustomDisplayOverlay();
		if (overlay instanceof JemuDisplayOverlayBridge) {
			return ((JemuDisplayOverlayBridge) overlay).getSource();
		} else {
			return null;
		}
	}

	@Override
	public void setCustomDisplayOverlay(AmstradDisplayOverlay overlay) {
		synchronized (getAmstradPc()) {
			if (overlay != null) {
				getJemuDisplay().installCustomDisplayOverlay(new JemuDisplayOverlayBridge(overlay));
			} else {
				resetCustomDisplayOverlay();
			}
		}
	}

	@Override
	public void resetCustomDisplayOverlay() {
		synchronized (getAmstradPc()) {
			getJemuDisplay().uninstallCustomDisplayOverlay();
		}
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		handleAutonomousDisplayRendering();
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		handleAutonomousDisplayRendering();
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcProgramLoaded(AmstradPc amstradPc) {
	}

	private void handleAutonomousDisplayRendering() {
		synchronized (getAmstradPc()) {
			if (!Switches.autonomousDisplayRendering && getAmstradPc().isPaused()
					&& isAlternativeDisplaySourceShowing()) {
				// When computer is paused, there is no vSync. When vSync ignites the rendering process, it means we
				// need to take over the rendering (until the computer resumes for example)
				if (getAutonomousDisplayRenderer() == null || getAutonomousDisplayRenderer().isStopped()) {
					AutonomousDisplayRenderer renderer = new AutonomousDisplayRenderer();
					setAutonomousDisplayRenderer(renderer);
					renderer.start();
				}
			} else {
				// Stop our own rendering
				if (getAutonomousDisplayRenderer() != null) {
					getAutonomousDisplayRenderer().stopRendering();
					setAutonomousDisplayRenderer(null);
				}
			}
		}
	}

	private AutonomousDisplayRenderer getAutonomousDisplayRenderer() {
		return autonomousDisplayRenderer;
	}

	private void setAutonomousDisplayRenderer(AutonomousDisplayRenderer renderer) {
		this.autonomousDisplayRenderer = renderer;
	}

	private class AutonomousDisplayRenderer extends Thread {

		private boolean stop;

		public AutonomousDisplayRenderer() {
			super("AutonomousDisplayRenderer");
			setDaemon(true);
		}

		@Override
		public void run() {
			System.out.println("Autonomous display render thread started");
			final Display display = getJemuDisplay();
			while (!isStopped()) {
				display.updateImage(true);
			}
			System.out.println("Autonomous display render thread stopped");
		}

		public void stopRendering() {
			stop = true;
		}

		public boolean isStopped() {
			return stop;
		}

	}

	private class JemuSecondaryDisplaySourceBridge implements SecondaryDisplaySource {

		private AmstradAlternativeDisplaySource source;

		private AmstradMonitorMode rememberedMonitorMode;

		private boolean rememberedMonitorEffect;

		private boolean rememberedMonitorScanLinesEffect;

		private boolean rememberedMonitorBilinearEffect;

		public JemuSecondaryDisplaySourceBridge(AmstradAlternativeDisplaySource source) {
			this.source = source;
		}

		@Override
		public void init(JComponent displayComponent) {
			AmstradMonitor monitor = JemuMonitor.this;
			rememberedMonitorMode = monitor.getMode();
			rememberedMonitorEffect = monitor.isMonitorEffectOn();
			rememberedMonitorScanLinesEffect = monitor.isScanLinesEffectOn();
			rememberedMonitorBilinearEffect = monitor.isBilinearEffectOn();
			getSource().init(displayComponent, getGraphicsContext());
		}

		@Override
		public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds) {
			getSource().renderOntoDisplay(display, displayBounds, getGraphicsContext());
		}

		@Override
		public void dispose(JComponent displayComponent) {
			getSource().dispose(displayComponent);
			if (getSource().isRestoreMonitorSettingsOnDispose()) {
				System.out.println("Restoring monitor settings");
				restoreMonitorSettings();
			}
		}

		private void restoreMonitorSettings() {
			AmstradMonitor monitor = JemuMonitor.this;
			monitor.setMode(rememberedMonitorMode);
			monitor.setMonitorEffect(rememberedMonitorEffect);
			monitor.setScanLinesEffect(rememberedMonitorScanLinesEffect);
			monitor.setBilinearEffect(rememberedMonitorBilinearEffect);
		}

		private AmstradAlternativeDisplaySource getSource() {
			return source;
		}

	}

	private class JemuDisplayOverlayBridge implements DisplayOverlay {

		private AmstradDisplayOverlay source;

		private final Insets ZERO_INSETS = new Insets(0, 0, 0, 0);

		public JemuDisplayOverlayBridge(AmstradDisplayOverlay source) {
			this.source = source;
		}

		@Override
		public void init(JComponent displayComponent) {
			getSource().init(displayComponent, getGraphicsContext());
		}

		@Override
		public void renderOntoDisplay(DisplayView displayView, Rectangle displayBounds, MonitorMask monitorMask,
				boolean offscreenImage) {
			Insets monitorInsets = computeMonitorInsets(monitorMask, displayBounds);
			AmstradDisplayView view = new JemuDisplayViewBridge(displayView);
			getSource().renderOntoDisplay(view, displayBounds, monitorInsets, offscreenImage, getGraphicsContext());
		}

		private Insets computeMonitorInsets(MonitorMask monitorMask, Rectangle displayBounds) {
			Insets monitorInsets = ZERO_INSETS;
			if (monitorMask != null) {
				Insets in = monitorMask.getInsetsToInnerArea();
				int maskWidth = monitorMask.getImage().getWidth(null);
				if (displayBounds.width == maskWidth) {
					monitorInsets = in;
				} else {
					double scale = displayBounds.getWidth() / maskWidth;
					monitorInsets = new Insets((int) Math.round(in.top * scale), (int) Math.round(in.left * scale),
							(int) Math.round(in.bottom * scale), (int) Math.round(in.right * scale));
				}
			}
			return monitorInsets;
		}

		@Override
		public void dispose(JComponent displayComponent) {
			getSource().dispose(displayComponent);
		}

		public AmstradDisplayOverlay getSource() {
			return source;
		}

	}

	private static class JemuDisplayViewBridge implements AmstradDisplayView {

		private DisplayView sourceView;

		public JemuDisplayViewBridge(DisplayView sourceView) {
			this.sourceView = sourceView;
		}

		@Override
		public Graphics2D createDisplayViewport(int x, int y, int width, int height) {
			return getSourceView().createDisplayViewport(x, y, width, height);
		}

		@Override
		public FontMetrics getFontMetrics(Font font) {
			return getSourceView().getFontMetrics(font);
		}

		private DisplayView getSourceView() {
			return sourceView;
		}

	}

}