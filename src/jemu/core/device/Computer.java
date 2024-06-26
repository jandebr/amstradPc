package jemu.core.device;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import jemu.core.Util;
import jemu.core.cpu.Processor;
import jemu.core.device.floppy.Drive;
import jemu.core.device.memory.Memory;
import jemu.core.device.memory.MemoryWriteObserver;
import jemu.settings.Settings;
import jemu.ui.Display;
import jemu.ui.Switches;
import jemu.util.diss.Disassembler;

/**
 * Title: JEMU Description: The Java Emulation Platform Copyright: Copyright (c) 2002 Company:
 * 
 * @author
 * @version 1.0
 */

public abstract class Computer extends Device implements Runnable, ItemListener {
	protected boolean webstart = false;
	protected final String server = "http://yourdomain.com/";

	public JComboBox box = new JComboBox();
	public String[] content = new String[20000];
	public Frame dummy = new Frame();
	public int count, rubbish;
	public int choosen = 0;
	ItemEvent ev;
	/*
	 * Server is your Server path, where JavaCPCWebstart.jar is located for example: http://yourdomain.com/jemu/
	 */

	// Entries are Name, Key, Class, Shown in list
	public static final ComputerDescriptor[] COMPUTERS = {
			new ComputerDescriptor("CPC464T", "Amstrad/Schneider CPC 464", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("CPC464", "Amstrad/Schneider CPC 464", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("CPC664", "Amstrad/Schneider CPC 664", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("CPC6128", "Amstrad/Schneider CPC 6128", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("CPC464PARA", "Amstrad CPC 464 / ParaDos", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("PARADOS", "Amstrad CPC 6128 / ParaDos", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("ROMPACK", "CPC 6128 / ParaDos / RomPack", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("ROMSYM", "CPC 6128 / SymbOS / RomPack", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("ST128", "Amstrad CPC 6128 / ST 128", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("ART", "Amstrad CPC 6128 / OCP Art Studio", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("MEGA", "Amstrad CPC 6128 Mega Edition", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("CPC6128fr", "CPC 6128 - French ROMs", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("CPC6128es", "CPC 6128 - Spanish ROMs", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("SymbOS", "Symbiosis SymbOS", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("FutureOS", "CPC 6128 / Future-OS", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("KCcomp", "KC compact", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("COMCPC", "Surprise CPC", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("JEMCPC", "JEMU CPC", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("CUSTOM", "CUSTOM", "jemu.system.cpc.CPC"),
			new ComputerDescriptor("CPCPLUS", "CPC 6128+ (Roms)", "jemu.system.cpc.CPC") };

	public int zipcount = 0;
	public static final String DEFAULT_COMPUTER = "CPC6128";

	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_SNAPSHOT = 1;
	public static final int TYPE_DISC_IMAGE = 2;
	public static final int TYPE_TAPE_IMAGE = 3;

	public static final int STOP = 0;
	public static final int STEP = 1;
	public static final int STEP_OVER = 2;
	public static final int RUN = 3;

	public static final int MAX_FRAME_SKIP = 20;
	public static final int MAX_FILE_SIZE = 1024 * 1024 * 20; // 40960K maximum

	protected Applet applet;
	// protected String name;
	private Thread thread = new Thread(this, "JemuComputer");
	private boolean stopped = false;
	private int action = STOP;
	private boolean running = false;
	private boolean confirmStep = false;
	private boolean verboseStopStart = false;
	protected long startTime;
	protected long startCycles;
	protected String romPath;
	protected String filePath;
	protected Vector<FileDescriptor> files = null;
	protected Display display;
	protected int frameSkip = 0;
	protected int runTo = -1;
	protected int mode = STOP;
	protected long maxResync = 200;
	protected int currentDrive = 0;
	// Devices used in this computer
	protected Vector devices = new Vector();

	// Listeners for stopped emulation
	protected Vector listeners = new Vector(1);

	// Listeners for keyboard
	private List<ComputerKeyboardListener> keyboardListeners;
	private boolean escapeKeyMode = false;
	private KeyEvent escapeKeyEventPressed;
	private KeyEvent escapeKeyEventReleased;

	// Performance observation
	private List<ComputerPerformanceListener> performanceListeners;
	private long processorMonitoringStartTime = -1L;
	private int processorTimerSyncs;
	private int processorLaggingSyncs;
	private int processorThrottledSyncs;

	@SuppressWarnings("unchecked")
	public static Computer createComputer(Applet applet, String name) throws Exception {
		for (int index = 0; index < COMPUTERS.length; index++) {
			if (COMPUTERS[index].key.equalsIgnoreCase(name)) {
				Class cl = Util.findClass(null, COMPUTERS[index].className);
				Constructor con = cl.getConstructor(new Class[] { Applet.class, String.class });
				return (Computer) con.newInstance(new Object[] { applet, name });
			}
		}
		throw new Exception("Computer " + name + " not found");
	}

	public Computer(Applet applet, String name) {
		super("Computer: " + name);
		this.applet = applet;
		this.name = name;
		this.keyboardListeners = new Vector<ComputerKeyboardListener>();
		this.performanceListeners = new Vector<ComputerPerformanceListener>();
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	protected void setBasePath(String path) {
		String systemDir = Settings.get(Settings.SYSTEM_DIR, "system");
		File dir = new File(systemDir, path);
		romPath = new File(dir, "rom").getPath() + File.separator;
		filePath = new File(dir, "file").getPath() + File.separator;
	}

	public void initialise() {
		reset();
	}

	public Device addDevice(Device device) {
		return addDevice(device, null);
	}

	@SuppressWarnings("unchecked")
	public Device addDevice(Device device, String name) {
		if (name != null)
			device.setName(name);
		devices.addElement(device);
		return device;
	}

	public Vector getDevices() {
		return devices;
	}

	public final InputStream openFile(String name) throws Exception {
		zipcount = 0;
		InputStream result;

		// Webstart Application uses this:
		if (webstart) {

			try {
				result = new FileInputStream(name);
			} catch (Exception e) {
				result = new URL(server + name).openStream();
			}

		}
		// Applet & Standalone emulator uses this code:
		else {
			try {
				result = new URL(applet.getCodeBase(), name).openStream();
			} catch (Exception e) {
				result = new FileInputStream(name);
			}
		}

		if (name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jtp")) {
			checkZip(name);
			ZipEntry zipentry;
			ZipInputStream zipinput = new ZipInputStream(result);
			if (zipcount - rubbish <= 1) {
				while ((zipentry = zipinput.getNextEntry()) != null) {
					String EntryName = zipentry.getName().toLowerCase();
					if (EntryName.endsWith(".dsk") || // amstrad diskimage
							EntryName.endsWith(".cdt") || // amstrad tape
							EntryName.endsWith(".tzx") || // spectrum tape
							EntryName.endsWith(".bin") || // amstrad binary
							EntryName.endsWith(".bas") || // amstrad basic
							EntryName.endsWith(".cpr") || // amstrad + cartridge
							EntryName.endsWith(".sna") || // amstrad snapshot
							EntryName.endsWith(".rom") || // rom (general)
							EntryName.endsWith(".wav") || // PCM .wav file
							EntryName.endsWith(".csw") // Compressed Square Wave .csw file
					) {
						System.out.println("Loading: " + EntryName);
						return zipinput;
					} else {
						System.out.println("Skipping " + zipentry.getName());
					}
				}
			}
			JOptionPane pane = new JOptionPane(box, JOptionPane.QUESTION_MESSAGE, JOptionPane.CLOSED_OPTION);
			if (!Switches.loaded) {
				Switches.loaded = true;
				choosen = 0;
				pane.createDialog(null, "Choose file:").setVisible(true);
				while (content[choosen].toLowerCase().endsWith(".diz")) {
					choosen++;
					System.out.println("file_id.diz file found... skipping!");
				}
				System.out.println("You have choosen " + content[choosen]);
				Switches.choosenname = content[choosen];
			}
			for (int ir = 0; ir < choosen + 1; ir++)
				zipinput.getNextEntry();
			return zipinput;
		}

		if (name.toLowerCase().endsWith(".jar")) {
			JarInputStream str = new JarInputStream(result);
			str.getNextEntry();
			result = str;
			str.close();
		}
		if (name.toLowerCase().endsWith(".snz") || name.toLowerCase().endsWith(".taz")
				|| name.toLowerCase().endsWith(".dsz") || name.toLowerCase().endsWith(".szk")) {
			System.out.println("Opening GZip compressed file...");
			GZIPInputStream str = new GZIPInputStream(result);
			result = str;
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public void checkZip(String name) throws Exception {
		InputStream result;

		// Webstart Application uses this:
		if (webstart) {

			try {
				result = new FileInputStream(name);
			} catch (Exception e) {
				result = new URL(server + name).openStream();
			}

		}
		// Applet & Standalone emulator uses this code:
		else {
			try {
				result = new URL(applet.getCodeBase(), name).openStream();
			} catch (Exception e) {
				result = new FileInputStream(name);
			}
		}
		if (!Switches.loaded)
			System.out.println("Checking zip....");
		zipcount = 0;
		LinkedList<String> entries = new LinkedList<String>();
		count = rubbish = 0;
		ZipEntry zipentry;
		ZipInputStream zipinputr = new ZipInputStream(result);
		while ((zipentry = zipinputr.getNextEntry()) != null) {
			String EntryName = zipentry.getName().toLowerCase();
			entries.add(EntryName);
		}
		box.removeAllItems();
		box.addItemListener(this);
		Iterator<String> i = entries.iterator();
		while (i.hasNext()) {
			String found = i.next();
			content[count] = found;
			String EntryName = found.toLowerCase();
			System.out.println("Entry found: " + EntryName);
			if (EntryName.endsWith(".dsk") || // amstrad diskimage
					EntryName.endsWith(".cdt") || // amstrad tape
					EntryName.endsWith(".tzx") || // spectrum tape
					EntryName.endsWith(".bin") || // amstrad binary
					EntryName.endsWith(".bas") || // amstrad basic
					EntryName.endsWith(".cpr") || // amstrad + cartridge
					EntryName.endsWith(".sna") || // amstrad snapshot
					EntryName.endsWith(".rom") || // rom (general)
					EntryName.endsWith(".wav") || // PCM .wav file
					EntryName.endsWith(".csw") // Compressed Square Wave .csw file
			) {
				box.addItem(found);
				if (!Switches.loaded)
					System.out.println("Found: " + found);
			} else {
				if (!Switches.loaded)
					System.out.println("Rubbish: " + found);
				rubbish++;
			}
			count++;
			zipcount++;
			System.out.println("******* Count: " + count + " Rubbish: " + rubbish + " Found: " + found);
		}
		zipinputr.close();
		// if (count==1 && rubbish ==1)
		// Switches.loaded = true;
	}

	public void itemStateChanged(ItemEvent e) {
		JComboBox selectedChoice = (JComboBox) e.getSource();
		for (int i = 0; i < count; i++)
			if (selectedChoice.getSelectedItem().equals(content[i]))
				choosen = i;
	}

	protected int readStream(InputStream stream, byte[] buffer, int offs, int size) throws Exception {
		return readStream(stream, buffer, offs, size, true);
	}

	protected int readStream(InputStream stream, byte[] buffer, int offs, int size, boolean error) throws Exception {
		while (size > 0) {
			int read = stream.read(buffer, offs, size);
			if (read == -1) {
				if (error)
					throw new Exception("Unexpected end of stream");
				else
					break;
			} else {
				offs += read;
				size -= read;
			}
		}
		return offs;
	}

	protected static byte[] SKIP_BUFFER = new byte[1024];

	protected void skipStream(InputStream stream, int size) throws Exception {
		while (size > 0) {
			int bytes = size > 1024 ? 1024 : size;
			stream.read(SKIP_BUFFER, 0, bytes);
			size -= bytes;
		}
	}

	public byte[] getFile(String name) {
		return getFile(name, MAX_FILE_SIZE, true);
	}

	public byte[] getFile(String name, int size) {
		return getFile(name, size, false);
	}

	public byte[] getFile(String name, int size, boolean crop) {
		byte[] buffer = new byte[size];
		int offs = 0;
		try {
			InputStream stream = null;
			try {
				stream = openFile(name);
				while (size > 0) {
					int read = stream.read(buffer, offs, size);
					if (read == -1)
						break;
					else {
						offs += read;
						size -= read;
					}
				}
			} finally {
				if (stream != null)
					stream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (crop && offs < buffer.length) {
			byte[] result = new byte[offs];
			System.arraycopy(buffer, 0, result, 0, offs);
			buffer = result;
		}
		return buffer;
	}

	public void setDisplay(Display value) {
		display = value;
		escapeKeyEventPressed = new KeyEvent(display, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_ESCAPE,
				KeyEvent.CHAR_UNDEFINED);
		escapeKeyEventReleased = new KeyEvent(display, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_ESCAPE,
				KeyEvent.CHAR_UNDEFINED);
		displaySet();
	}

	public Display getDisplay() {
		return display;
	}

	protected void displaySet() {
	}

	// For now, only supporting a single Processor
	public Disassembler getDisassembler() {
		return null;
	}

	public abstract Processor getProcessor();

	public abstract Memory getMemory();

	public void addMemoryWriteObserver(MemoryWriteObserver observer) {
		getMemory().addWriteObserver(observer);
	}

	public void removeMemoryWriteObserver(MemoryWriteObserver observer) {
		getMemory().removeWriteObserver(observer);
	}

	public void removeAllMemoryWriteObservers() {
		getMemory().removeAllWriteObservers();
	}

	public List<MemoryWriteObserver> getMemoryWriteObservers() {
		return getMemory().getWriteObservers();
	}

	public void breakEscape() {
		processKeyEvent(escapeKeyEventPressed);
		sleepBetweenKeyEvents();
		processKeyEvent(escapeKeyEventReleased);
		sleepBetweenKeyEvents();
		processKeyEvent(escapeKeyEventPressed);
		sleepBetweenKeyEvents();
		processKeyEvent(escapeKeyEventReleased);
		sleepBetweenKeyEvents();
	}

	public void processKeyEvent(KeyEvent e) {
		if (mode == RUN) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					escapeKeyMode = true;
					firePressEscapeKey();
				} else if (escapeKeyMode) {
					escapeKeyMode = false;
					fireSuppressEscapeKey();
				}
			} else if (e.getID() == KeyEvent.KEY_RELEASED)
				keyReleased(e);
		}
	}

	private static void sleepBetweenKeyEvents() {
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {
		}
	}

	public void notifyEnterBasicKeyboardPrompt(BasicKeyboardPromptModus modus) {
		fireEnterBasicKeyboardPrompt(modus);
	}

	public void notifyExitBasicKeyboardPrompt(BasicKeyboardPromptModus modus) {
		fireExitBasicKeyboardPrompt(modus);
	}

	public abstract void recordKeys();

	public abstract void playKeys();

	public abstract void stopKeys();

	public abstract void keyPressed(KeyEvent e);

	public abstract void keyReleased(KeyEvent e);

	public abstract void AtKey();

	public abstract void bootCPM();

	public abstract void typeCat();

	public abstract void typeRun();

	public abstract void bootDisk();

	public abstract void bootDiskb();

	public abstract void AutoType();

	public abstract void MouseFire1();

	public abstract void MouseFire2();

	public abstract void MouseReleaseFire1();

	public abstract void MouseReleaseFire2();

	public abstract void tape_WAV_save();

	public abstract void CDT2WAV();

	public abstract void tapeEject();

	/*
	 * Subclasses to override
	 */
	public void loadFile(int type, String name) throws Exception {
	}

	/*
	 * Subclasses to override
	 */
	public byte readByteFromUnmappedMemory(int memoryAddress) {
		return 0;
	}

	/*
	 * Subclasses to override
	 */
	public byte[] readBytesFromUnmappedMemory(int memoryOffset, int memoryLength) {
		byte[] data = new byte[memoryLength];
		for (int i = 0; i < memoryLength; i++) {
			data[i] = readByteFromUnmappedMemory(memoryOffset + i);
		}
		return data;
	}

	/*
	 * Subclasses to override
	 */
	public void writeByteToUnmappedMemory(int memoryAddress, byte value) {
	}

	/*
	 * Subclasses to override
	 */
	public void writeBytesToUnmappedMemory(int memoryOffset, byte[] data, int dataOffset, int dataLength) {
		for (int i = 0; i < dataLength; i++) {
			writeByteToUnmappedMemory(memoryOffset + i, data[dataOffset + i]);
		}
	}

	public abstract Dimension getDisplaySize(boolean large);

	public abstract void reSync();

	public void setLarge(boolean value) {
	}

	public Dimension getDisplayScale(boolean large) {
		return large ? Display.SCALE_2 : Display.SCALE_1;
	}

	public void run() {
		System.out.println(this + " Thread start");
		while (!isStopped()) {
			boolean shouldRun = false;
			int shouldRunAction = 0;
			synchronized (this) {
				while (!isStopped() && action == STOP) {
					setRunning(false);
					fireActionEvent();
					if (verboseStopStart)
						System.out.println("Enter waitUninterrupted");
					waitUninterrupted();
					if (verboseStopStart)
						System.out.println("Exit waitUninterrupted");
				}
				shouldRun = !isStopped() && action != STOP;
				shouldRunAction = action;
			}
			if (shouldRun) {
				synchronized (this) {
					mode = shouldRunAction;
					getProcessor().canRun();
					setRunning(true);
					setAction(STOP);
				}
				try {
					startCycles = getProcessor().getCycles();
					startTime = System.currentTimeMillis();
					if (verboseStopStart)
						System.out.println(this + " Emulate start (mode " + mode + ")");
					emulate(mode);
					if (verboseStopStart)
						System.out.println(this + " Emulate end");
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					if (mode == STEP || mode == STEP_OVER) {
						setConfirmStep(true);
					}
				}
			}
		}
		setRunning(false);
		fireActionEvent();
		System.out.println(this + " Thread end");
	}

	public synchronized void start() {
		if (isStopped())
			return;
		synchronized (thread) {
			if (!running) {
				setAction(RUN);
				while (!running) {
					waitUninterrupted();
				}
			}
		}
	}

	public synchronized void stop() {
		if (isStopped())
			return;
		synchronized (thread) {
			if (running) {
				setAction(STOP);
				display.setPainted(true);
				getProcessor().shouldStop(); // async
				while (running) {
					waitUninterrupted();
				}
			}
		}
		processorMonitoringStartTime = -1L;
	}

	public synchronized void step() {
		synchronized (thread) {
			stop();
			setAction(STEP);
			setConfirmStep(false);
			while (!confirmStep) {
				waitUninterrupted();
			}
		}
	}

	public synchronized void stepOver() {
		synchronized (thread) {
			stop();
			setAction(STEP_OVER);
			setConfirmStep(false);
			while (!confirmStep) {
				waitUninterrupted();
			}
		}
	}

	public void dispose() {
		if (thread == null)
			return; // already disposed
		if (verboseStopStart)
			System.out.println(this + " Dispose");
		stop();
		stopped = true;
		setAction(STOP); // awake when in stop-wait
		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		thread = null;
		display = null;
		applet = null;
	}

	public boolean isStopped() {
		return stopped;
	}

	public synchronized boolean isRunning() {
		return running;
	}

	private synchronized void setRunning(boolean newRunning) {
		running = newRunning;
		if (verboseStopStart) {
			System.out.println("Computer.setRunning(" + newRunning + ")");
			Thread.dumpStack();
		}
		this.notifyAll();
	}

	private synchronized void setAction(int newAction) {
		action = newAction;
		if (verboseStopStart) {
			System.out.println("Computer.setAction(" + newAction + ")");
			Thread.dumpStack();
		}
		this.notifyAll();
	}

	private synchronized void setConfirmStep(boolean newConfirmStep) {
		confirmStep = newConfirmStep;
		if (verboseStopStart) {
			System.out.println("Computer.setConfirmStep(" + newConfirmStep + ")");
			Thread.dumpStack();
		}
		this.notifyAll();
	}

	private synchronized void waitUninterrupted() {
		boolean interrupted;
		do {
			interrupted = false;
			try {
				this.wait();
			} catch (InterruptedException e) {
				interrupted = true;
			}
		} while (interrupted);
	}

	protected void emulate(int mode) {
		if (mode == STEP) {
			getProcessor().step();
		} else if (mode == STEP_OVER) {
			getProcessor().stepOver();
		} else if (mode == RUN) {
			if (runTo == -1) {
				getProcessor().run();
			} else {
				getProcessor().runTo(runTo);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void addActionListener(ActionListener listener) {
		listeners.addElement(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.removeElement(listener);
	}

	protected void fireActionEvent() {
		ActionEvent e = new ActionEvent(this, 0, null);
		for (int i = 0; i < listeners.size(); i++)
			((ActionListener) listeners.elementAt(i)).actionPerformed(e);
	}

	public String getROMPath() {
		return romPath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void reset() {
		/*
		 * System.out.println(this + " Reset"); stop(); getProcessor().reset(); start();
		 */
	}

	public void eject() {
	}

	public void showDevices() {
		final Vector devices = getDevices();
		final int n = devices.size();
		for (int i = 0; i < n; i++) {
			final Object device = devices.get(i);
			System.out.println("Device connected: " + device);
		}
	}

	public Vector<FileDescriptor> getFiles() {
		if (files == null) {
			files = new Vector<FileDescriptor>();
			LineNumberReader reader = null;
			try {
				reader = new LineNumberReader(new InputStreamReader(openFile(filePath + "Files.txt")));
				String line;
				while ((line = reader.readLine()) != null) {
					int iDesc = line.indexOf('=');
					if (iDesc != -1) {
						String desc = line.substring(0, iDesc).trim();
						int iName = line.indexOf(',', iDesc + 1);
						if (iName == -1)
							iName = line.length();
						String names = line.substring(iDesc + 1, iName).trim();
						String instructions = iName < line.length()
								? line.substring(iName + 1).trim().replace('|', '\n')
								: "";
						files.addElement(new FileDescriptor(desc, names, instructions));
					}
				}
			} catch (Exception e) {
				System.out.println("Cannot get file list for " + this);
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
		return files;
	}

	public String getFileInfo(String fileName) {
		String result = null;
		getFiles();
		for (int i = 0; i < files.size(); i++) {
			FileDescriptor file = (FileDescriptor) files.elementAt(i);
			if (file.filename.equalsIgnoreCase(fileName)) {
				result = file.instructions;
				break;
			}
		}
		return result;
	}

	protected void syncProcessor() {
		long deltaCycles = getProcessor().getCycles() - startCycles;
		long count = ((deltaCycles * 2000 / getProcessor().getCyclesPerSecond()) + 1) / 2;
		syncProcessor(null, count, 200);
	}

	protected void syncProcessor(ComputerTimer timer) {
		syncProcessor(timer, timer.getUpdates(), timer.getDeviation());
	}

	protected void syncProcessor(ComputerTimer timer, long count, long deviation) {
		processorTimerSyncs++;
		startTime += count;
		startCycles = getProcessor().getCycles();
		long time = timer != null ? timer.getCount() : System.currentTimeMillis();
		if (time < startTime - deviation) {
			setFrameSkip(0);
			startTime = time;
		} else if (time > startTime) {
			processorLaggingSyncs++;
			if (frameSkip == MAX_FRAME_SKIP) {
				setFrameSkip(0);
				if (timer != null)
					timer.resync();
				// System.out.println(" R: " + (time - startTime));
				startTime = (timer != null ? timer.getCount() : System.currentTimeMillis());
			} else {
				// System.out.print(" S" + frameSkip);
				setFrameSkip(frameSkip + 1);
			}
		} else {
			try {
				setFrameSkip(0);
				boolean throttled = false;
				long start = System.currentTimeMillis();
				while ((time = timer != null ? timer.getCount() : System.currentTimeMillis()) < startTime) {
					if (timer != null && System.currentTimeMillis() - start > maxResync) {
						timer.resync();
						// System.out.println("resync 2");
						startTime = timer.getCount();
						break;
					}
					Thread.sleep(1);
					throttled = true;
				}
				if (throttled)
					processorThrottledSyncs++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long now = System.currentTimeMillis();
		if (processorMonitoringStartTime < 0L || now >= processorMonitoringStartTime + 1000L) {
			if (processorMonitoringStartTime >= 0L)
				fireProcessorPerformanceUpdate(now - processorMonitoringStartTime, processorTimerSyncs,
						processorLaggingSyncs, processorThrottledSyncs);
			processorMonitoringStartTime = now;
			processorTimerSyncs = 0;
			processorLaggingSyncs = 0;
			processorThrottledSyncs = 0;
		}
	}

	public void setMaxResync(long value) {
		maxResync = value;
	}

	public void setFrameSkip(int value) {
		frameSkip = value;
	}

	public void displayLostFocus() {
	}

	public void updateDisplay(boolean wait) {
	}

	public String getName() {
		return name;
	}

	public void setRunToAddress(int value) {
		runTo = value;
	}

	public void clearRunToAddress() {
		runTo = -1;
	}

	public int getMode() {
		return mode;
	}

	public Drive[] getFloppyDrives() {
		return null;
	}

	public int getCurrentDrive() {
		return currentDrive;
	}

	public void setCurrentDrive(int drive) {
		currentDrive = drive;
	}

	public void addKeyboardListener(ComputerKeyboardListener listener) {
		getKeyboardListeners().add(listener);
	}

	public void removeKeyboardListener(ComputerKeyboardListener listener) {
		getKeyboardListeners().remove(listener);
	}

	public List<ComputerKeyboardListener> getKeyboardListeners() {
		return keyboardListeners;
	}

	protected void firePressEscapeKey() {
		for (ComputerKeyboardListener listener : getKeyboardListeners())
			listener.computerPressEscapeKey(this);
	}

	protected void fireSuppressEscapeKey() {
		for (ComputerKeyboardListener listener : getKeyboardListeners())
			listener.computerSuppressEscapeKey(this);
	}

	protected void fireAutotypeStarted() {
		for (ComputerKeyboardListener listener : getKeyboardListeners())
			listener.computerAutotypeStarted(this);
	}

	protected void fireAutotypeEnded() {
		for (ComputerKeyboardListener listener : getKeyboardListeners())
			listener.computerAutotypeEnded(this);
	}

	protected void fireEnterBasicKeyboardPrompt(BasicKeyboardPromptModus modus) {
		for (ComputerKeyboardListener listener : getKeyboardListeners())
			listener.computerEnterBasicKeyboardPrompt(this, modus);
	}

	protected void fireExitBasicKeyboardPrompt(BasicKeyboardPromptModus modus) {
		for (ComputerKeyboardListener listener : getKeyboardListeners())
			listener.computerExitBasicKeyboardPrompt(this, modus);
	}

	public void addPerformanceListener(ComputerPerformanceListener listener) {
		getPerformanceListeners().add(listener);
	}

	public void removePerformanceListener(ComputerPerformanceListener listener) {
		getPerformanceListeners().remove(listener);
	}

	public List<ComputerPerformanceListener> getPerformanceListeners() {
		return performanceListeners;
	}

	protected void fireProcessorPerformanceUpdate(long timeIntervalMillis, int timerSyncs, int laggingSyncs,
			int throttledSyncs) {
		for (ComputerPerformanceListener listener : getPerformanceListeners())
			listener.processorPerformanceUpdate(this, timeIntervalMillis, timerSyncs, laggingSyncs, throttledSyncs);
	}

}