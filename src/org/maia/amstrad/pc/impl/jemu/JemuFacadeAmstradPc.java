package org.maia.amstrad.pc.impl.jemu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.MenuBar;
import java.awt.Point;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.memory.AmstradMemoryTrap;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.util.AmstradUtils;

import jemu.core.device.memory.MemoryWriteObserver;
import jemu.settings.Settings;
import jemu.ui.Autotype;
import jemu.ui.Display;
import jemu.ui.FrameAdapter;
import jemu.ui.JEMU;
import jemu.ui.JEMU.PauseListener;
import jemu.ui.Switches;

public class JemuFacadeAmstradPc extends JemuAmstradPc implements PauseListener {

	private JEMU jemuInstance; // the JEMU facade instance

	public JemuFacadeAmstradPc() {
		this.jemuInstance = createJemuFacade();
	}

	protected JEMU createJemuFacade() {
		JEMU jemu = new JEMU(new JemuFrameBridge());
		jemu.setStandalone(true);
		jemu.setControlKeysEnabled(false);
		jemu.setMouseClickActionsEnabled(false);
		return jemu;
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
	public AmstradPcFrame displayInFrame(boolean exitOnClose) {
		AmstradPcFrame frame = super.displayInFrame(exitOnClose);
		getFrameBridge().setFrame(frame);
		getJemuInstance().alwaysOnTopCheck();
		return frame;
	}

	@Override
	public void start(boolean waitUntilReady, boolean silent) {
		boolean floppySound = false;
		synchronized (this) {
			checkNoInstanceRunning();
			checkNotStarted();
			checkNotTerminated();
			floppySound = Switches.FloppySound;
			if (silent)
				Switches.FloppySound = false;
			JEMU jemu = getJemuInstance();
			jemu.init();
			jemu.start();
			jemu.addPauseListener(this);
			jemu.addComputerPerformanceListener(this);
			jemu.getDisplay().addPrimaryDisplaySourceListener(this);
			getGraphicsContext().setPrimaryDisplaySourceResolution(
					new Dimension(jemu.getDisplay().getImageWidth(), jemu.getDisplay().getImageHeight()));
			getFrameBridge().pack();
			getMemoryTrapProcessor().start();
			setStarted(true);
			JemuAmstradPc.setInstanceRunning(true);
			fireStartedEvent();
		}
		if (waitUntilReady)
			waitUntilBasicRuntimeReady();
		if (silent)
			Switches.FloppySound = floppySound;
	}

	@Override
	public synchronized void reboot(boolean waitUntilReady, boolean silent) {
		checkStartedNotTerminated();
		boolean floppySound = Switches.FloppySound;
		if (silent)
			Switches.FloppySound = false;
		getJemuInstance().reBoot();
		fireRebootingEvent();
		if (waitUntilReady)
			waitUntilBasicRuntimeReady();
		if (silent)
			Switches.FloppySound = floppySound;
	}

	@Override
	protected void doPause() {
		getJemuInstance().pauseToggle(); // will be notified as PauseListener
	}

	@Override
	protected void doPauseImmediately() {
		getJemuInstance().pauseComputer(); // blocking call until computer is stopped
		firePausingEvent(); // in this case no PauseListener notification from JEMU
	}

	@Override
	protected void doResume() {
		getJemuInstance().pauseToggle(); // will be notified as PauseListener
	}

	@Override
	protected boolean selfManagesPauseStateEvents() {
		return true;
	}

	@Override
	public void pauseStateChanged(JEMU jemuInstance, boolean paused) {
		setPaused(paused);
		if (paused) {
			firePausingEvent();
		} else {
			fireResumingEvent();
		}
	}

	@Override
	public synchronized void terminate() {
		if (!isTerminated()) {
			if (isStarted()) {
				Autotype.clearText();
				getJemuInstance().quit();
				getMemoryTrapProcessor().stopProcessing();
				if (getMonitor().isAlternativeDisplaySourceShowing()) {
					getMonitor().resetDisplaySource();
				}
			}
			setTerminated(true);
			setInstanceRunning(false);
			fireTerminatedEvent();
		}
	}

	@Override
	protected void doLoad(File snapshotFile) {
		getJemuInstance().doAutoOpen(snapshotFile);
	}

	@Override
	protected Font getJemuDisplayFont() {
		return getJemuInstance().getDisplay().getDisplayFont();
	}

	private JemuFrameBridge getFrameBridge() {
		return (JemuFrameBridge) getJemuInstance().getFrameAdapter();
	}

	private JEMU getJemuInstance() {
		return jemuInstance;
	}

	private class JemuFrameImpl extends JemuFrame {

		public JemuFrameImpl(boolean exitOnClose) {
			super(JemuFacadeAmstradPc.this, exitOnClose);
		}

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			super.amstradPcStarted(amstradPc);
			AmstradMonitor monitor = amstradPc.getMonitor();
			if (monitor.isWindowFullscreen()) {
				// Force full screen as it is not consistently working
				if (getContentComponent().getLocationOnScreen().getX() != 0) {
					System.out.println("Force center display on screen");
					monitor.toggleWindowFullscreen();
					monitor.toggleWindowFullscreen();
				}
			}
			new MonitorDisplayUltimateCenterer().start(); // final check and attempts
		}

		@Override
		protected Component getContentComponent() {
			return getJemuInstance();
		}

		private class MonitorDisplayUltimateCenterer extends Thread {

			public MonitorDisplayUltimateCenterer() {
				super("MonitorDisplayUltimateCenterer");
				setDaemon(true);
			}

			@Override
			public void run() {
				AmstradUtils.sleep(1000L);
				AmstradMonitor monitor = getAmstradPc().getMonitor();
				if (monitor.isWindowFullscreen()) {
					JComponent displayComp = monitor.getDisplayComponent();
					int expectedX = (getScreenSize().width - displayComp.getWidth()) / 2;
					int attempts = 0;
					while (Math.abs(displayComp.getLocationOnScreen().x - expectedX) > 2 && ++attempts <= 3) {
						System.out.println("Ultimate center display on screen");
						monitor.toggleWindowFullscreen();
						monitor.toggleWindowFullscreen();
						AmstradUtils.sleep(500L);
					}
					System.out.println("Display is centered fullscreen");
				} else if (extendsOutsideScreen()) {
					centerOnScreen();
					System.out.println("Display is centered on screen");
				}
			}

			private boolean extendsOutsideScreen() {
				Dimension screen = getScreenSize();
				Dimension size = getSize();
				Point loc = getLocationOnScreen();
				return loc.x < 0 || loc.y < 0 || loc.x + size.width > screen.width
						|| loc.y + size.height > screen.height;
			}

		}

	}

	private class JemuFrameBridge extends FrameAdapter {

		private JFrame frame;

		public JemuFrameBridge() {
			this(null);
		}

		public JemuFrameBridge(JFrame frame) {
			setFrame(frame);
		}

		@Override
		public void setTitle(String title) {
			if (!isFrameLess()) {
				if (Settings.getBoolean(Settings.UPDATETITLE, true)) {
					getFrame().setTitle(title);
				}
			}
		}

		@Override
		public void setMenuBar(MenuBar menuBar) {
			if (!isFrameLess()) {
				if (Settings.getBoolean(Settings.SHOWMENU, true)) {
					getFrame().setMenuBar(menuBar);
				} else {
					if (getFrame().getJMenuBar() != null && !getAmstradPc().getMonitor().isWindowFullscreen()) {
						getFrame().getJMenuBar().setVisible(true);
					}
				}
			}
		}

		@Override
		public void removeMenuBar(MenuBar menuBar) {
			if (!isFrameLess()) {
				if (Settings.getBoolean(Settings.SHOWMENU, true)) {
					getFrame().remove(menuBar);
				} else {
					if (getFrame().getJMenuBar() != null) {
						getFrame().getJMenuBar().setVisible(false);
					}
				}
			}
		}

		@Override
		public void setLocation(int x, int y) {
			if (!isFrameLess()) {
				getFrame().setLocation(x, y);
			}
		}

		@Override
		public void setSize(int width, int height) {
			if (!isFrameLess()) {
				getFrame().setSize(width, height);
			}
		}

		@Override
		public void setResizable(boolean resizable) {
			if (!isFrameLess()) {
				getFrame().setResizable(resizable);
			}
		}

		@Override
		public void setAlwaysOnTop(boolean alwaysOnTop) {
			if (!isFrameLess()) {
				getFrame().setAlwaysOnTop(alwaysOnTop);
			}
		}

		@Override
		public void setVisible(boolean visible) {
			if (!isFrameLess()) {
				getFrame().setVisible(visible);
			}
		}

		@Override
		public void pack() {
			if (!isFrameLess()) {
				getFrame().pack();
			}
		}

		@Override
		public void dispose() {
			if (!isFrameLess()) {
				getFrame().dispose();
			}
		}

		@Override
		public void setUndecorated(boolean undecorated) {
			if (!isFrameLess()) {
				getFrame().setUndecorated(undecorated);
			}
		}

		@Override
		public int getX() {
			if (!isFrameLess()) {
				return getFrame().getX();
			} else {
				return 0;
			}
		}

		@Override
		public int getY() {
			if (!isFrameLess()) {
				return getFrame().getY();
			} else {
				return 0;
			}
		}

		@Override
		public Dimension getSize() {
			if (!isFrameLess()) {
				return getFrame().getSize();
			} else {
				return new Dimension();
			}
		}

		@Override
		public FileDialog createFileDialog(String title, int mode) {
			if (isFrameLess())
				throw new HeadlessException("Not backed by any frame");
			return new FileDialog(getFrame(), title, mode);
		}

		private AmstradPc getAmstradPc() {
			return JemuFacadeAmstradPc.this;
		}

		private boolean isFrameLess() {
			return getFrame() == null;
		}

		private JFrame getFrame() {
			return frame;
		}

		public void setFrame(JFrame frame) {
			this.frame = frame;
		}

	}

	private class JemuKeyboardImpl extends JemuKeyboard {

		public JemuKeyboardImpl() {
			super(JemuFacadeAmstradPc.this);
		}

		@Override
		protected JemuKeyboardController createController() {
			return new JemuKeyboardControllerImpl(this);
		}

		@Override
		protected void connectKeyboardWhenPcStarted() {
			getJemuInstance().getDisplay().addKeyListener(this);
			getJemuInstance().addComputerKeyboardListener(this);
		}

		@Override
		protected void doBreakEscape() {
			getJemuInstance().breakEscape();
		}

	}

	private class JemuKeyboardControllerImpl extends JemuKeyboardController {

		public JemuKeyboardControllerImpl(JemuKeyboardImpl keyboard) {
			super(keyboard);
		}

		@Override
		protected void doResetKeyModifiers() {
			getJemuInstance().resetKeyModifiers();
		}

	}

	private class JemuMemoryImpl extends JemuMemory {

		public JemuMemoryImpl() {
			super(JemuFacadeAmstradPc.this);
		}

		@Override
		public byte readByte(int memoryAddress) {
			return getJemuInstance().readByteFromUnmappedMemory(memoryAddress);
		}

		@Override
		public byte[] readBytes(int memoryOffset, int memoryLength) {
			return getJemuInstance().readBytesFromUnmappedMemory(memoryOffset, memoryLength);
		}

		@Override
		public void writeByte(int memoryAddress, byte value) {
			getJemuInstance().writeByteToUnmappedMemory(memoryAddress, value);
		}

		@Override
		public void writeBytes(int memoryOffset, byte[] data, int dataOffset, int dataLength) {
			getJemuInstance().writeBytesToUnmappedMemory(memoryOffset, data, dataOffset, dataLength);
		}

		@Override
		protected synchronized void addMemoryTrap(AmstradMemoryTrap memoryTrap) {
			getJemuInstance().addMemoryWriteObserver(new JemuMemoryTrapBridge(memoryTrap));
		}

		@Override
		public synchronized void removeMemoryTrapsAt(int memoryAddress) {
			List<MemoryWriteObserver> observers = new Vector<MemoryWriteObserver>(
					getJemuInstance().getMemoryWriteObservers());
			for (MemoryWriteObserver observer : observers) {
				if (observer.getObservedMemoryAddress() == memoryAddress) {
					getJemuInstance().removeMemoryWriteObserver(observer);
				}
			}
		}

		@Override
		public synchronized void removeAllMemoryTraps() {
			getJemuInstance().removeAllMemoryWriteObservers();
		}

		@Override
		protected void pauseComputerInstantly() {
			getJemuInstance().pauseComputer();
		}

		@Override
		protected void resumeComputerInstantly() {
			getJemuInstance().goComputer();
		}

	}

	private class JemuMonitorImpl extends JemuBaseMonitor {

		public JemuMonitorImpl() {
		}

		@Override
		protected Display getJemuDisplay() {
			return getJemuInstance().getDisplay();
		}

		@Override
		protected void changeMonitorModeToColour() {
			getJemuInstance().changeMonitorModeToColour();
		}

		@Override
		protected void changeMonitorModeToGreen() {
			getJemuInstance().changeMonitorModeToGreen();
		}

		@Override
		protected void changeMonitorModeToGray() {
			getJemuInstance().changeMonitorModeToGray();
		}

		@Override
		protected void toggleToFullscreen() {
			getJemuInstance().FullSize();
		}

		@Override
		protected void toggleToWindowed() {
			getJemuInstance().FullSize();
		}

		@Override
		protected void doSetWindowAlwaysOnTop(boolean alwaysOnTop) {
			getJemuInstance().setAlwaysOnTop(alwaysOnTop);
		}

	}

}