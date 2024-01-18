package org.maia.amstrad.pc.impl.jemu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;

import org.maia.amstrad.gui.overlay.VirtualKeyboardDisplayOverlay;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboardGridLayout;
import org.maia.amstrad.pc.memory.AmstradMemoryTrap;
import org.maia.amstrad.pc.menu.AmstradMenuBar;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.util.SystemUtils;

import jemu.core.device.Computer;
import jemu.core.device.memory.MemoryWriteObserver;
import jemu.settings.Settings;
import jemu.system.cpc.CPC;
import jemu.system.cpc.CPCPrinter;
import jemu.system.cpc.GateArray;
import jemu.system.cpc.RomSetter;
import jemu.ui.Autotype;
import jemu.ui.Display;
import jemu.ui.JEMU;
import jemu.ui.KeyDispatcher;
import jemu.ui.MonitorMask;
import jemu.ui.Switches;

public class JemuDirectAmstradPc extends JemuAmstradPc {

	private Computer computer;

	private Display display;

	private KeyDispatcher keyDispatcher;

	public JemuDirectAmstradPc(Computer computer, Display display) {
		this.computer = computer;
		this.display = display;
		this.keyDispatcher = new KeyDispatcher(display, computer);
		Switches.FloppySound = Settings.getBoolean(Settings.FLOPPYSOUND, true);
	}

	@Override
	protected AmstradPcFrame createFrame(boolean exitOnClose) {
		return new JemuFrameImpl(exitOnClose);
	}

	@Override
	protected JemuKeyboard createKeyboard() {
		return new JemuKeyboardImpl();
	}

	@Override
	protected JemuMemory createMemory() {
		return new JemuMemoryImpl();
	}

	@Override
	protected JemuMonitor createMonitor() {
		return new JemuMonitorImpl();
	}

	@Override
	protected void doStart() {
		System.out.println("Starting Jemu DIRECT pc");
		initJemu();
		initMonitor();
		Computer computer = getComputer();
		computer.addPerformanceListener(this);
		computer.start(); // Prevents loud beep sound during initialise()
		computer.initialise();
		computer.start();
		computer.reSync();
		Display display = getDisplay();
		display.changePerformance();
		display.addPerformanceListener(this);
		display.addPrimaryDisplaySourceListener(this);
		primaryDisplaySourceResolutionChanged(display,
				new Dimension(display.getImageWidth(), display.getImageHeight()));
		display.requestFocus();
		System.out.println("Started Jemu DIRECT pc");
	}

	private void initJemu() {
		// from JEMU.init()
		Switches.executable = true;
		Switches.stretch = false;
		Switches.khz44 = Settings.getBoolean(Settings.KHZ44, Switches.khz44);
		Switches.khz11 = Settings.getBoolean(Settings.KHZ11, Switches.khz11);
		Switches.doIntack = Settings.getBoolean(Settings.INTACK, true);
		Switches.doublesize = Settings.getBoolean(Settings.DOUBLE, false);
		Switches.triplesize = Settings.getBoolean(Settings.TRIPLE, false);
		Display.lowperformance = Settings.getBoolean(Settings.LOWPERFORMANCE, true);
		// from JEMU.start()
		new RomSetter().prepareRomsetter();
		new Autotype();
		new CPCPrinter();
		System.out.println("JavaCPC [v." + JEMU.version + "]\n\n[" + Calendar.getInstance().getTime() + "]\n");
		Switches.availmem = Runtime.getRuntime().freeMemory();
		Switches.overrideP = Settings.getBoolean(Settings.OVERRIDEP, false);
		Switches.changePolarity = Settings.getBoolean(Settings.POLARITY, false);
		Switches.MouseJoy = Settings.getBoolean(Settings.MOUSEJOY, false);
		Switches.CRTC = Integer.parseInt(Settings.get(Settings.CRTC, "0"));
		Switches.Printer = Settings.getBoolean(Settings.PRINTER, false);
		Switches.Expansion = Settings.getBoolean(Settings.EXPANSION, false);
		Switches.digiblaster = Settings.getBoolean(Settings.DIGIBLASTER, false);
		Switches.floppyturbo = Settings.getBoolean(Settings.FLOPPYTURBO, false);
		Switches.autosave = Settings.getBoolean(Settings.AUTOSAVE, false);
		Switches.checksave = Settings.getBoolean(Settings.CHECKSAVE, true);
		Switches.neverOverwrite = Settings.getBoolean(Settings.CHECKRENAME, false);
		Switches.ScanLines = Settings.getBoolean(Settings.SCANLINES, false);
		Display.scaneffect = Settings.getBoolean(Settings.SCANEFFECT, false);
		Switches.bilinear = Settings.getBoolean(Settings.BILINEAR, false);
		Switches.Memory = Settings.get(Settings.MEMORY, "TYPE_512K");
		Switches.computername = Integer.parseInt(Settings.get(Settings.COMPUTERNAME, "7"), 16);
		Switches.osddisplay = Settings.getBoolean(Settings.OSD, false);
		Display.model = "System: " + getComputer().getName();
		getMonitor().applyMonitorMode(getMonitor().getMode());
		Switches.audioenabler = getAudio().isMuted() ? 0 : 1;
		Switches.notebook = Settings.getBoolean(Settings.NOTEBOOK, false);
		Switches.joystick = Settings.getBoolean(Settings.JOYSTICK, true) ? 1 : 0;
		Switches.volume = Integer.parseInt(Settings.get(Settings.VOLUME, "1000")) / 1000.0;
		CPC.saveOnExit = 0;
	}

	private void initMonitor() {
		((JemuMonitorImpl) getMonitor()).update();
	}

	@Override
	protected void doReboot() {
		if (isPaused()) {
			setPaused(false);
			fireResumingEvent();
		}
		getComputer().reset();
		getComputer().start();
	}

	@Override
	protected void doTerminate() {
		Autotype.save();
		getComputer().dispose();
		getDisplay().dispose();
	}

	@Override
	protected void doPause() {
		doPauseImmediately();
	}

	@Override
	protected void doPauseImmediately() {
		Display.showpause = 1;
		getComputer().stop();
		System.out.println("System halted");
	}

	@Override
	protected void doResume() {
		Display.showpause = 0;
		getComputer().start();
		System.out.println("System resumed");
	}

	@Override
	protected synchronized void doLoad(File snapshotFile) throws IOException {
		Computer computer = getComputer();
		boolean running = computer.isRunning();
		computer.stop();
		try {
			computer.loadFile(0, snapshotFile.getAbsolutePath());
		} catch (Exception e) {
			throw new IOException("Failed to load snapshot file", e);
		} finally {
			if (running)
				computer.start();
		}
	}

	@Override
	protected AmstradDisplayOverlay createVirtualKeyboardDisplayOverlay() {
		return new VirtualKeyboardRelocatingOverlay();
	}

	@Override
	protected Font getJemuDisplayFont() {
		return getDisplay().getDisplayFont();
	}

	protected Computer getComputer() {
		return computer;
	}

	protected Display getDisplay() {
		return display;
	}

	protected KeyDispatcher getKeyDispatcher() {
		return keyDispatcher;
	}

	private class JemuFrameImpl extends JemuFrame implements ComponentListener {

		private Collection<Component> paddingComponents;

		private VirtualKeyboardSidePanel virtualKeyboardSidePanel; // one of paddingComponents (can be null)

		public JemuFrameImpl(boolean exitOnClose) {
			super(JemuDirectAmstradPc.this, "Amstrad CPC Emulator", exitOnClose);
			this.paddingComponents = new Vector<Component>(4);
			setResizable(false);
			updateBackground();
			addComponentListener(this);
		}

		void updateBackground() {
			MonitorMask mask = getDisplay().getMonitorMask();
			if (mask != null) {
				getContentPane().setBackground(mask.getAmbientBackgroundColor());
			}
		}

		synchronized void addPaddingAroundDisplay() {
			if (!hasPaddingAroundDisplay()) {
				Dimension screenSize = AmstradPcFrame.getScreenSize();
				Dimension displaySize = getDisplay().getSize();
				// Horizontal padding
				int padLeft = (screenSize.width - displaySize.width) / 2;
				int padRight = screenSize.width - displaySize.width - padLeft;
				if (padLeft > 0) {
					Component comp = Box.createHorizontalStrut(padLeft);
					getContentPane().add(comp, BorderLayout.WEST);
					getPaddingComponents().add(comp);
				}
				if (padRight > 0) {
					VirtualKeyboardSidePanel comp = new VirtualKeyboardSidePanel(padRight);
					getContentPane().add(comp, BorderLayout.EAST);
					getPaddingComponents().add(comp);
					setVirtualKeyboardSidePanel(comp);
				} else {
					setVirtualKeyboardSidePanel(null);
				}
				// Vertical padding
				int padTop = (screenSize.height - displaySize.height) / 2;
				int padBottom = screenSize.height - displaySize.height - padTop;
				if (padTop > 0) {
					Component comp = Box.createVerticalStrut(padTop);
					getContentPane().add(comp, BorderLayout.NORTH);
					getPaddingComponents().add(comp);
				}
				if (padBottom > 0) {
					Component comp = Box.createVerticalStrut(padBottom);
					getContentPane().add(comp, BorderLayout.SOUTH);
					getPaddingComponents().add(comp);
				}
			}
		}

		synchronized void removePaddingAroundDisplay() {
			if (hasPaddingAroundDisplay()) {
				for (Component comp : getPaddingComponents()) {
					getContentPane().remove(comp);
				}
				getPaddingComponents().clear();
				setVirtualKeyboardSidePanel(null);
			}
		}

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			super.amstradPcStarted(amstradPc);
			if (amstradPc.getMonitor().isFullscreen()) {
				// Force full screen as it is not consistently working
				new MonitorDisplayUltimateFullscreenMaker().start();
			}
		}

		@Override
		public void componentResized(ComponentEvent e) {
			// no action
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			if (!getMonitor().isFullscreen()) {
				Settings.set(Settings.FRAMEX, String.valueOf(getX()));
				Settings.set(Settings.FRAMEY, String.valueOf(getY()));
			}
		}

		@Override
		public void componentShown(ComponentEvent e) {
			// no action
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			// no action
		}

		@Override
		public void installMenuBar(AmstradMenuBar menuBar) {
			super.installMenuBar(menuBar);
			((JemuMonitorImpl) getMonitor()).setMenuBarWhenWindowed(menuBar);
		}

		@Override
		protected Component getContentComponent() {
			return getDisplay();
		}

		public Color getBackgroundColor() {
			return getContentPane().getBackground();
		}

		private boolean hasPaddingAroundDisplay() {
			return !getPaddingComponents().isEmpty();
		}

		private Collection<Component> getPaddingComponents() {
			return paddingComponents;
		}

		public VirtualKeyboardSidePanel getVirtualKeyboardSidePanel() {
			return virtualKeyboardSidePanel;
		}

		private void setVirtualKeyboardSidePanel(VirtualKeyboardSidePanel sidePanel) {
			this.virtualKeyboardSidePanel = sidePanel;
		}

		private class MonitorDisplayUltimateFullscreenMaker extends Thread {

			public MonitorDisplayUltimateFullscreenMaker() {
				super("MonitorDisplayUltimateFullscreenMaker");
				setDaemon(true);
			}

			@Override
			public void run() {
				SystemUtils.sleep(1000L);
				AmstradMonitor monitor = getAmstradPc().getMonitor();
				if (monitor.isFullscreen()) {
					int attempts = 0;
					while (isDisplayPaddedOnAllSides() && ++attempts <= 3) {
						System.out.println("Display is forced fullscreen");
						monitor.toggleFullscreen();
						monitor.toggleFullscreen();
						SystemUtils.sleep(500L);
					}
				}
			}

			private boolean isDisplayPaddedOnAllSides() {
				if (!hasPaddingAroundDisplay())
					return false;
				if (getPaddingComponents().size() < 4)
					return false;
				for (Component comp : getPaddingComponents()) {
					// Significant size?
					if (comp.getWidth() <= 2 || comp.getHeight() <= 2)
						return false;
				}
				return true;
			}

		}

	}

	private class JemuKeyboardImpl extends JemuKeyboard {

		public JemuKeyboardImpl() {
			super(JemuDirectAmstradPc.this);
		}

		@Override
		protected JemuKeyboardController createController() {
			return new JemuKeyboardControllerImpl(this);
		}

		@Override
		protected void connectKeyboardWhenPcStarted() {
			getDisplay().addKeyListener(this);
			getComputer().addKeyboardListener(this);
		}

		@Override
		protected void doBreakEscape() {
			getKeyDispatcher().breakEscape();
		}

		@Override
		public void pressKey(KeyEvent keyEvent) {
			getKeyDispatcher().keyPressed(keyEvent);
		}

		@Override
		public void releaseKey(KeyEvent keyEvent) {
			getKeyDispatcher().keyReleased(keyEvent);
		}

	}

	private class JemuKeyboardControllerImpl extends JemuKeyboardController {

		public JemuKeyboardControllerImpl(JemuKeyboardImpl keyboard) {
			super(keyboard);
		}

		@Override
		protected void doResetKeyModifiers() {
			getKeyDispatcher().resetKeyModifiers();
		}

	}

	private class JemuMemoryImpl extends JemuMemory {

		public JemuMemoryImpl() {
			super(JemuDirectAmstradPc.this);
		}

		@Override
		public byte readByte(int memoryAddress) {
			return getComputer().readByteFromUnmappedMemory(memoryAddress);
		}

		@Override
		public byte[] readBytes(int memoryOffset, int memoryLength) {
			return getComputer().readBytesFromUnmappedMemory(memoryOffset, memoryLength);
		}

		@Override
		public void writeByte(int memoryAddress, byte value) {
			getComputer().writeByteToUnmappedMemory(memoryAddress, value);
		}

		@Override
		public void writeBytes(int memoryOffset, byte[] data, int dataOffset, int dataLength) {
			getComputer().writeBytesToUnmappedMemory(memoryOffset, data, dataOffset, dataLength);
		}

		@Override
		protected synchronized void addMemoryTrap(AmstradMemoryTrap memoryTrap) {
			getComputer().addMemoryWriteObserver(new JemuMemoryTrapBridge(memoryTrap));
		}

		@Override
		public synchronized void removeMemoryTrapsAt(int memoryAddress) {
			List<MemoryWriteObserver> observers = new Vector<MemoryWriteObserver>(
					getComputer().getMemoryWriteObservers());
			for (MemoryWriteObserver observer : observers) {
				if (observer.getObservedMemoryAddress() == memoryAddress) {
					getComputer().removeMemoryWriteObserver(observer);
				}
			}
		}

		@Override
		public synchronized void removeAllMemoryTraps() {
			getComputer().removeAllMemoryWriteObservers();
		}

		@Override
		protected void pauseComputerInstantly() {
			getComputer().stop();
		}

		@Override
		protected void resumeComputerInstantly() {
			getComputer().start();
		}

	}

	private class JemuMonitorImpl extends JemuBaseMonitor {

		private AmstradMenuBar menuBarWhenWindowed;

		public JemuMonitorImpl() {
		}

		synchronized void update() {
			GateArray.doRender = false;
			boolean running = getComputer().isRunning();
			if (running)
				doPauseImmediately();
			if (isFullscreen()) {
				applyFullscreen();
			} else {
				applyWindowed();
			}
			if (running)
				doResume();
			new Thread(new Runnable() {

				@Override
				public void run() {
					SystemUtils.sleep(500L);
					GateArray.doRender = true;
				}
			}).start();
		}

		@Override
		protected void applyFullscreen() {
			Settings.setBoolean(Settings.FULLSCREEN, true);
			updateDisplaySize();
			if (hasFrame()) {
				Dimension screenSize = AmstradPcFrame.getScreenSize();
				JemuFrameImpl frame = getFrame();
				boolean visible = frame.isVisible();
				setMenuBarWhenWindowed(frame.getInstalledMenuBar()); // remember
				frame.uninstallMenuBar();
				frame.dispose();
				frame.setUndecorated(true);
				frame.setLocation(0, 0);
				frame.setSize(screenSize.width, screenSize.height);
				frame.addPaddingAroundDisplay();
				frame.setVisible(visible); // displayable after dispose()
			}
			getDisplay().requestFocus();
		}

		@Override
		protected void applyWindowed() {
			Settings.setBoolean(Settings.FULLSCREEN, false);
			updateDisplaySize();
			if (hasFrame()) {
				int frameX = Integer.parseInt(Settings.get(Settings.FRAMEX, "0"));
				int frameY = Integer.parseInt(Settings.get(Settings.FRAMEY, "0"));
				JemuFrameImpl frame = getFrame();
				boolean visible = frame.isVisible();
				frame.installMenuBar(getMenuBarWhenWindowed()); // restore
				frame.dispose();
				frame.setUndecorated(false);
				frame.removePaddingAroundDisplay();
				frame.pack();
				frame.setLocation(frameX, frameY);
				frame.setVisible(visible); // displayable after dispose()
			}
			getDisplay().requestFocus();
		}

		protected void updateDisplaySize() {
			boolean fullSizeGateArray = isFullGateArray();
			Computer computer = getComputer();
			computer.setLarge(fullSizeGateArray);
			Display display = getDisplay();
			display.setImageSize(computer.getDisplaySize(fullSizeGateArray),
					computer.getDisplayScale(fullSizeGateArray));
			computer.setDisplay(display); // must come after setImageSize() as it links the raster pixels
			int width = display.getScaledWidth();
			int height = display.getScaledHeight();
			if (isFullscreen()) {
				Dimension screenSize = AmstradPcFrame.getScreenSize();
				double scaleX = screenSize.getWidth() / width;
				double scaleY = screenSize.getHeight() / height;
				double scale = Math.min(scaleX, scaleY);
				width = (int) Math.ceil(scale * width);
				height = (int) Math.ceil(scale * height);
			}
			display.setSize(width, height);
		}

		@Override
		protected void doSetFullGateArray(boolean full) {
			boolean sizeChange = false;
			if (full) {
				if (!isSingleSize()) {
					doSetSingleSizeWithoutUpdate();
					sizeChange = true;
				}
			} else {
				if (!isDoubleSize()) {
					doSetDoubleSizeWithoutUpdate();
					sizeChange = true;
				}
			}
			Settings.setBoolean(Settings.LARGE, full);
			update();
			if (sizeChange) {
				fireMonitorSizeChangedEvent();
			}
		}

		@Override
		protected void doSetSingleSize() {
			doSetSingleSizeWithoutUpdate();
			update();
		}

		private void doSetSingleSizeWithoutUpdate() {
			Settings.setBoolean(Settings.DOUBLE, false);
			Settings.setBoolean(Settings.TRIPLE, false);
			Switches.doublesize = false;
			Switches.triplesize = false;
		}

		@Override
		protected void doSetDoubleSize() {
			doSetDoubleSizeWithoutUpdate();
			update();
		}

		private void doSetDoubleSizeWithoutUpdate() {
			Settings.setBoolean(Settings.DOUBLE, true);
			Settings.setBoolean(Settings.TRIPLE, false);
			Switches.doublesize = true;
			Switches.triplesize = false;
		}

		@Override
		protected void doSetTripleSize() {
			doSetTripleSizeWithoutUpdate();
			update();
		}

		private void doSetTripleSizeWithoutUpdate() {
			Settings.setBoolean(Settings.DOUBLE, false);
			Settings.setBoolean(Settings.TRIPLE, true);
			Switches.doublesize = false;
			Switches.triplesize = true;
		}

		@Override
		protected void applyMonitorModeColour() {
			Switches.monitormode = 0;
			Settings.set(Settings.MONITOR, Settings.MONITOR_COLOUR);
			getDisplay().changePerformance();
			jemu.system.cpc.CPC.resetInk = 1;
			updateFrameBackground();
		}

		@Override
		protected void applyMonitorModeGreen() {
			Switches.monitormode = 2;
			Settings.set(Settings.MONITOR, Settings.MONITOR_GREEN);
			getDisplay().changePerformance();
			jemu.system.cpc.CPC.resetInk = 1;
			updateFrameBackground();
		}

		@Override
		protected void applyMonitorModeGray() {
			Switches.monitormode = 3;
			Settings.set(Settings.MONITOR, Settings.MONITOR_GRAY);
			getDisplay().changePerformance();
			jemu.system.cpc.CPC.resetInk = 1;
			updateFrameBackground();
		}

		private void updateFrameBackground() {
			if (hasFrame()) {
				getFrame().updateBackground();
			}
		}

		@Override
		protected void doSetWindowAlwaysOnTop(boolean alwaysOnTop) {
			Settings.setBoolean(Settings.ONTOP, alwaysOnTop);
			if (hasFrame()) {
				getFrame().setAlwaysOnTop(alwaysOnTop);
			}
		}

		@Override
		protected Display getJemuDisplay() {
			return getDisplay();
		}

		private JemuFrameImpl getFrame() {
			return (JemuFrameImpl) JemuDirectAmstradPc.this.getFrame();
		}

		protected AmstradMenuBar getMenuBarWhenWindowed() {
			return menuBarWhenWindowed;
		}

		protected void setMenuBarWhenWindowed(AmstradMenuBar menuBar) {
			this.menuBarWhenWindowed = menuBar;
		}

	}

	private class VirtualKeyboardRelocatingOverlay extends VirtualKeyboardDisplayOverlay {

		private boolean rendered;

		private RenderContext renderContext;

		public VirtualKeyboardRelocatingOverlay() {
			super(JemuDirectAmstradPc.this);
		}

		@Override
		public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
				boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
			rendered = false;
			super.renderOntoDisplay(displayView, displayBounds, monitorInsets, offscreenImage, graphicsContext);
			if (!rendered)
				stopShowingInSidePanel();
		}

		@Override
		protected void renderVirtualKeyboard(AmstradVirtualKeyboard keyboard, AmstradVirtualKeyboardGridLayout layout,
				AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets) {
			VirtualKeyboardSidePanel sidePanel = getSidePanel();
			int sidePanelWidth = sidePanel != null ? sidePanel.getWidth() : -1;
			int gridWidth = computeKeyRowWidth(keyboard, layout);
			if (sidePanelWidth >= gridWidth + 4) {
				// render in side panel
				setupRenderContextForSidePanel(sidePanel, keyboard, layout);
				sidePanel.setShowVirtualKeyboard(true);
				sidePanel.repaint();
			} else {
				// render as overlay
				stopShowingInSidePanel();
				super.renderVirtualKeyboard(keyboard, layout, displayView, displayBounds, monitorInsets);
			}
			rendered = true;
		}

		private void setupRenderContextForSidePanel(VirtualKeyboardSidePanel sidePanel, AmstradVirtualKeyboard keyboard,
				AmstradVirtualKeyboardGridLayout layout) {
			Sizes sizes = getCurrentSizes();
			int gridWidth = computeKeyRowWidth(keyboard, layout);
			int gridHeight = computeKeyColumnHeight(keyboard, layout);
			int x0 = (sidePanel.getWidth() - gridWidth) / 2;
			int y0 = (sidePanel.getHeight() - gridHeight) / 2;
			RenderContext context = new RenderContext(keyboard, layout, null); // inject Graphics2D later
			context.setSizes(sizes);
			context.setLocationOfUpperLeftKey(new Point(x0, y0));
			setRenderContext(context);
		}

		public void renderAllKeys(Graphics2D g) {
			getSymbolRenderer().replaceGraphics2D(g);
			RenderContext context = getRenderContext();
			context.setGraphics2D(g);
			super.renderAllKeys(context);
		}

		private void stopShowingInSidePanel() {
			VirtualKeyboardSidePanel sidePanel = getSidePanel();
			if (sidePanel != null && sidePanel.isShowVirtualKeyboard()) {
				sidePanel.setShowVirtualKeyboard(false);
				sidePanel.repaint();
			}
		}

		private VirtualKeyboardSidePanel getSidePanel() {
			VirtualKeyboardSidePanel sidePanel = null;
			if (hasFrame()) {
				sidePanel = ((JemuFrameImpl) getFrame()).getVirtualKeyboardSidePanel();
			}
			return sidePanel;
		}

		private RenderContext getRenderContext() {
			return renderContext;
		}

		private void setRenderContext(RenderContext context) {
			this.renderContext = context;
		}

	}

	private class VirtualKeyboardSidePanel extends Box.Filler {

		private boolean showVirtualKeyboard;

		public VirtualKeyboardSidePanel(int fixedWidth) {
			super(new Dimension(fixedWidth, 0), new Dimension(fixedWidth, 0),
					new Dimension(fixedWidth, Short.MAX_VALUE));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (isShowVirtualKeyboard() && g instanceof Graphics2D) {
				getOverlay().renderAllKeys((Graphics2D) g);
			}
		}

		private VirtualKeyboardRelocatingOverlay getOverlay() {
			return (VirtualKeyboardRelocatingOverlay) getVirtualKeyboardDisplayOverlay();
		}

		public boolean isShowVirtualKeyboard() {
			return showVirtualKeyboard;
		}

		public void setShowVirtualKeyboard(boolean show) {
			this.showVirtualKeyboard = show;
		}

	}

}