/**
 * Title:        JavaCPC by http://cpc-live.com
 * Description:  JAVA emulator for Amstrad Homecomputers
 * Copyright:    Copyright (c) 2002-2009
 * Company:
 * @author Devilmarkus
 * based on JEMU
 * author Richard
 * @version 6.5
 *
 * -Xms512m -Xmx768m -XX:MaxPermSize=256m
 *
 */

package jemu.ui;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import jemu.core.Util;
import jemu.core.device.Computer;
import jemu.core.device.ComputerKeyboardListener;
import jemu.core.device.FileDescriptor;
import jemu.core.device.floppy.DiscImage;
import jemu.core.device.floppy.Drive;
import jemu.core.device.floppy.DriveListener;
import jemu.core.device.floppy.UPD765A;
import jemu.core.device.floppy.virtualDrive;
import jemu.core.device.memory.MemoryWriteObserver;
import jemu.core.device.sound.YMControl;
import jemu.core.samples.Samples;
import jemu.settings.Settings;
import jemu.system.cpc.CPCPrinter;
import jemu.system.cpc.GateArray;
import jemu.system.cpc.RomSetter;
import jemu.ui.gfx.AnimatedGifEncoder;
import jemu.util.hexeditor.HexEditor;

public class JEMU extends Applet implements KeyListener, MouseListener, ItemListener, ActionListener, FocusListener,
		Runnable, DriveListener, MouseMotionListener {

	public static String version = "6.6";
	protected boolean useConsole = true;
	boolean beta = false;

	public int rendertimer = 0;

	private FrameAdapter frameAdapter;
	private List<PauseListener> pauseListeners;

	private boolean virtualShiftKey = false;
	private boolean virtualUnshiftKey = false;
	private boolean deferredShift = false;
	private boolean skipUnshift = false;
	private KeyEvent virtualShiftKeyEventPressed = new KeyEvent(this, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_SHIFT,
			KeyEvent.CHAR_UNDEFINED);
	private KeyEvent virtualShiftKeyEventReleased = new KeyEvent(this, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_SHIFT,
			KeyEvent.CHAR_UNDEFINED);

	private boolean controlKeysEnabled = true;
	private boolean mouseClickActionsEnabled = true;

	protected boolean isbackground;
	private static final Color BACKGROUND_COLOR = Color.BLACK;
	JInternalFrame intern = new JInternalFrame(null);
	int updateP = 0;
	public static JCheckBox doupdate = new JCheckBox("update");
	public static JCheckBox debugthis = new JCheckBox("update");
	protected static int screenXstored, screenYstored;
	protected int flasher;
	protected String captureName = "output.gif";
	protected boolean doRec, startR, doingRec, initRec;
	protected AnimatedGifEncoder encoder = new AnimatedGifEncoder();
	protected String titleA, keepA, titleB, keepB, titleC, keepC, titleD, keepD, titleE, keepE, titleF, keepF;

	protected JButton KeepA, KeepB, KeepC, KeepD, KeepE, KeepF;

	AdvancedOptions optionPanel = new AdvancedOptions();
	protected boolean showabout = false;
	RomSetter romsetter = new RomSetter();
	public static boolean setRoms = false;
	protected int CPUspeed;

	private File2CDT makeCDT = new File2CDT();
	private samp2cdt samp2cdt = new samp2cdt();
	public boolean useURL = false;
	protected static String Autoopen = "none";
	protected static int doAutoopen = 0;
	protected boolean KeyRec;
	boolean followCPU = false;
	Locale loc;
	String localkeys = "";
	boolean checkupdate;
	protected FileDialog dlg;
	public static boolean isTape = false;
	public static YMControl ymControl = new YMControl();
	int displaywidth, displayheight;
	protected boolean keepDimension = false;
	protected int colorcheck;
	JLabel toplabel, leftlabel, downlabel, rightlabel;

	String keys = "";

	final URL discimageA = getClass().getResource("image/discA.gif");
	final URL discimageB = getClass().getResource("image/discB.gif");
	final ImageIcon discA = new ImageIcon(discimageA);
	final ImageIcon discB = new ImageIcon(discimageB);

	private ArrayList<DropTarget> dropTargetList;

	public boolean tapeload = false;

	String update = "";
	HexEditor hexi;
	protected int QuitTimer = 0;
	public boolean runner = false;
	protected String loadtitle = "Load emulator file";
	Frame snaChooser;
	JButton s64 = new JButton("64k");
	JButton s128 = new JButton("128k");
	JButton s256 = new JButton("256k");
	JButton s512 = new JButton("512k");
	JButton cancel = new JButton("Cancel");
	JLabel Sn = new JLabel("Choose Snapshot-format:");

	JLabel Moniup = new JLabel(new ImageIcon(getClass().getResource("image/moniup.jpg")));
	JLabel Monidown = new JLabel(new ImageIcon(getClass().getResource("image/monidown.jpg")));
	JLabel Monileft = new JLabel(new ImageIcon(getClass().getResource("image/monileft.jpg")));
	JLabel Moniright = new JLabel(new ImageIcon(getClass().getResource("image/moniright.jpg")));

	JPanel Monitoruppanel = null;
	JPanel Monitordownpanel = null;
	JPanel Monitorleftpanel = null;
	JPanel Monitorrightpanel = null;

	JFrame screenpreview;
	JLabel sprev = new JLabel();
	BufferedImage images;
	int xpos;
	int ypos;
	int xoldpos;
	int yoldpos;
	protected boolean ctrl, shift, alt = false;

	public static int screenshottimer = 0;
	public static int screentimer = 0;
	public static int dsksavetimer = 0;
	public static int dskmaketimer = 0;
	protected boolean Vhold = false;
	protected String server;
	public String MachineMemory = "";

	public boolean audiooutput = true;
	public boolean floppyoutput = true;
	public boolean altjoystick = false;
	private int loadtimer = 0;
	public boolean mousejoy = false;

	protected int sizecounter = 0;

	private boolean stretcher = true;
	public int flopsound;
	private String autoloadprog;
	private String autoloadprogram = "~none~";
	private String autotypetext = "~none~";
	private int loadprogtimer = 0;
	private int simpletimer = 0;
	private int stopsimple = 0;
	private String DSKfile;
	private String autostartProg = null;
	private String autostartDisk = null;
	private String setComputers = null;
	private String discb = null;
	private String discc = null;
	private String discd = null;
	private String Monitor = null;
	private String compsys = null;
	private int pausetimer = 0;
	private int paused = 0;
	private int autoload = 0;
	public static int autoloader = 0;
	protected Computer computer = null;

	private static int winx1 = 532; // 916
	private static int winy1 = 402; // 672

	protected Display display = new Display();
	protected int dtimer = 1;
	protected JLabel jlComputer = new JLabel("System  ");
	protected JLabel jlDrive = new JLabel("Disc drive ");
	protected JLabel jlImage = new JLabel("Media");
	protected JLabel jmMerge = new JLabel("Merges 2 single-sided DSK to 1 double sided");
	protected JLabel jmSavec = new JLabel("Save canceled");
	protected String jmSavefile = "Select name for your dsk file (Ends with '.dsk')";

	/*
	 * protected JComboBox jcbComputer = new JComboBox(); protected JComboBox jcbDrive = new JComboBox(); protected
	 * JComboBox jcbImage = new JComboBox(); protected JComboBox driveHead = new JComboBox(new String[] { "Head 0",
	 * "Head 1" });
	 */
	protected GridBagConstraints gbcConstraints = null;
	protected boolean isStandalone = false;
	public static Debugger debugger = null;
	protected boolean started = false;
	public boolean large;
	protected boolean joystick;
	protected boolean executable = false;
	public boolean autosave;
	public boolean autoboot;
	public int autobooter;
	public boolean showmessage;
	protected Thread focusThread = null;
	protected boolean gotGames = false;
	private ResourceBundle msg_ui;
	protected String Language = null;
	protected String cname = null;
	protected String loadName = null;
	protected Color background;

	public static boolean onTop;
	protected boolean fsound;
	protected boolean notebook;
	protected boolean hideframe;
	protected boolean skinned;
	public String optionp;
	public static boolean togglesize = true;
	public static boolean fullscreen;

	boolean osd;

	JButton okay = new JButton(new ImageIcon(getClass().getResource("image/aboutok.gif")));
	JFrame about;
	JLabel JJemu = new JLabel(new ImageIcon(getClass().getResource("image/about.png")));
	JLabel JLogo = new JLabel(new ImageIcon(getClass().getResource("image/javacpc.gif")));

	// private ResourceBundle msg_dbg; // Not used yet
	public String getParameter(String key, String def) {
		return this.isStandalone ? System.getProperty(key, def) : (getParameter(key) != null ? getParameter(key) : def);
	}

	// The graphical part of JEMU

	private GridBagConstraints getGridBagConstraints(int x, int y, double weightx, double weighty, int width,
			int fill) {
		if (this.gbcConstraints == null) {
			this.gbcConstraints = new GridBagConstraints();
		}
		this.gbcConstraints.gridx = x;
		this.gbcConstraints.gridy = y;
		this.gbcConstraints.weightx = weightx;
		this.gbcConstraints.weighty = weighty;
		this.gbcConstraints.gridwidth = width;
		this.gbcConstraints.fill = fill;
		return this.gbcConstraints;
	}

	private JPanel getControlPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		return panel;
	}

	public JEMU(FrameAdapter frameAdapter) {
		this.frameAdapter = frameAdapter;
		this.pauseListeners = new Vector<PauseListener>();
		enableEvents(AWTEvent.KEY_EVENT_MASK);
	}

	public FrameAdapter getFrameAdapter() {
		return frameAdapter;
	}

	public void init() {
		pauseRec.setFocusable(false);
		startRec.setFocusable(false);
		stopRec.setFocusable(false);
		startRec.setBackground(Color.DARK_GRAY);
		pauseRec.setBackground(Color.DARK_GRAY);
		stopRec.setBackground(Color.DARK_GRAY);
		startRec.setForeground(Color.LIGHT_GRAY);
		pauseRec.setForeground(Color.LIGHT_GRAY);
		stopRec.setForeground(Color.LIGHT_GRAY);
		startRec.setBorder(new BevelBorder(BevelBorder.RAISED));
		pauseRec.setBorder(new BevelBorder(BevelBorder.RAISED));
		stopRec.setBorder(new BevelBorder(BevelBorder.RAISED));
		loc = getLocale();
		localkeys = loc.toString().toUpperCase();
		if (localkeys.equals("DE_DE"))
			keys = "German keyboard  ";
		else if (localkeys.equals("ES_ES"))
			keys = "Spanish keyboard  ";
		else
			keys = "English keyboard  ";
		Keys.setLabel(keys);

		Switches.executable = false;

		try {
			server = getCodeBase().toString();
		} catch (final Exception e) {
			Switches.executable = true;
			executable = true;
		}

		keepDimension = Settings.getBoolean(Settings.DIMENSION, true);
		keepprop.setState(keepDimension);
		KeyRec = Settings.getBoolean(Settings.KEYREC, false);
		RecKey.setState(KeyRec);

		keepA = getParameter("CHANGEA", null);
		titleA = getParameter("TITLEA", null);
		keepB = getParameter("CHANGEB", null);
		titleB = getParameter("TITLEB", null);
		keepC = getParameter("CHANGEC", null);
		titleC = getParameter("TITLEC", null);
		keepD = getParameter("CHANGED", null);
		titleD = getParameter("TITLED", null);
		keepE = getParameter("CHANGEE", null);
		titleE = getParameter("TITLEE", null);
		keepF = getParameter("CHANGEF", null);
		titleF = getParameter("TITLEF", null);

		KeepA = new JButton(titleA);
		KeepB = new JButton(titleB);
		KeepC = new JButton(titleC);
		KeepD = new JButton(titleD);
		KeepE = new JButton(titleE);
		KeepF = new JButton(titleF);

		if (keepA != null)
			selectDialog();

		String frequency = getParameter("SAMPLERATE", "notset").toLowerCase();
		Switches.khz44 = Settings.getBoolean(Settings.KHZ44, Switches.khz44);
		Switches.khz11 = Settings.getBoolean(Settings.KHZ11, Switches.khz11);
		if (frequency.startsWith("44")) {
			Switches.khz44 = true;
			Switches.khz11 = false;
		}
		if (frequency.startsWith("22")) {
			Switches.khz44 = false;
			Switches.khz11 = false;
		}
		if (frequency.startsWith("11")) {
			Switches.khz44 = false;
			Switches.khz11 = true;
		}
		if (Switches.khz44)
			recrateA.setState(true);
		if (Switches.khz11)
			recrateC.setState(true);
		if (!Switches.khz44 && !Switches.khz11)
			recrateB.setState(true);

		Switches.doIntack = Settings.getBoolean(Settings.INTACK, true);
		intack.setState(Switches.doIntack);

		boolean usegzip = Settings.getBoolean(Settings.GZIP, false);
		UseGzip.setState(usegzip);
		if (usegzip)
			Switches.uncompressed = false;
		else
			Switches.uncompressed = true;

		boolean firstrun = Settings.getBoolean(Settings.FIRSTRUN, true);
		if (firstrun) {
			Object[] object = {
					"This is the first time you run JavaCPC.\n" + "JavaCPC wants to optimize performance now."
							+ "\nthis can be changed later in Settings.\n" + "\nPlease choose PC-performance:"
							+ "\nYes - Your CPU has about 3 ghz or more" + "\nNo  - Your CPU is slower than 3 ghz" };

			int selectedValue = JOptionPane.showOptionDialog(this, object, "Please choose:", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (selectedValue == JOptionPane.NO_OPTION) {
				Display.lowperformance = true;
				display.changePerformance();
				Settings.setBoolean(Settings.LOWPERFORMANCE, true);
				lowsys.setState(true);
			}
			if (selectedValue == JOptionPane.YES_OPTION) {
				Display.lowperformance = false;
				display.changePerformance();
				Settings.setBoolean(Settings.LOWPERFORMANCE, false);
				lowsys.setState(false);
			}
			Settings.setBoolean(Settings.FIRSTRUN, false);
		}
		checkupdate = Settings.getBoolean(Settings.AUTOCHECK, false);
		autocheck.setState(checkupdate);
		Switches.doublesize = Settings.getBoolean(Settings.DOUBLE, false);
		Switches.triplesize = Settings.getBoolean(Settings.TRIPLE, false);
		Display.lowperformance = Settings.getBoolean(Settings.LOWPERFORMANCE, true);
		lowsys.setState(Display.lowperformance);
		display.changePerformance();
		if (!executable) {
			Switches.doublesize = Util.getBoolean(getParameter("DOUBLE", "false"));
			Switches.triplesize = Util.getBoolean(getParameter("TRIPLE", "false"));
		}
		checkDouble.setState(Switches.doublesize);
		checkTriple.setState(Switches.triplesize);
		checkFull.setState(fullscreen);
		showmenu = Settings.getBoolean(Settings.SHOWMENU, showmenu);
		if (showmenu)
			getFrameAdapter().setMenuBar(JavaCPCMenu);

		showmessage = Settings.getBoolean(Settings.MESSAGE, true);
		if (showmessage) {
			message();
		}
		autorun();
		simpleBoot();
		initFrame();
		requestFocus();
		if (executable) {
			screenXstored = Integer.parseInt(Settings.get(Settings.FRAMEX, "0"));
			screenYstored = Integer.parseInt(Settings.get(Settings.FRAMEY, "0"));
			getFrameAdapter().setLocation(screenXstored, screenYstored);
		}
	}

	private void initFrame() {
		// On top
		onTop = Settings.getBoolean(Settings.ONTOP, false);
		onTop = Util.getBoolean(getParameter("ONTOP", Boolean.toString(onTop)));
		aontop.setState(onTop);
		applyAlwaysOnTop(onTop);
		// Stretch
		Switches.stretch = false;
		// Screen size
		winx1 = getFrameAdapter().getSize().width;
		winy1 = getFrameAdapter().getSize().height;
		// System.out.println("Window is " + winx1 + " pixels wide & " + winy1 + " pixels high");
		// Fullscreen
		fullscreen = Settings.getBoolean(Settings.FULLSCREEN, false);
		fullscreen = Util.getBoolean(getParameter("FULLSCREEN", Boolean.toString(fullscreen)));
		checkFull.setState(fullscreen);
		if (fullscreen) {
			togglesize = false;
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			getFrameAdapter().dispose();
			getFrameAdapter().setUndecorated(true);
			getFrameAdapter().setSize(d.width, d.height);
			getFrameAdapter().setLocation((d.width - getFrameAdapter().getSize().width) / 2,
					(d.height - getFrameAdapter().getSize().height) / 2);
		}
	}

	public void autorun() {
		autoloadprog = Settings.get(Settings.AUTOLOAD, null);
		autoloadprog = getParameter("AUTOLOAD", autoloadprog);
		autoloadprogram = autoloadprog;
		stopsimple = 0;
		if (autoloadprog != null) {
			if (autoloadprog.equals("~none~")) {
			} else if (autoloadprog.equals("launchtape")) {
				runner = true;
				stopsimple = 1;
				// loadprogtimer = 1;
				jemu.system.cpc.CPC.tapestarttimer = 1;
			} else {
				runner = true;
				stopsimple = 1;
				if (autoloadprog.startsWith("|")) {
					loadprogtimer = 1;
					Switches.loadauto = autoloadprog + "\n";
					Settings.set(Settings.AUTOLOAD, "~none~");
				} else {
					loadprogtimer = 1;
					Switches.loadauto = "RUN\"" + autoloadprog + "\"\n";
					Settings.set(Settings.AUTOLOAD, "~none~");
				}
			}
		}
		autoloadprog = Settings.get(Settings.AUTOTYPE, null);
		autoloadprog = getParameter("AUTOTYPE", autoloadprog);
		autotypetext = autoloadprog;
		stopsimple = 0;
		if (autoloadprog != null) {
			if (autoloadprog.equals("~none~")) {
			} else {
				runner = false;
				stopsimple = 1;
				loadprogtimer = 1;
				Switches.loadauto = autoloadprog + "\n";
				Settings.set(Settings.AUTOTYPE, "~none~");
			}
		}
	}

	public void reBoot() {
		computer.reset();
		setPaused(0);
		Display.showpause = 0;
		computer.start();
		firePauseStateChanged();
		stopsimple = 0;
		if (runner) {
			if (autoloadprogram != null) {
				if (autoloadprogram.equals("~none~")) {
				} else if (autoloadprogram.equals("launchtape")) {
					runner = true;
					stopsimple = 1;
					// loadprogtimer = 1;
					jemu.system.cpc.CPC.tapestarttimer = 1;
					jemu.system.cpc.CPC.number = 0;
				} else {
					stopsimple = 1;
					if (autoloadprogram.startsWith("|")) {
						loadprogtimer = 1;
						Switches.loadauto = autoloadprogram + "\n";
					} else {
						loadprogtimer = 1;
						Switches.loadauto = "RUN" + '"' + autoloadprogram + '"' + "\n";
					}
				}
			}
		} else if (autotypetext != null) {
			if (autotypetext.equals("~none~")) {
			} else {
				stopsimple = 1;
				loadprogtimer = 1;
				Switches.loadauto = autotypetext + "\n";
			}
		}
	}

	public void simpleBoot() {
		if (stopsimple != 1) {
			// String simpleboot = Settings.get(Settings.SIMPLEBOOT, null);
			DSKfile = getParameter("SIMPLEBOOT", null);

			if (DSKfile != null) {
				Settings.set(Settings.AUTOLOAD, DSKfile);
				simpletimer = 1;
				autorun();
			}
		}
	}

	public void loaddata() {
		Component framer = this;
		applyAlwaysOnTop(false);
		while (!(framer instanceof Frame)) {
			framer = framer.getParent();
		}

		dlg = new FileDialog((Frame) framer, loadtitle, FileDialog.LOAD);
		dlg.setFile("*.sna; *.dsk; *.zip; *.bin; *.bas; *.ym; *.wav; *.cdt; *.tzx; *.taz; *.snz; *.snk; *.snz");

		dlg.setVisible(true);
		if (dlg.getFile() != null) {
			loadFile(Computer.TYPE_SNAPSHOT, dlg.getDirectory() + dlg.getFile(), false);
		}

		applyAlwaysOnTop(onTop);
		this.display.requestFocus();
	}

	public void loadTape(boolean launch) {
		jemu.system.cpc.CPC.tapeloaded = false;
		Component framer = this;
		applyAlwaysOnTop(false);
		while (!(framer instanceof Frame)) {
			framer = framer.getParent();
		}
		if (launch)
			loadtitle = "Load tape file";
		else
			loadtitle = "Launch tape file";
		dlg = new FileDialog((Frame) framer, loadtitle, FileDialog.LOAD);
		dlg.setFile("*.wav; *.cdt; *.tzx; *.zip");

		dlg.setVisible(true);
		if (dlg.getFile() != null) {
			loadFile(Computer.TYPE_SNAPSHOT, dlg.getDirectory() + dlg.getFile(), false);
		}
		loadtitle = "Load emulator file";

		applyAlwaysOnTop(onTop);
		this.display.requestFocus();
		if (launch) {
			computer.reset();
			jemu.system.cpc.CPC.tapestarttimer = 1;
		}
	}

	public void reportBug() {
		applyAlwaysOnTop(false);
		String checkthis = "" + Util.hex((short) Util.random(0xffff));
		JTextField UserName = new JTextField();
		JTextField Email = new JTextField();
		JTextArea content = new JTextArea();
		JTextField checkcode = new JTextField();
		JTextField entercode = new JTextField();
		checkcode.setText(checkthis);
		checkcode.setEditable(false);
		checkcode.setEnabled(false);
		JScrollPane content2 = new JScrollPane(content);
		content.setColumns(40);
		content.setRows(25);
		content.setText("");
		Font font = new Font("Courier", 1, 10);
		InputStream in = getClass().getResourceAsStream("security.ttf");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.BOLD, 28);
		} catch (Exception er) {
		}
		checkcode.setFont(font);
		entercode.setFont(font);
		Object[] message = { "Security code", checkcode, "Your name:", UserName, "Your email:", Email, "Your message",
				content2, "Enter security code", entercode };

		JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);

		pane.createDialog(null, "Report a bug").setVisible(true);

		if (content.getText().length() >= 10) {
			if (entercode.getText().toUpperCase().equals(checkthis)) {
				if (Email.getText().endsWith(".com") || Email.getText().endsWith(".de")
						|| Email.getText().endsWith(".fr") || Email.getText().endsWith(".gr")
						|| Email.getText().endsWith(".dk") || Email.getText().endsWith(".es")
						|| Email.getText().endsWith(".uk") || Email.getText().endsWith(".fn")
						|| Email.getText().endsWith(".ro") || Email.getText().endsWith(".ru")
						|| Email.getText().endsWith(".it") || Email.getText().endsWith(".tk")) {
					username = UserName.getText();
					usermail = Email.getText();
					usertext = content.getText();

					if (Email.getText().length() > 4 && UserName.getText().length() > 3) {
						BugReport.reportBug();
					}
				} else
					JOptionPane.showMessageDialog(null, "Email address not valid.\nPlease try again...");
			} else
				JOptionPane.showMessageDialog(null, "Security code not valid.\nPlease try again...");
		}
		applyAlwaysOnTop(onTop);
	}

	public static String username, usermail, usertext;

	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}

	@Override
	public void start() {

		debugthis.addItemListener(this);
		useURL = false;
		useURL = Util.getBoolean(getParameter("URL", "false"));
		Calendar cal = Calendar.getInstance();
		System.gc();
		okay.addActionListener(this);
		romsetter.prepareRomsetter();

		skinned = Util.getBoolean(getParameter("SKINNED", "false"));
		if (skinned)
			addSkin();

		new Autotype();
		new CPCPrinter();
		if (useConsole)
			Console.init();
		System.out.println("JavaCPC [v." + version + "]\n\n[" + cal.getTime() + "]\n");
		System.out.println("executable is " + executable);
		Runtime r = Runtime.getRuntime();
		long free = r.totalMemory() - r.freeMemory();
		Switches.availmem = free * 10;
		if (!executable)
			Switches.availmem = 34000000;

		System.out.println("Avail mem is " + Switches.availmem);

		if (checkupdate)
			try {
				UpdateCheck();
			} catch (Exception et) {
				et.printStackTrace();
			}

		try {

			System.out.println("init()");
			removeAll();
			this.background = getBackground();
			setBackground(Color.BLACK);
			setLayout(new BorderLayout());
			final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			int height1 = (int) (dim.width / 1.41);
			int realheight = dim.height;
			int labelheight = (realheight - height1) / 2;
			int width1 = (int) (dim.height * 1.41);
			int realwidth = dim.width;
			int labelwidth = (realwidth - width1) / 2;
			if (labelheight <= 0)
				labelheight = 0;
			if (labelwidth <= 0)
				labelwidth = 0;
			int[] pixelsc = new int[1 * labelheight];
			Image topdistance = Toolkit.getDefaultToolkit()
					.createImage(new MemoryImageSource(1, labelheight, pixelsc, 0, labelheight));
			int[] pixelsf = new int[labelwidth * 1];
			Image leftdistance = Toolkit.getDefaultToolkit()
					.createImage(new MemoryImageSource(labelwidth, 1, pixelsf, 0, 1));
			toplabel = new JLabel(new ImageIcon(topdistance));
			downlabel = new JLabel(new ImageIcon(topdistance));
			leftlabel = new JLabel(new ImageIcon(leftdistance));
			rightlabel = new JLabel(new ImageIcon(leftdistance));
			intern.add(toplabel, BorderLayout.NORTH);
			intern.add(downlabel, BorderLayout.SOUTH);
			intern.add(leftlabel, BorderLayout.WEST);
			intern.add(rightlabel, BorderLayout.EAST);
			intern.setTitle("Output");
			intern.setBackground(BACKGROUND_COLOR);
			intern.setVisible(true);
			intern.setBorder(null);
			intern.setResizable(false);
			intern.setMaximizable(false);
			intern.setSelected(true);
			((javax.swing.plaf.basic.BasicInternalFrameUI) intern.getUI()).setNorthPane(null);
			intern.setOpaque(true);
			intern.setDoubleBuffered(true);
			intern.add(display, BorderLayout.CENTER);
			intern.pack();
			this.add(intern, BorderLayout.CENTER);
			display.setBorder(null);
			display.setBackground(Color.BLACK);
			display.addKeyListener(this);
			display.addMouseListener(this);
			display.addFocusListener(this);
			display.addMouseMotionListener(this);
			// display.setDoubleBuffered(true);
			intern.addKeyListener(this);
			intern.addMouseListener(this);
			intern.addFocusListener(this);
			intern.addMouseMotionListener(this);

			dropTargetList = new ArrayList<DropTarget>();
			DropListener myListener = new DropListener();
			registerDropListener(dropTargetList, this, myListener);

			// Parameters

			overrideP.setState(Settings.getBoolean(Settings.OVERRIDEP, false));
			Switches.overrideP = overrideP.getState();

			showDrive.setState(Settings.getBoolean(Settings.SHOWDRIVE, false));
			// display.showDrive = showDrive.getState();
			if (showDrive.getState())
				virtualDrive.Show();
			else
				virtualDrive.Hide();

			changePolarity.setState(Settings.getBoolean(Settings.POLARITY, false));
			Switches.changePolarity = changePolarity.getState();

			mousejoy = Util.getBoolean(getParameter("MOUSEJOY", "false"));
			if (mousejoy == false)
				mousejoy = Settings.getBoolean(Settings.MOUSEJOY, mousejoy);
			if (mousejoy) {
				Switches.MouseJoy = true;
				joymouse.setState(true);
			} else {
				Switches.MouseJoy = false;
				joymouse.setState(false);
			}

			Switches.CRTC = Integer.parseInt(Settings.get(Settings.CRTC, "0"));
			Switches.CRTC = Integer.parseInt(getParameter("CRTC", "" + Switches.CRTC));

			if (Switches.CRTC == 1)
				crtc1.setState(true);
			else
				crtc0.setState(true);

			Switches.unprotect = Util.getBoolean(getParameter("UNPROTECT", "false"));
			Switches.Printer = Settings.getBoolean(Settings.PRINTER, false);
			Switches.Printer = Util.getBoolean(getParameter("PRINTER", Boolean.toString(Switches.Printer)));
			Switches.Expansion = Settings.getBoolean(Settings.EXPANSION, false);
			Switches.Expansion = Util.getBoolean(getParameter("EXPANSION", Boolean.toString(Switches.Expansion)));

			Switches.digiblaster = Settings.getBoolean(Settings.DIGIBLASTER, false);
			Switches.digiblaster = Util.getBoolean(getParameter("DIGIBLASTER", Boolean.toString(Switches.digiblaster)));
			Switches.floppyturbo = Settings.getBoolean(Settings.FLOPPYTURBO, false);
			Switches.floppyturbo = Util.getBoolean(getParameter("FLOPPYTURBO", Boolean.toString(Switches.floppyturbo)));
			floppyturbo.setState(Switches.floppyturbo);

			unprotect.setState(Switches.unprotect);
			Printer.setState(Switches.Printer);
			Expansion.setState(Switches.Expansion);
			Digiblaster.setState(Switches.digiblaster);

			Vhold = Util.getBoolean(getParameter("VHOLD", "false"));
			if (Vhold) {
				Switches.vhold = 10;
			}

			large = Settings.getBoolean(Settings.LARGE, true);
			large = Util.getBoolean(getParameter("LARGE", Boolean.toString(large)));
			checkGate.setState(large);

			if (!Switches.doublesize && !fullscreen & !Switches.triplesize && !large)
				checkSimple.setState(true);

			hideframe = Settings.getBoolean(Settings.HIDEFRAME, false);
			hideframe = Util.getBoolean(getParameter("HIDEFRAME", Boolean.toString(hideframe)));

			this.fsound = Settings.getBoolean(Settings.FLOPPYSOUND, true);
			this.fsound = Util.getBoolean(getParameter("FLOPPYSOUND", Boolean.toString(this.fsound)));
			drivesound.setState(fsound);
			this.notebook = Settings.getBoolean(Settings.NOTEBOOK, false);
			this.notebook = Util.getBoolean(getParameter("NOTEBOOK", Boolean.toString(this.notebook)));
			qaop.setState(notebook);

			final boolean alternativejoy = Util.getBoolean(getParameter("ALTJOY", "false"));

			final boolean debug = Util.getBoolean(getParameter("DEBUG", "false"));

			final boolean pause = Util.getBoolean(getParameter("PAUSE", "false"));

			this.joystick = Settings.getBoolean(Settings.JOYSTICK, true);
			this.joystick = Util.getBoolean(getParameter("JOYSTICK", Boolean.toString(joystick)));
			joyemu.setState(joystick);
			qaop.setEnabled(joystick);

			boolean audioon = Settings.getBoolean(Settings.AUDIO, true);
			audioon = Util.getBoolean(getParameter("AUDIO", Boolean.toString(audioon)));
			autosave = Settings.getBoolean(Settings.AUTOSAVE, false);
			autosave = Util.getBoolean(getParameter("AUTOSAVE", Boolean.toString(autosave)));
			Switches.autosave = autosave;
			autosavedsk.setState(autosave);
			Switches.checksave = Settings.getBoolean(Settings.CHECKSAVE, true);
			Switches.checksave = Util.getBoolean(getParameter("CHECKSAVE", Boolean.toString(Switches.checksave)));

			checksave.setState(Switches.checksave);
			Switches.neverOverwrite = Settings.getBoolean(Settings.CHECKRENAME, false);

			checkrename.setState(Switches.neverOverwrite);

			Switches.ScanLines = Settings.getBoolean(Settings.SCANLINES, false);
			Switches.ScanLines = Util.getBoolean(getParameter("SCANLINES", Boolean.toString(Switches.ScanLines)));
			checkScan.setState(Switches.ScanLines);
			Display.scaneffect = Settings.getBoolean(Settings.SCANEFFECT, false);
			Display.scaneffect = Util.getBoolean(getParameter("SCANEFFECT", Boolean.toString(Display.scaneffect)));
			checkScaneff.setState(Display.scaneffect);
			if (Display.scaneffect)
				isbackground = false;
			else
				isbackground = true;
			Switches.bilinear = Settings.getBoolean(Settings.BILINEAR, false);
			Switches.bilinear = Util.getBoolean(getParameter("BILINEAR", Boolean.toString(Switches.bilinear)));
			checkbilinear.setState(Switches.bilinear);

			if (executable) {
				/*
				 * selector = false; buttons=false; border=false;
				 */
				getFrameAdapter().setMenuBar(JavaCPCMenu);
				showmenu = true;
			}
			Switches.Memory = Settings.get(Settings.MEMORY, "TYPE_512K");
			Switches.computername = Util.hexValue(Settings.get(Settings.COMPUTERNAME, "7"));
			switch (Switches.computername) {
			case 0:
				cname0.setState(true);
				break;
			case 1:
				cname1.setState(true);
				break;
			case 2:
				cname2.setState(true);
				break;
			case 3:
				cname3.setState(true);
				break;
			case 4:
				cname4.setState(true);
				break;
			case 5:
				cname5.setState(true);
				break;
			case 6:
				cname6.setState(true);
				break;
			case 7:
				cname7.setState(true);
				break;
			}
			this.MachineMemory = getParameter("MEMORY", MachineMemory);
			if (MachineMemory.equals("512"))
				Switches.Memory = "TYPE_512K";
			if (MachineMemory.equals("256"))
				Switches.Memory = "TYPE_256K";
			if (MachineMemory.equals("128"))
				Switches.Memory = "TYPE_128K";
			if (MachineMemory.equals("64"))
				Switches.Memory = "TYPE_64K";
			if (MachineMemory.equals("SILICON"))
				Switches.Memory = "TYPE_SILICON_DISC";
			if (MachineMemory.equals("SILICON128"))
				Switches.Memory = "TYPE_128_SILICON_DISC";

			if (Switches.Memory.equals("TYPE_64K")) {
				memory1.setState(true);
			}
			if (Switches.Memory.equals("TYPE_128K")) {
				memory2.setState(true);
			}
			if (Switches.Memory.equals("TYPE_256K")) {
				memory3.setState(true);
			}
			if (Switches.Memory.equals("TYPE_512K")) {
				memory4.setState(true);
			}
			if (Switches.Memory.equals("TYPE_SILICON_DISC")) {
				memory5.setState(true);
			}
			if (Switches.Memory.equals("TYPE_128_SILICON_DISC")) {
				memory6.setState(true);
			}

			showonlinemenu = Util.getBoolean(getParameter("MENU", "false"));
			autoboot = Util.getBoolean(getParameter("AUTOBOOT", "false"));
			osd = Settings.getBoolean(Settings.OSD, false);
			osd = Util.getBoolean(getParameter("OSD", Boolean.toString(osd)));
			osdisplay.setState(osd);
			discb = getParameter("DISCB", null);
			discb = getParameter("DISKB", discb);
			discc = getParameter("DISCC", null);
			discc = getParameter("DISKC", discc);
			discd = getParameter("DISCD", null);
			discd = getParameter("DISKD", discd);
			autostartProg = getParameter("FILE", null);
			autostartDisk = getParameter("DISC", null);
			autostartDisk = getParameter("DISK", autostartDisk);

			Monitor = Settings.get(Settings.MONITOR, "COLOR");
			Monitor = getParameter("MONITOR", Monitor);

			if (computer == null) {

				final String ComputerSystem = Settings.get(Settings.SYSTEM, "CPC6128");
				if (ComputerSystem.equals("CPC464T"))
					cpc464t.setState(true);
				if (ComputerSystem.equals("CPC464"))
					cpc464.setState(true);
				if (ComputerSystem.equals("CPC664"))
					cpc664.setState(true);
				if (ComputerSystem.equals("CPC6128"))
					cpc6128.setState(true);
				if (ComputerSystem.equals("SymbOS"))
					symbos.setState(true);
				if (ComputerSystem.equals("FutureOS"))
					futureos.setState(true);
				if (ComputerSystem.equals("KCcomp"))
					kccompact.setState(true);
				if (ComputerSystem.equals("CUSTOM"))
					customcpc.setState(true);
				setComputers = getParameter("COMPUTER", null);

				if (osd) {
					Switches.osddisplay = true;
				} else {
					Switches.osddisplay = false;
				}

				if (setComputers != null) {
					setComputer(setComputers, !(debug || pause));
				} else {
					setComputer((ComputerSystem), !(debug || pause));
				}
			} else if (!(debug || pause)) {
				computer.start();
			}

			System.out.println("DEBUG=" + debug + ", PAUSE=" + pause + ", LARGE=" + large);

			System.out.println("System Set: " + computer);
			Display.model = "System: " + computer.getName();
			Display.showmodel = 100;

			if (autostartDisk != null) {
				if (autostartProg != null) {
				} else {
					pauseComputer();
					autoload = 4;
					loadFile(autostartDisk);
					goComputer();
				}
			}

			if (autostartProg != null) {
				if (autostartDisk != null) {
					autoload = 4;
					Display.loadgames = 100;
				} else {
					autoload = 4;
					pauseComputer();
					loadFile(autostartProg);
					goComputer();
				}
			}

			if (discb != null) {
				if (autostartProg == null) {
					computer.setCurrentDrive(1);
					loadFile(discb);
					computer.setCurrentDrive(0);
				}
			}
			if (discc != null) {
				if (autostartProg == null) {
					computer.setCurrentDrive(2);
					loadFile(discc);
					computer.setCurrentDrive(0);
				}
			}

			if (discd != null) {
				if (autostartProg == null) {
					computer.setCurrentDrive(3);
					loadFile(discd);
					computer.setCurrentDrive(0);
				}
			}

			if (Monitor != null) {
				if (Monitor.equals("COLOR2")) {
					Switches.monitormode = 1;
					checkJColor.setState(true);
					Display.monmessage = "2nd Colorset";
				}
				if (Monitor.equals("JEMU")) {
					Switches.monitormode = 1;
					checkJColor.setState(true);
					Display.monmessage = "2nd Colorset";
				}
				if (Monitor.equals("COLOUR2")) {
					Switches.monitormode = 1;
					checkJColor.setState(true);
					Display.monmessage = "2nd Colorset";
				}
				if (Monitor.equals("COLOUR")) {
					Switches.monitormode = 0;
					checkColor.setState(true);
					Display.monmessage = "Colour monitor";
				}
				if (Monitor.equals("COLOR")) {
					Switches.monitormode = 0;
					checkColor.setState(true);
					Display.monmessage = "Colour monitor";
				}
				if (Monitor.equals("GREEN")) {
					Switches.monitormode = 2;
					checkGreen.setState(true);
					Display.monmessage = "Green monitor";
				}
				{
					if (Monitor.equals("GREY")) {
						Switches.monitormode = 3;
						checkGrey.setState(true);
						Display.monmessage = "Grey monitor";
					}
				}
				{
					if (Monitor.equals("GRAY")) {
						Switches.monitormode = 3;
						checkGrey.setState(true);
						Display.monmessage = "Grey monitor";
					}

				}
			}

			if (audioon) {
				Switches.audioenabler = 1;
				checkAudio.setState(true);
				System.out.println("Audio Enabled");
			} else {
				Switches.audioenabler = 0;
				checkAudio.setState(false);
				System.out.println("Audio Disabled");
			}

			fsoundInit();

			if (notebook) {
				Switches.notebook = true;
				System.out.println("Notebook Enabled");
			} else {
				Switches.notebook = false;
				System.out.println("Notebook Disabled");
			}

			if (alternativejoy)
				Switches.notebook = true;

			if (autosave) {
				Switches.autosave = true;
				System.out.println("Autosave Enabled");
			} else {
				Switches.autosave = false;
				System.out.println("Autosave Disabled");
			}

			if (autoboot)
				autobooter = 1;
			else
				autobooter = 0;

			if (joystick) {
				Switches.joystick = 1;
			} else {
				Switches.joystick = 0;
			}

			if (Monitoruppanel != null)
				add(Monitoruppanel, BorderLayout.NORTH);
			if (Monitordownpanel != null)
				add(Monitordownpanel, BorderLayout.SOUTH);
			if (Monitorleftpanel != null)
				add(Monitorleftpanel, BorderLayout.WEST);
			if (Monitorrightpanel != null)
				add(Monitorrightpanel, BorderLayout.EAST);

			if (debug) {
				showDebugger();
			}
			this.started = true;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		Display.automessage = "JavaCPC started...";
		Display.showauto = 150;

		SetMenu();
		if (showonlinemenu)
			OnlineMenu();

		if (!executable) {
			checkFull.setEnabled(false);
			checkTriple.setEnabled(false);
			checkDouble.setEnabled(false);
			checkSimple.setEnabled(false);
			checkGate.setEnabled(false);
			aontop.setEnabled(false);
		}
		checkDouble.setState(Switches.doublesize);
		checkTriple.setState(Switches.triplesize);

		// parameters for internal programs to launch

		Switches.digi = Util.getBoolean(getParameter("DIGITRACKER", "false"));
		Switches.digimc = Util.getBoolean(getParameter("DIGITRACKERMC", "false"));
		Switches.digipg = Util.getBoolean(getParameter("DIGITRACKERPG", "false"));
		Switches.devil = Util.getBoolean(getParameter("CHEAT", "false"));

		//

		optionPanel.init();
		if (Util.getBoolean(getParameter("OPTIONS", "false")) == true)
			optionPanel.OptionPanel();

		String tapeload = getParameter("TAPE", null);
		tapeload = getParameter("TAPE", tapeload);
		if (tapeload != null) {
			jemu.system.cpc.CPC.loadtape = tapeload;
			jemu.system.cpc.CPC.inserttape = true;
		}

		displayheight = display.getHeight();
		displaywidth = display.getWidth();
		System.out.println("Display is " + displaywidth + "," + displayheight + " pixels");
		checkDisplay();
		makeshot.addActionListener(this);
		cancelshot.addActionListener(this);
		startRec.addActionListener(this);
		stopRec.addActionListener(this);
		pauseRec.addActionListener(this);
		jemu.system.cpc.CPC.saveOnExit = 0;
		computer.reSync();

		String autostartKeys = getParameter("KEYS", null);
		if (autostartKeys != null) {
			loadFile(autostartKeys);
		}
		/*
		 * JFrame output = new JFrame(); output.addKeyListener(this); output.addMouseListener(this);
		 * output.addMouseMotionListener(this); output.add(display); output.setMenuBar(JavaCPCMenu); output.pack();
		 * output.setVisible(true);
		 */
		try {
			intern.setMaximum(true);
		} catch (Exception m) {
		}
		doupdate.addItemListener(this);

	}

	public boolean isStarted() {
		return started;
	}

	public void waitStart() {
		try {
			while (!isStarted())
				Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void focusDisplay() {
		display.requestFocus();
	}

	public void run() {
	}

	public void startComputer() {
		Display.showpause = 0;
		computer.start();
		computer.reSync();
	}

	public void addComputerKeyboardListener(ComputerKeyboardListener listener) {
		computer.addKeyboardListener(listener);
	}

	public void removeComputerKeyboardListener(ComputerKeyboardListener listener) {
		computer.removeKeyboardListener(listener);
	}

	public void addMemoryWriteObserver(MemoryWriteObserver observer) {
		computer.addMemoryWriteObserver(observer);
	}

	public void removeMemoryWriteObserver(MemoryWriteObserver observer) {
		computer.removeMemoryWriteObserver(observer);
	}

	public void removeAllMemoryWriteObservers() {
		computer.removeAllMemoryWriteObservers();
	}

	public List<MemoryWriteObserver> getMemoryWriteObservers() {
		return computer.getMemoryWriteObservers();
	}

	public void quit() {
		if (debugger != null) {
			debugger.removeAllBreakpoints();
			debugger.continueAndReset();
		}
		Switches.breakpoints = false;
		Autotype.save();
		jemu.system.cpc.CPC.checkSaveOnExit();
		computer.dispose();
	}

	public void osdCheck() {
		if (osd) {
			osd = false;
			Settings.setBoolean(Settings.OSD, false);
			Switches.osddisplay = false;
		} else {
			osd = true;
			Settings.setBoolean(Settings.OSD, true);
			Switches.osddisplay = true;
		}
	}

	public String getAppletInfo() {
		return this.getAppletInfo();
	}

	public String[][] getParameterInfo() {
		return this.getParameterInfo();
	}

	public void doAutoOpen(File file) {
		if (doAutoopen == 0) {
			Autoopen = file.getAbsolutePath();
			System.out.println("Autoopen this file: " + Autoopen);
			doAutoopen = 1;
		}
	}

	protected void checkDisplay() {
		if (fullscreen) {
			toplabel.setVisible(true);
			downlabel.setVisible(true);
			leftlabel.setVisible(true);
			rightlabel.setVisible(true);
		} else {
			toplabel.setVisible(false);
			downlabel.setVisible(false);
			leftlabel.setVisible(false);
			rightlabel.setVisible(false);
		}
		displayheight = display.getHeight();
		displaywidth = display.getWidth();
		double divider = 1.4117647058823529411764705882353;
		if (displayheight * divider >= displaywidth)
			display.setSize(displaywidth, (int) (displaywidth / divider));
		else
			display.setSize((int) (displayheight * divider) + 1, (displayheight));

		displaywidth = display.getWidth();
		displayheight = display.getHeight();
		System.out.println("Display is " + displaywidth + "," + displayheight + " pixels");
		if (!fullscreen && executable)
			getFrameAdapter().pack();
	}

	public void updateRoms() {
		GateArray.doRender = false;
		rendertimer = 1;

		try {
			setComputer("CUSTOM");
		} catch (final Exception ex) {
		}
		// if (executable)
		// defaultSize();
		LoadFiles();
	}

	int reloader;
	boolean updated = true;

	/*
	 * @Override public void update(Graphics g) { if (g != null) paint(g); update(); }
	 */

	protected boolean useBorderColor = false;

	public void update() {
		if (isbackground != Display.scaneffect || useBorderColor) {
			if (Display.scaneffect)
				intern.setBackground(BACKGROUND_COLOR);
			else {
				if (useBorderColor)
					intern.setBackground(new Color(display.getColor()));
				else
					intern.setBackground(new Color(0x000000));
			}
			isbackground = Display.scaneffect;
		}
		if (rendertimer > 0) {
			rendertimer++;
			if (rendertimer > 30) {
				rendertimer = 0;
				GateArray.doRender = true;
			}
		}
		if (executable && !fullscreen) {
			if (getFrameAdapter().getX() != screenXstored || getFrameAdapter().getY() != screenYstored) {
				screenXstored = getFrameAdapter().getX();
				Settings.set(Settings.FRAMEX, "" + screenXstored);
				screenYstored = getFrameAdapter().getY();
				Settings.set(Settings.FRAMEY, "" + screenYstored);
			}
		}
		updateP++;
		if (updateP == 8) {
			// System.out.println("Display double buffer is:"+display.isDoubleBuffered());
			updateP = 0;
			if (initRec)
				screencapture();
		}
		if (executable)
			if (CPUspeed != Switches.turbo * 100) {
				CPUspeed = Switches.turbo * 100;
				getFrameAdapter().setTitle("JavaCPC - Amstrad CPC Emulator        CPU:" + CPUspeed + "%");
			}
		if (doAutoopen > 0) {
			doAutoopen++;
			if (doAutoopen >= 2) {
				Switches.askDrive = true;
				loadFile(0, Autoopen, false);
				doAutoopen = 0;

			}
		}
		if (setRoms) {
			setRoms = false;
			updateRoms();
		}
		if (keepDimension && executable && (display.getHeight() != displayheight || display.getWidth() != displaywidth))
			checkDisplay();
		if (followCPU)
			try {
				debugger.updateDisplay();
			} catch (Exception ex) {
			}
		if (tapeload) {
			tapeload = false;
			loadTape(false);
		}
		if (QuitTimer >= 1)
			System.exit(0);
		if (screenshottimer != 0) {
			screenshottimer++;
			if (screenshottimer == 150) {
				screenshot();
				screenshottimer = 0;
			}
		}
		if (screentimer != 0) {
			screentimer++;
			if (screentimer == 10) {
				screenshot();
				screentimer = 0;
			}
		}
		if (dsksavetimer != 0) {
			dsksavetimer++;
			if (dsksavetimer == 12)
				saveDsk();
		}
		if (dskmaketimer != 0) {
			dskmaketimer++;
			if (dskmaketimer == 12)
				CreateDsk();
		}
		if (loadtimer != 0) {
			loadtimer++;
			if (loadtimer == 20) {
				Switches.askDrive = false;
				loaddata();
				loadtimer = 0;
			}
		}

		if (loadprogtimer != 0) {
			Display.loadtimer = 1;
			Switches.osddisplay = false;
			loadprogtimer++;
			if (loadprogtimer == 200) {
				loadprogtimer = 0;
				computer.AutoType();
				Display.loadtimer = 0;
				Switches.osddisplay = osd;
			}

		}

		if (simpletimer != 0) {
			simpletimer++;
			if (simpletimer == 2) {
				System.out.println("SimpleBoot active. Trying to boot " + DSKfile);
				loadFile(DSKfile + ".dsk");
				loadFile(DSKfile + ".zip");
				simpletimer = 0;
			}
		}

		autoLoad();
		if (pausetimer != 0) {
			pausetimer = 0;
			pauseComputer();
		}

		if (autobooter != 0) {
			if (autobooter == 1)
				Display.showboot = 120;
			autobooter = autobooter + 1;
			if (autobooter == 100) {
				reset();
			}
			if (autobooter == 160) {
				reset();
			}
			if (autobooter >= 320) {
				autobooter = 0;
				Switches.booter = 1;
				autoBoot();
			}
		}

		if (dtimer == 1) {
			display.requestFocus();
			dtimer = 0;
		}
	}

	public void autoBoot() {
		computer.reset();
		System.out.print("Trying to autoboot " + compsys);
		if (compsys.equals("CPC664"))
			loadFile("autoboot664.sna");
		else if (compsys.equals("CPC464"))
			loadFile("autoboot464.sna");
		else if (compsys.equals("CPC464PARA"))
			loadFile("autoboot464.sna");
		else
			loadFile("autoboot.sna");

		Switches.booter = 0;
	}

	private void autoLoad() {
		if (autoloader == 1) {
			autoloader = 0;
			pauseComputer();
			loadFile(autostartProg);
			autostartProg = null;
			autoload = 4;
			goComputer();
		} else if (autoloader == 2) {
			autoloader = 0;
			pauseComputer();
			loadFile(autostartDisk);
			autostartDisk = null;
			autoload = 4;
			if (discb != null) {
				computer.setCurrentDrive(1);
				loadFile(discb);
				computer.setCurrentDrive(0);
			}
			goComputer();
		} else if (autoload == 0) {
			autoload = 5;
			pauseComputer();
			LoadFiles();
			goComputer();
		}
	}

	protected boolean loaddrive;
	protected String fileName;

	public void LoadFiles() {
		// load drives from settings
		int numOfDrives = computer.getFloppyDrives() == null ? 0 : computer.getFloppyDrives().length;
		for (int i = 0; i < numOfDrives; i++) {
			fileName = Settings.get(Settings.DRIVE_FILE + Integer.toString(i), null);
			loaddrive = Settings.getBoolean(Settings.LOADDRIVE + Integer.toString(i), false);
			if (loaddrive == true) {
				if (fileName != null) {
					System.out.println("auto load drive " + i + ": " + "*" + fileName + "*");
					computer.setCurrentDrive(i);
					loadFile(Computer.TYPE_SNAPSHOT, fileName, false);
				}
			}
			computer.setCurrentDrive(0);
		}
		fileName = Settings.get(Settings.TAPE_FILE, null);
		loaddrive = Settings.getBoolean(Settings.LOADTAPE, false);
		if (loaddrive == true) {
			if (fileName != null) {
				System.out.println("auto load tape: " + "*" + fileName + "*");
				loadFile(Computer.TYPE_SNAPSHOT, fileName, false);
			}
		}
	}

	public void CreateDsk() {
		applyAlwaysOnTop(false);
		if (Switches.FloppySound)
			flopsound = 1;
		Switches.FloppySound = false;
		loadFile("blankdisk.dsk");
		if (flopsound == 1)
			Switches.FloppySound = true;
		flopsound = 0;
		saveDsk();
		applyAlwaysOnTop(onTop);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// Remember modifiers
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_CONTROL) {
			ctrl = true;
		} else if (keyCode == KeyEvent.VK_SHIFT) {
			shift = true;
		} else if (keyCode == KeyEvent.VK_ALT) {
			alt = true;
		}
		// Keyboard mapping
		e = cloneKeyEvent(e);
		virtualShiftKey = false;
		virtualUnshiftKey = false;
		applyKeyboardMapping(e, true);
		keyCode = e.getKeyCode();
		// Control keys
		if (isControlKeysEnabled()) {
			if (ctrl) {
				if (keyCode == KeyEvent.VK_F1) {
					ctrl = false;
					loadtitle = "Load DSK file to DF0";
					computer.setCurrentDrive(0);
					Switches.askDrive = false;
					loaddata();
					computer.setCurrentDrive(0);
					loadtitle = "Load emulator file";
					return;
				} else if (keyCode == KeyEvent.VK_F2) {
					ctrl = false;
					loadtitle = "Load DSK file to DF1";
					computer.setCurrentDrive(1);
					Switches.askDrive = false;
					loaddata();
					computer.setCurrentDrive(0);
					loadtitle = "Load emulator file";
					return;
				} else if (keyCode == KeyEvent.VK_F3) {
					ctrl = false;
					loadTape(false);
					return;
				} else if (keyCode == KeyEvent.VK_F4) {
					if (shift) {
						ctrl = false;
						shift = false;
						turboCheck();
						return;
					} else {
						ctrl = false;
						loadTape(true);
						return;
					}
				} else if (keyCode == KeyEvent.VK_F5) {
					if (alt) {
						ctrl = false;
						alt = false;
						new EditIni();
						return;
					} else if (!shift) {
						if (!jemu.system.cpc.CPC.tapedeck) {
							jemu.system.cpc.CPC.TapeDrive.setVisible(true);
							jemu.system.cpc.CPC.tapedeck = true;
						} else {
							jemu.system.cpc.CPC.TapeDrive.setVisible(false);
							jemu.system.cpc.CPC.tapedeck = false;
						}
						ctrl = false;
						return;
					}
				} else if (keyCode == KeyEvent.VK_F6) {
					ctrl = false;
					chooseSNA();
					return;
				} else if (keyCode == KeyEvent.VK_F9) {
					ctrl = false;
					reset();
					return;
				} else if (keyCode == KeyEvent.VK_F10) {
					ctrl = false;
					optionPanel.OptionPanel();
					return;
				} else if (keyCode == KeyEvent.VK_F11) {
					ctrl = false;
					screenshot();
					return;
				} else if (keyCode == KeyEvent.VK_F12) {
					ctrl = false;
					MenuCheck();
					return;
				}
			} else {
				if (keyCode == KeyEvent.VK_F11) {
					Autotype.PasteText();
					return;
				}
			}
			// Key recording
			if (KeyRec && ctrl) {
				if (keyCode == KeyEvent.VK_NUMPAD1) {
					ctrl = false;
					System.out.println("keyboard input recording...");
					computer.stopKeys();
					computer.recordKeys();
					return;
				} else if (keyCode == KeyEvent.VK_NUMPAD2) {
					ctrl = false;
					System.out.println("keyboard input stopped...");
					computer.stopKeys();
					return;
				} else if (keyCode == KeyEvent.VK_NUMPAD3) {
					ctrl = false;
					System.out.println("keyboard input playing...");
					computer.stopKeys();
					computer.playKeys();
					return;
				}
			}
			// Misc
			if (keyCode == KeyEvent.VK_ENTER && alt && executable) {
				alt = false;
				FullSize();
				return;
			} else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
				for (int i = 0; i < 4; i++) {
					computer.setCurrentDrive(i);
					mediumeject();
				}
				computer.setCurrentDrive(0);
			} else if (keyCode == KeyEvent.VK_PAGE_UP) {
				Switches.askDrive = true;
				loaddata();
			} else if (keyCode == KeyEvent.VK_SCROLL_LOCK) {
				autosavecheck();
			} else if (keyCode == KeyEvent.VK_PAUSE) {
				pauseToggle();
			} else if (keyCode == KeyEvent.VK_HOME) {
				romsetter.setRoms();
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				if (dialogsnap) {
					snaChooser.dispose();
					computer.start();
					computer.reSync();
					dialogsnap = false;
					return;
				}
			} else if (keyCode == KeyEvent.VK_ADD) {
				applyAlwaysOnTop(false);
				saveDsk();
				applyAlwaysOnTop(onTop);
			}
		}
		// Pass key pressed to the computer
		if (keyToProcessByComputer(e)) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				deferredShift = true;
			} else {
				if ((deferredShift || virtualShiftKey) && !virtualUnshiftKey) {
					computer.processKeyEvent(virtualShiftKeyEventPressed);
				} else if (deferredShift) {
					skipUnshift = true;
				}
				deferredShift = false;
				computer.processKeyEvent(e);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		// Keyboard mapping
		e = cloneKeyEvent(e);
		virtualShiftKey = false;
		virtualUnshiftKey = false;
		applyKeyboardMapping(e, false);
		// Pass key released to the computer
		if (keyToProcessByComputer(e)) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				if (!deferredShift && !skipUnshift) {
					computer.processKeyEvent(e);
				} else {
					deferredShift = false;
					skipUnshift = false;
				}
			} else {
				computer.processKeyEvent(e);
				if (virtualShiftKey) {
					computer.processKeyEvent(virtualShiftKeyEventReleased);
				}
			}
		}
		// Update modifiers
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_CONTROL) {
			ctrl = false;
		} else if (keyCode == KeyEvent.VK_SHIFT) {
			shift = false;
		} else if (keyCode == KeyEvent.VK_ALT) {
			alt = false;
		}
	}

	public void resetKeyModifiers() {
		char cUnd = KeyEvent.CHAR_UNDEFINED;
		keyReleased(new KeyEvent(this, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_CONTROL, cUnd));
		keyReleased(new KeyEvent(this, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_SHIFT, cUnd));
		keyReleased(new KeyEvent(this, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_ALT, cUnd));
	}

	public void breakEscape() {
		if (isRunning()) {
			computer.breakEscape();
		}
	}

	private KeyEvent cloneKeyEvent(KeyEvent e) {
		return new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
				e.getKeyLocation());
	}

	private boolean keyToProcessByComputer(KeyEvent e) {
		if (Switches.blockKeyboard)
			return false;
		if (isFunctionKey(e) && !e.isShiftDown())
			return false;
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_UNDEFINED)
			return false;
		if (keyCode == KeyEvent.VK_CONTROL)
			return false;
		if (keyCode == KeyEvent.VK_ALT)
			return false;
		if (isAlphabeticKey(e) && (ctrl || alt))
			return false;
		return true;
	}

	private boolean isAlphabeticKey(KeyEvent e) {
		int keyCode = e.getKeyCode();
		return keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z;
	}

	private boolean isFunctionKey(KeyEvent e) {
		int keyCode = e.getKeyCode();
		return keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12;
	}

	private void applyKeyboardMapping(KeyEvent e, boolean updateKeyboardLabel) {
		if (localkeys.equals("DE_DE")) {
			applyGermanKeyboardMapping(e, updateKeyboardLabel);
		} else if (localkeys.equals("ES_ES")) {
			applySpanishKeyboardMapping(e, updateKeyboardLabel);
		} else {
			applyEnglishKeyboardMapping(e, updateKeyboardLabel);
		}
	}

	private void applyGermanKeyboardMapping(KeyEvent e, boolean updateKeyboardLabel) {
		if (updateKeyboardLabel && !keys.equals("German keyboard  ")) {
			keys = "German keyboard  ";
			Keys.setLabel(keys);
		}
		int keyChar = e.getKeyChar();
		if (keyChar == '\u00FC' || keyChar == '\u00DC') {
			e.setKeyCode(KeyEvent.VK_OPEN_BRACKET);
		} else if (keyChar == '\u00E4' || keyChar == '\u00C4') {
			e.setKeyCode(KeyEvent.VK_QUOTE);
		} else if (keyChar == '\u00F6' || keyChar == '\u00D6') {
			e.setKeyCode(KeyEvent.VK_SEMICOLON);
		} else if (keyChar == '\u00DF' || keyChar == '\u003F') {
			e.setKeyCode(KeyEvent.VK_MINUS);
		}
		int keyCode = e.getKeyCode();
		if (keyCode == 0x2d) { // - content to /
			e.setKeyCode(KeyEvent.VK_SLASH);
		} else if (keyCode == 0x81) { // ï¿½ content to ^
			e.setKeyCode(KeyEvent.VK_EQUALS);
		} else if (keyCode == 0x99) { // <> content to [
			e.setKeyCode(KeyEvent.VK_ALT_GRAPH);
		} else if (keyCode == 0x82) { // ^ content to TAB
			e.setKeyCode(KeyEvent.VK_TAB);
		} else if (keyCode == 0x208) { // # content to \
			e.setKeyCode(KeyEvent.VK_BACK_SLASH);
		} else if (keyCode == 0x209) { // + content to ]
			e.setKeyCode(KeyEvent.VK_CLOSE_BRACKET);
		} else if (keyCode == KeyEvent.VK_Z) { // change Z to Y
			e.setKeyCode(KeyEvent.VK_Y);
		} else if (keyCode == KeyEvent.VK_Y) { // and Y to Z
			e.setKeyCode(KeyEvent.VK_Z);
		}
	}

	private void applySpanishKeyboardMapping(KeyEvent e, boolean updateKeyboardLabel) {
		if (updateKeyboardLabel && !keys.equals("Spanish keyboard  ")) {
			keys = "Spanish keyboard  ";
			Keys.setLabel(keys);
		}
		int keyChar = e.getKeyChar();
		if (keyChar == '\u00BA' || keyChar == '\u00B2') {
			e.setKeyCode(KeyEvent.VK_TAB);
		} else if (keyChar == '\u00E7' || keyChar == '\u00C7') {
			e.setKeyCode(KeyEvent.VK_BACK_SLASH);
		} else if (keyChar == '\u00D1' || keyChar == '\u00F1') {
			e.setKeyCode(KeyEvent.VK_SEMICOLON);
		}
		int keyCode = e.getKeyCode();
		if (keyCode == 0xde) {
			e.setKeyCode(KeyEvent.VK_MINUS);
		} else if (keyCode == 0x2d) { // - content to /
			e.setKeyCode(KeyEvent.VK_SLASH);
		} else if (keyCode == 0x81) {
			e.setKeyCode(KeyEvent.VK_QUOTE);
		} else if (keyCode == 0x209) { // + content to ]
			e.setKeyCode(KeyEvent.VK_CLOSE_BRACKET);
		} else if (keyCode == 0x206) {
			e.setKeyCode(KeyEvent.VK_EQUALS);
		} else if (keyCode == 0x99) {
			e.setKeyCode(KeyEvent.VK_ALT_GRAPH);
		} else if (keyCode == 0x80) {
			e.setKeyCode(KeyEvent.VK_OPEN_BRACKET);
		}
	}

	private void applyEnglishKeyboardMapping(KeyEvent e, boolean updateKeyboardLabel) {
		if (updateKeyboardLabel && !keys.equals("English keyboard  ")) {
			keys = "English keyboard  ";
			Keys.setLabel(keys);
		}
		char keyChar = e.getKeyChar();
		int keyCode = e.getKeyCode();
		if ("²³°é§èçàùµ~¨".indexOf(keyChar) >= 0) {
			e.setKeyCode(KeyEvent.VK_UNDEFINED);
		} else if (keyCode == 48 && e.isShiftDown()) {
			e.setKeyCode(123); // 0
		} else if (keyCode >= 49 && keyCode <= 57 && e.isShiftDown()) {
			e.setKeyCode(112 + keyCode - 49); // 1,2,...,9
		} else if (keyCode == 106) {
			virtualShiftKey = true;
			e.setKeyCode(59); // numpad '*'
		} else if (keyCode == 107) {
			virtualShiftKey = true;
			e.setKeyCode(222); // numpad '+'
		} else if (keyCode == 109) {
			e.setKeyCode(45); // numpad '-'
		} else if (keyCode == 111) {
			e.setKeyCode(47); // numpad '/'
		} else if (keyChar == '&') {
			virtualShiftKey = true;
			e.setKeyCode(54);
		} else if (keyChar == '|') {
			virtualShiftKey = true;
			e.setKeyCode(91);
		} else if (keyChar == '"') {
			virtualShiftKey = true;
			e.setKeyCode(50);
		} else if (keyChar == '#') {
			virtualShiftKey = true;
			e.setKeyCode(51);
		} else if (keyChar == '\'') {
			virtualShiftKey = true;
			e.setKeyCode(55);
		} else if (keyChar == '(') {
			virtualShiftKey = true;
			e.setKeyCode(56);
		} else if (keyChar == '!') {
			virtualShiftKey = true;
			e.setKeyCode(49);
		} else if (keyChar == '{') {
			virtualShiftKey = true;
			e.setKeyCode(65406);
		} else if (keyChar == '}') {
			virtualShiftKey = true;
			e.setKeyCode(93);
		} else if (keyChar == ')') {
			virtualShiftKey = true;
			e.setKeyCode(57);
		} else if (keyChar == '$') {
			virtualShiftKey = true;
			e.setKeyCode(52);
		} else if (keyChar == '´') {
			virtualShiftKey = true;
			e.setKeyCode(55);
		} else if (keyChar == '`') {
			virtualShiftKey = true;
			e.setKeyCode(92);
		} else if (keyChar == '<') {
			virtualShiftKey = true;
			e.setKeyCode(44);
		} else if (keyChar == '=') {
			virtualShiftKey = true;
			e.setKeyCode(45);
		} else if (keyChar == '@') {
			e.setKeyCode(91);
		} else if (keyChar == '-') {
			e.setKeyCode(45);
		} else if (keyChar == '^') {
			e.setKeyCode(61);
		} else if (keyChar == '[') {
			e.setKeyCode(65406);
		} else if (keyChar == ']') {
			e.setKeyCode(93);
		} else if (keyChar == '\\') {
			e.setKeyCode(92);
		} else if (keyChar == ',') {
			e.setKeyCode(44);
		} else if (keyChar == ';') {
			e.setKeyCode(222);
		} else if (keyChar == ':') {
			e.setKeyCode(59);
		} else if (keyChar == '/') {
			virtualUnshiftKey = true;
			e.setKeyCode(47);
		} else if (keyChar == '_') {
			e.setKeyCode(48);
		} else if (keyChar == '*') {
			e.setKeyCode(59);
		} else if (keyChar == '%') {
			e.setKeyCode(53);
		} else if (keyChar == '£') {
			e.setKeyCode(61);
		} else if (keyChar == '>') {
			e.setKeyCode(46);
		} else if (keyChar == '?') {
			e.setKeyCode(47);
		} else if (keyChar == '.') {
			e.setKeyCode(110);
		} else if (keyChar == '+') {
			e.setKeyCode(222);
		}
	}

	public void MenuCheck() {
		if (executable) {
			// frame.dispose();
			if (showmenu) {
				showmenu = false;
				getFrameAdapter().removeMenuBar(JavaCPCMenu);
				Settings.setBoolean(Settings.SHOWMENU, false);

			} else {
				showmenu = true;
				getFrameAdapter().setMenuBar(JavaCPCMenu);
				Settings.setBoolean(Settings.SHOWMENU, true);

			}
			getFrameAdapter().pack();
			// frame.setVisible(true);
			display.requestFocus();
		} else {
			if (showmenu == false) {
				showmenu = true;
				OnlineMenu();
			} else {
				showmenu = false;
				onlinemenu.dispose();
			}

		}
	}

	public void mouseClicked(MouseEvent e) {
		if (isMouseClickActionsEnabled() && mousejoy == false && !Switches.lightGun) {
			if (e.getClickCount() == 2) {
				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
					Autotype.typeconsole.setVisible(true);
				} else {
					MenuCheck();
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (mousejoy) {
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				computer.MouseFire1();
			} else {
				computer.MouseFire2();
			}
		}
		if (Switches.lightGun) {
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				System.out.println("X = " + e.getX() + " Y = " + e.getY());
				display.mouseX = e.getX() / 2;
				display.mouseY = e.getY() / 2;
				if (display.processGun()) {
					System.out.println("Fire possible");
					computer.MouseFire1();
				} else {
					System.out.println("No fire possible");
				}
			}
		}
		display.requestFocus();
	}

	public void mouseReleased(MouseEvent e) {
		if (mousejoy) {
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				computer.MouseReleaseFire1();
			} else {
				computer.MouseReleaseFire2();
			}
		}
		if (Switches.lightGun) {
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				computer.MouseReleaseFire1();
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void showDebugger() {
		// Switches.showPalette = true;
		try {
			System.out.println("showDebugger");
			if (debugger == null) {
				JEMU.debugger = (Debugger) Util.secureConstructor(Debugger.class, new Class[] {}, new Object[] {});
				JEMU.debugger.setBounds(0, 0, 640, 480);
				debugger.setComputer(computer);
			}
			System.out.println("Showing Debugger");
			debugger.setVisible(true);
			debugger.toFront();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Computer getComputer() {
		return computer;
	}

	public String loadFile(String name) {
		Switches.loaded = false;
		return loadFile(Computer.TYPE_SNAPSHOT, name, true);
	}

	public String loadFile(int type, String name, boolean usePath) {
		if (name.startsWith("http") && useURL)
			usePath = false;
		if (Switches.askDrive && (name.toLowerCase().endsWith(".dsk") || name.toLowerCase().endsWith(".dsz"))) {
			try {
				askDrive(name);
			} catch (Exception error) {
			}
			return "";
		}
		Switches.loaded = false;
		String result = null;
		try {
			boolean running = computer.isRunning();
			isTape = false;
			computer.stop();
			try {
				String fileName = (usePath ? computer.getFilePath() : "") + name;
				if (name.toString().toUpperCase().endsWith(".WAV") || name.toString().toUpperCase().endsWith(".JTP")
						|| name.toString().toUpperCase().endsWith(".TAP"))
					isTape = true;
				else
					isTape = false;
				if (name.toString().toUpperCase().endsWith(".YM")) {
					fileName = ("") + name;
					ymControl.setVisible(true);
					ymControl.btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
					ymControl.btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
					ymControl.btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
					ymControl.btnREC.setBackground(new Color(0xff, 0x00, 0x00));
					ymControl.btnPLAY.setBackground(Color.BLACK);
					ymControl.btnREC.setForeground(Color.WHITE);
					ymControl.btnPLAY.setForeground(Color.LIGHT_GRAY);
					// ymControl.YM_Counter.setBackground(new Color (0xB8,0xCF,0xE5));
					YMControl.YM_Counter.setForeground(new Color(0x18, 0x2F, 0x35));
				}
				computer.loadFile(type, fileName);
				result = computer.getFileInfo(name);

				Switches.loadname = name;

				Display.showdisk = 150;

				if (Switches.booter == 0) {
					if (!isTape) {
						Settings.set(Settings.DRIVE_FILE + Integer.toString(computer.getCurrentDrive()), fileName);
						Settings.setBoolean(Settings.LOADDRIVE + Integer.toString(computer.getCurrentDrive()), true);
					} else {
						Settings.set(Settings.TAPE_FILE, fileName);
						Settings.setBoolean(Settings.LOADTAPE, true);
					}
				}

			} finally {
				display.requestFocus();
				if (running)
					computer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean head = Settings.getBoolean(Settings.DF0HEAD, false);
		if (head) {
			setFloppyHead(0, 1);
			head0U.setState(true);
		} else
			head0L.setState(true);
		head = Settings.getBoolean(Settings.DF1HEAD, false);
		if (head) {
			setFloppyHead(1, 1);
			head1U.setState(true);
		} else
			head1L.setState(true);
		return result;
	}

	public byte readByteFromUnmappedMemory(int memoryAddress) {
		return computer.readByteFromUnmappedMemory(memoryAddress);
	}

	public byte[] readBytesFromUnmappedMemory(int memoryOffset, int memoryLength) {
		return computer.readBytesFromUnmappedMemory(memoryOffset, memoryLength);
	}

	public void writeByteToUnmappedMemory(int memoryAddress, byte value) {
		computer.writeByteToUnmappedMemory(memoryAddress, value);
	}

	public void writeBytesToUnmappedMemory(int memoryOffset, byte[] data) {
		writeBytesToUnmappedMemory(memoryOffset, data, 0, data.length);
	}

	public void writeBytesToUnmappedMemory(int memoryOffset, byte[] data, int dataOffset, int dataLength) {
		computer.writeBytesToUnmappedMemory(memoryOffset, data, dataOffset, dataLength);
	}

	public void resetComputer() {
		checkTurbo.setState(false);
		computer.reset();
		Display.showpause = 0;
		computer.start();
		computer.reset();
		Display.showpause = 0;
		computer.start();
	}

	public void rebootComputer() {
		checkTurbo.setState(false);
		computer.reset();
		Display.showpause = 0;
		computer.start();
		reBoot();
	}

	public void ejectComputer() {
		computer.eject();
	}

	public void goComputer() {
		Display.showpause = 0;
		computer.start();
	}

	public void stopComputer() {
		Display.showpause = 1;
		display.repaint();
		pausetimer = 1;
	}

	public void pauseComputer() {
		computer.stop();
	}

	public void fsoundcheck() {
		if (fsound == false) {
			Switches.FloppySound = true;
			Settings.setBoolean(Settings.FLOPPYSOUND, true);
			System.out.println("Drive mechanic noise enabled");
			Display.automessage = "Drive mechanic noise emulation is ON";
			Display.showauto = 150;
			drivesound.setState(true);
			fsound = true;
		} else {
			Switches.FloppySound = false;
			Settings.setBoolean(Settings.FLOPPYSOUND, false);
			System.out.println("Drive mechanic noise disabled");
			Display.automessage = "Drive mechanic noise emulation is OFF";
			Display.showauto = 150;
			drivesound.setState(false);
			fsound = false;
		}

	}

	public void nbcheck() {

		if (notebook == false) {
			Switches.notebook = true;
			Settings.setBoolean(Settings.NOTEBOOK, true);
			System.out.println("Notebook Enabled");
			System.out.println("Q, A, O, P = Direction-control, SPACE, CTRL = Firebutton 1,2");
			Display.automessage = "Notebook is ON";
			Display.showauto = 150;
			notebook = true;
		} else {
			Switches.notebook = false;
			Settings.setBoolean(Settings.NOTEBOOK, false);
			System.out.println("Notebook Disabled");
			Display.automessage = "Notebook is OFF";
			Display.showauto = 150;
			notebook = false;
		}
	}

	public void pauseToggle() {
		if (!isPaused()) {
			setPaused(1);
			stopComputer();
			System.out.println("System halted");
		} else {
			setPaused(0);
			goComputer();
			System.out.println("System resumed");
		}
		firePauseStateChanged();
	}

	public boolean isPaused() {
		return paused == 1;
	}

	private void setPaused(int paused) {
		this.paused = paused;
	}

	private void firePauseStateChanged() {
		for (PauseListener listener : getPauseListeners())
			listener.pauseStateChanged(this, isPaused());
	}

	public void info() {
		message();
	}

	public void joycheck() {

		if (Switches.joystick == 1) {
			Switches.joystick = 0;
			Settings.setBoolean(Settings.JOYSTICK, false);
			System.out.println("Joystick emulation disabled");
			Display.automessage = "Joystick is OFF";
			Display.showauto = 150;
		} else {
			Switches.joystick = 1;
			Settings.setBoolean(Settings.JOYSTICK, true);
			Display.automessage = "Joystick is ON";
			Display.showauto = 150;
			System.out.println(
					"Joystick emulation enabled\nuse seperate number-block with NUM-Lock on\n4, 8, 6, 2 - directions, 5, 0 - fire-buttons");
		}
	}

	public void audiocheck() {

		if (Switches.audioenabler == 0) {
			Switches.audioenabler = 1;
			System.out.println("Audio Enabled");
			Settings.setBoolean(Settings.AUDIO, true);
			Display.automessage = "Audio enabled";
			Display.showauto = 150;

			this.display.requestFocus();
		} else {
			if (Switches.audioenabler == 1) {
				Switches.audioenabler = 0;
				System.out.println("Audio Disabled");
				Settings.setBoolean(Settings.AUDIO, false);
				Display.automessage = "Audio disabled";
				Display.showauto = 150;

				this.display.requestFocus();
			}

		}
	}

	public void dskmerge() {
		jemu.system.cpc.CPCDiscImageMerger.merge();
	}

	public void autosavecheck() {
		if (autosave) {
			System.out.println("Autosave Disabled");
			Settings.setBoolean(Settings.AUTOSAVE, false);
			Display.automessage = "Autosave is OFF";
			Display.showauto = 150;
			autosave = false;
			Switches.autosave = false;
		} else {
			System.out.println("Autosave Enabled");
			Settings.setBoolean(Settings.AUTOSAVE, true);
			Display.automessage = "Autosave is ON";
			Display.showauto = 150;
			autosave = true;
			Switches.autosave = true;
		}
	}

	public void alwaysOnTopCheck() {
		applyAlwaysOnTop(onTop);
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		if (executable && onTop != alwaysOnTop) {
			onTop = alwaysOnTop;
			aontop.setState(alwaysOnTop);
			Settings.setBoolean(Settings.ONTOP, alwaysOnTop);
			applyAlwaysOnTop(alwaysOnTop);
		}
	}

	private void applyAlwaysOnTop(boolean alwaysOnTop) {
		if (executable) {
			getFrameAdapter().setAlwaysOnTop(alwaysOnTop);
		}
	}

	public void autobootcheck() {
		autobooter = 1;
	}

	public void reset() {
		checkTurbo.setState(false);
		computer.reset();
		Display.showdisk = 150;
		this.display.requestFocus();
	}

	public void mediumeject() {
		computer.eject();
		this.display.requestFocus();
		Settings.set(Settings.DRIVE_FILE + Integer.toString(computer.getCurrentDrive()), "empty");
		Settings.setBoolean(Settings.LOADDRIVE + Integer.toString(computer.getCurrentDrive()), false);
		Display.showdisk = 150;
	}

	public void tapeEject() {
		computer.tapeEject();
		jemu.system.cpc.CPC.tapeloaded = false;
		Settings.set(Settings.TAPE_FILE, "~none~");
		Settings.setBoolean(Settings.LOADTAPE, false);
	}

	public void setComputer(final String name) throws Exception {
		GateArray.doRender = false;
		rendertimer = 1;
		if (name.toLowerCase().equals("kccomp")) {
			Switches.computername = Util.hexValue("7");
			compName.setEnabled(false);
		} else {
			Switches.computername = Util.hexValue(Settings.get(Settings.COMPUTERNAME, "7"));
			compName.setEnabled(true);
		}

		jemu.system.cpc.CPC.checkSave();
		for (int i = 0; i < 500; i++)
			try {
				Thread.activeCount();
			} catch (final Exception e) {
			}
		if (Switches.FloppySound)
			Samples.DEGAUSS.play();
		Display.txtpos = Display.txtstart;
		Display.ytext = Display.ztext;
		Switches.loaddrivea = "Drive is empty.";
		Switches.loaddriveb = "Drive is empty.";
		Switches.loaddrivec = "Drive is empty.";
		Switches.loaddrived = "Drive is empty.";
		System.out.println(name + " choosen.");
		setComputer(name, true);
		Display.model = "System: " + computer.getName();
		Display.showmodel = 100;
		compsys = name;
	}

	public void setComputer(final String name, final boolean start) throws Exception {
		try {
			jemu.core.device.floppy.UPD765A.floppy.dispose();
		} catch (Exception thereisnofloppy) {
		}
		try {
			// if (computer == null || !name.equalsIgnoreCase(computer.getName())) {
			{
				compsys = name;
				Computer newComputer = Computer.createComputer(this, name);
				if (computer != null) {
					Drive[] floppies = computer.getFloppyDrives();
					if (floppies != null)
						for (int i = 0; i < floppies.length; i++)
							if (floppies[i] != null)
								floppies[i].setActiveListener(null);
					for (ComputerKeyboardListener listener : computer.getKeyboardListeners()) {
						computer.removeKeyboardListener(listener);
						newComputer.addKeyboardListener(listener);
					}
					for (MemoryWriteObserver observer : computer.getMemoryWriteObservers()) {
						computer.removeMemoryWriteObserver(observer);
						newComputer.addMemoryWriteObserver(observer);
					}
					computer.dispose();
					computer = null;
					Runtime runtime = Runtime.getRuntime();
					runtime.gc();
					runtime.runFinalization();
					runtime.gc();
					System.out.println("Computer Disposed");
				}
				computer = newComputer;
				Settings.set(Settings.SYSTEM, name);
				setFullSize(large);
				computer.start();
				computer.initialise();
				Drive[] floppies = computer.getFloppyDrives();
				if (floppies != null)
					for (int i = 0; i < floppies.length; i++)
						if (floppies[i] != null)
							floppies[i].setActiveListener(this);
				// this.jcbImage.removeAllItems();
				// this.jcbDrive.removeAllItems();
				gotGames = false;
				// try {
				// Vector files = computer.getFiles();
				/*
				 * for (int i = 0; i < files.size(); i++) { this.jcbImage.addItem(files.elementAt(i)); }
				 * this.jcbImage.setSelectedIndex(-1);
				 */
				// } finally {
				// gotGames = true;
				// }

				/*
				 * final Vector floppydrives = getDrives(); for (int i = 0; i < floppydrives.size(); i++) {
				 * this.jcbDrive.addItem(floppydrives.elementAt(i)); } if (Switches.computersys == 0) {
				 * this.jcbDrive.setSelectedIndex(-1); } else { this.jcbDrive.setSelectedIndex(0); }
				 */
				if (debugger != null)
					debugger.setComputer(computer);
				if (start)
					computer.start();
			}
			showDevices();
		} catch (Exception p) {
		}
	}

	public void setAudio(final boolean value) {
		audiooutput = value;
		if (audiooutput)
			Switches.audioenabler = 1;
		else
			Switches.audioenabler = 0;
		this.display.requestFocus();
	}

	public void turboCheck() {
		if (Switches.turbo == 1) {
			Switches.turbo = 4;
			checkTurbo.setState(true);
		} else {
			Switches.turbo = 1;
			checkTurbo.setState(false);
		}
		computer.reSync();
	}

	public void setTurbo(final boolean value) {
		boolean turbocheck = value;
		if (turbocheck) {
			Switches.turbo = 4;
			checkTurbo.setState(true);
		} else {
			Switches.turbo = 1;
			checkTurbo.setState(false);
		}
		this.display.requestFocus();
		computer.reSync();
	}

	public void setFloppy(final boolean value) {
		floppyoutput = value;
		if (floppyoutput)
			Switches.FloppySound = true;
		else
			Switches.FloppySound = false;
		this.display.requestFocus();
	}

	public void setJoy(final boolean value) {
		altjoystick = value;
		if (altjoystick)
			Switches.notebook = true;
		else
			Switches.notebook = false;
		this.display.requestFocus();
	}

	public void showAutotype() {

		Autotype.typeconsole.setVisible(true);
		// new Autotype();
	}

	public void setFullSize(boolean value) {
		GateArray.doRender = false;
		rendertimer = 1;
		large = value;
		if (large) {
			if (skinned) {
				Moniup.setVisible(false);
				Monidown.setVisible(false);
				Monileft.setVisible(false);
				Moniright.setVisible(false);
			}
		} else {
			if (skinned) {
				Moniup.setVisible(true);
				Monidown.setVisible(true);
				Monileft.setVisible(true);
				Moniright.setVisible(true);
			}
		}
		final boolean running = computer.isRunning();
		computer.stop();
		computer.setLarge(large);
		display.setImageSize(computer.getDisplaySize(large), computer.getDisplayScale(large));
		computer.setDisplay(display);
		if (running)
			computer.start();
		this.display.requestFocus();
		setOutputSize();
		computer.reSync();
	}

	public void insertDisk() {
		loadtimer = 1;
	}

	public void setDoubleSize(final boolean value) {
		Switches.triplesize = false;
		Switches.doublesize = value;
		if (Switches.doublesize) {
			if (skinned) {
				Moniup.setVisible(false);
				Monidown.setVisible(false);
				Monileft.setVisible(false);
				Moniright.setVisible(false);
			}
		} else {
			if (skinned) {
				if (!large) {
					Moniup.setVisible(true);
					Monidown.setVisible(true);
					Monileft.setVisible(true);
					Moniright.setVisible(true);
				}
			}
		}
		// fullscreen = false;
		Settings.setBoolean(Settings.TRIPLE, false);
		Settings.setBoolean(Settings.DOUBLE, value);
		final boolean running = computer.isRunning();
		computer.stop();
		computer.setLarge(large);
		this.display.setImageSize(computer.getDisplaySize(large), computer.getDisplayScale(large));
		computer.setDisplay(this.display);
		if (running) {
			computer.start();
		}
		this.display.requestFocus();
	}

	public void setTripleSize(final boolean value) {
		Switches.doublesize = false;
		Switches.triplesize = value;
		if (Switches.triplesize) {
			if (skinned) {
				Moniup.setVisible(false);
				Monidown.setVisible(false);
				Monileft.setVisible(false);
				Moniright.setVisible(false);
			}
		} else {
			if (skinned) {
				if (large != true) {
					Moniup.setVisible(true);
					Monidown.setVisible(true);
					Monileft.setVisible(true);
					Moniright.setVisible(true);
				}
			}
		}
		// fullscreen = false;
		Settings.setBoolean(Settings.TRIPLE, value);
		Settings.setBoolean(Settings.DOUBLE, false);
		final boolean running = computer.isRunning();
		computer.stop();
		computer.setLarge(large);
		this.display.setImageSize(computer.getDisplaySize(large), computer.getDisplayScale(large));
		computer.setDisplay(this.display);
		if (running) {
			computer.start();
		}
		this.display.requestFocus();
	}

	public Window findWindow(Component comp) {
		while (comp != null)
			if (comp instanceof Window)
				return (Window) comp;
			else
				comp = comp.getParent();
		return null;
	}

	public Vector<String> getFiles() {
		Vector<FileDescriptor> files = computer == null ? new Vector<FileDescriptor>() : computer.getFiles();
		Vector<String> result = files.size() == 0 ? new Vector<String>() : new Vector<String>(files.size() * 2);
		for (int i = 0; i < files.size(); i++) {
			FileDescriptor file = (FileDescriptor) files.elementAt(i);
			result.addElement(file.description); // Description
			result.addElement(file.filename); // Name
		}
		return result;
	}

	public Vector<String> getDrives() {
		int floppydrives = computer == null || computer.getFloppyDrives() == null ? 0
				: computer.getFloppyDrives().length;
		final Vector<String> result = floppydrives == 0 ? new Vector<String>() : new Vector<String>(floppydrives);
		for (int i = 0; i < floppydrives; i++) {
			result.addElement(new String("DF" + i + ":"));
		}
		return result;
	}

	public void focusLost(FocusEvent e) {
		// Switches.audioenabler = 0;
		computer.displayLostFocus();
	}

	public void focusGained(FocusEvent e) {

		/*
		 * if (audiooutput){ boolean audioon = Settings.getBoolean(Settings.AUDIO, true); if (audioon)
		 * Switches.audioenabler = 1;}
		 */
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == KeepA) {
			KeepA.setBackground(Color.GREEN);
			KeepB.setBackground(Color.LIGHT_GRAY);
			KeepC.setBackground(Color.LIGHT_GRAY);
			KeepD.setBackground(Color.LIGHT_GRAY);
			KeepE.setBackground(Color.LIGHT_GRAY);
			KeepF.setBackground(Color.LIGHT_GRAY);

			computer.setCurrentDrive(0);
			mediumeject();
			loadFile(keepA);
		}
		if (e.getSource() == KeepB) {
			KeepA.setBackground(Color.LIGHT_GRAY);
			KeepB.setBackground(Color.GREEN);
			KeepC.setBackground(Color.LIGHT_GRAY);
			KeepD.setBackground(Color.LIGHT_GRAY);
			KeepE.setBackground(Color.LIGHT_GRAY);
			KeepF.setBackground(Color.LIGHT_GRAY);

			computer.setCurrentDrive(0);
			mediumeject();
			loadFile(keepB);
		}
		if (e.getSource() == KeepC) {
			KeepA.setBackground(Color.LIGHT_GRAY);
			KeepB.setBackground(Color.LIGHT_GRAY);
			KeepC.setBackground(Color.GREEN);
			KeepD.setBackground(Color.LIGHT_GRAY);
			KeepE.setBackground(Color.LIGHT_GRAY);
			KeepF.setBackground(Color.LIGHT_GRAY);

			computer.setCurrentDrive(0);
			mediumeject();
			loadFile(keepC);
		}
		if (e.getSource() == KeepD) {
			KeepA.setBackground(Color.LIGHT_GRAY);
			KeepB.setBackground(Color.LIGHT_GRAY);
			KeepC.setBackground(Color.LIGHT_GRAY);
			KeepD.setBackground(Color.GREEN);
			KeepE.setBackground(Color.LIGHT_GRAY);
			KeepF.setBackground(Color.LIGHT_GRAY);

			computer.setCurrentDrive(0);
			mediumeject();
			loadFile(keepD);
		}
		if (e.getSource() == KeepE) {
			KeepA.setBackground(Color.LIGHT_GRAY);
			KeepB.setBackground(Color.LIGHT_GRAY);
			KeepC.setBackground(Color.LIGHT_GRAY);
			KeepD.setBackground(Color.LIGHT_GRAY);
			KeepE.setBackground(Color.GREEN);
			KeepF.setBackground(Color.LIGHT_GRAY);

			computer.setCurrentDrive(0);
			mediumeject();
			loadFile(keepE);
		}
		if (e.getSource() == KeepF) {
			KeepA.setBackground(Color.LIGHT_GRAY);
			KeepB.setBackground(Color.LIGHT_GRAY);
			KeepC.setBackground(Color.LIGHT_GRAY);
			KeepD.setBackground(Color.LIGHT_GRAY);
			KeepE.setBackground(Color.LIGHT_GRAY);
			KeepF.setBackground(Color.GREEN);

			computer.setCurrentDrive(0);
			mediumeject();
			loadFile(keepF);
		}
		if (e.getSource() == makeshot) {
			screenpreview.dispose();
			saveShot();
		}
		if (e.getSource() == startRec) {
			doRec = true;
			startR = true;
		}
		if (e.getSource() == pauseRec) {
			if (pauserec)
				pauserec = false;
			else
				pauserec = true;
			flasher = 0;
		}
		if (e.getSource() == stopRec) {
			startR = false;

		}

		if (e.getSource() == cancelshot) {
			screenpreview.dispose();
		}
		if (e.getSource() == okay && showabout) {
			applyAlwaysOnTop(onTop);
			about.dispose();
			showabout = false;
		}
		if (e.getSource() == s64) {
			computer.start();
			snaChooser.dispose();
			dialogsnap = false;
			Switches.save64 = true;
		}
		if (e.getSource() == s128) {
			computer.start();
			snaChooser.dispose();
			dialogsnap = false;
			Switches.save128 = true;
		}
		if (e.getSource() == s256) {
			computer.start();
			snaChooser.dispose();
			dialogsnap = false;
			Switches.save256 = true;
		}
		if (e.getSource() == s512) {
			computer.start();
			snaChooser.dispose();
			dialogsnap = false;
			Switches.save512 = true;
		}
		if (e.getSource() == cancel) {
			computer.start();
			dialogsnap = false;
			snaChooser.dispose();
		} else if (e.getSource() instanceof MenuItem) {
			String menuAdd = e.getActionCommand();
			if (menuAdd.equals("Quit")) {
				quit();
			} else if (menuAdd.equals("Reset            Ctrl + F9"))
				reset();
			else if (menuAdd.equals("Reboot"))
				reBoot();
			else if (menuAdd.equals(extra1))
				new UpdateInfo("http://www.cpcgamereviews.com/a/index.html", "CPCGameReviews");
			else if (menuAdd.equals(extra2))
				new UpdateInfo("http://cpcgames.dirtyangels.eu", "Own CPC games archive");
			else if (menuAdd.equals(extra3))
				new UpdateInfo("http://www.homecomputerworld.com/0-9-cpc.html", "Homecomputerworld");
			else

			if (menuAdd.equals("Force quit")) {
				reportBug();
				System.exit(0);
			} else if (menuAdd.equals("About")) {
				info();
			} else if (menuAdd.equals("Create new DSK")) {
				applyAlwaysOnTop(false);
				CreateDsk();
				applyAlwaysOnTop(onTop);
			} else if (menuAdd.equals("Load...")) {
				Switches.askDrive = true;
				loaddata();
			} else if (menuAdd.equals("Debugger"))
				showDebugger();
			else if (menuAdd.equals(CPUfollow)) {
				if (followCPU)
					followCPU = false;
				else
					followCPU = true;
			} else if (menuAdd.equals("Boot CP/M"))
				computer.bootCPM();
			else if (menuAdd.equals(recordkeys))
				RecInfo();
			else if (menuAdd.equals("ROM Setup")) {
				applyAlwaysOnTop(false);
				romsetter.setRoms();
				applyAlwaysOnTop(onTop);
			} else if (menuAdd.equals("Java console"))
				Console.frameconsole.setVisible(true);
			else if (menuAdd.equals("Pause"))
				pauseToggle();
			else if (menuAdd.equals("Eject all")) {
				computer.setCurrentDrive(3);
				mediumeject();
				computer.setCurrentDrive(2);
				mediumeject();
				computer.setCurrentDrive(1);
				mediumeject();
				computer.setCurrentDrive(0);
				mediumeject();
			} else if (menuAdd.equals(checkup)) {
				try {
					UpdateCheck();
				} catch (final Exception er) {
				}
			} else if (menuAdd.equals(reportBug)) {
				applyAlwaysOnTop(false);
				reportBug();
				applyAlwaysOnTop(onTop);
			} else if (menuAdd.equals(downl)) {
				openWebPage("http://cpc-live.com/download.php?list.7");
			} else if (menuAdd.equals(homepage)) {
				openWebPage("http://cpc-live.com");
			} else if (menuAdd.equals(sourceforge)) {
				openWebPage("https://sourceforge.net/projects/javacpc");
			} else if (menuAdd.equals(showYM)) {
				ymControl.setVisible(true);
			} else if (menuAdd.equals(showRec)) {
				// new AudioRecorder();
				jemu.system.cpc.CPC.showAudioCapture = true;
			} else if (menuAdd.equals(showCap)) {
				FileDialog filedia = getFrameAdapter().createFileDialog("Save Animated Gif...", FileDialog.SAVE);
				filedia.setFile("*.gif");
				filedia.setVisible(true);
				String filename = filedia.getFile();
				if (filename != null) {
					filename = filedia.getDirectory() + filedia.getFile();
					String savename = filename;
					if (!savename.toLowerCase().endsWith(".gif"))
						savename = savename + ".gif";
					captureName = savename;
					initRec = true;
				}
			} else

			if (menuAdd.equals(df0sav)) {
				computer.setCurrentDrive(0);
				Switches.dskcheck = true;
			} else if (menuAdd.equals(df1sav)) {
				computer.setCurrentDrive(1);
				Switches.dskcheck = true;
			} else if (menuAdd.equals(df2sav)) {
				computer.setCurrentDrive(2);
				Switches.dskcheck = true;
			} else if (menuAdd.equals(df3sav)) {
				computer.setCurrentDrive(3);
				Switches.dskcheck = true;
			} else if (menuAdd.equals(df0save)) {
				computer.setCurrentDrive(0);
				applyAlwaysOnTop(false);
				saveDsk();
				applyAlwaysOnTop(onTop);
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df1save)) {
				computer.setCurrentDrive(1);
				applyAlwaysOnTop(false);
				saveDsk();
				applyAlwaysOnTop(onTop);
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df2save)) {
				computer.setCurrentDrive(2);
				applyAlwaysOnTop(false);
				saveDsk();
				applyAlwaysOnTop(onTop);
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(screenShot))
				screentimer = 1;
			else if (menuAdd.equals(digi)) {
				Switches.digi = true;
				Switches.digiblaster = true;
				Settings.setBoolean(Settings.DIGIBLASTER, Switches.digiblaster);
				Switches.Printer = false;
				Settings.setBoolean(Settings.PRINTER, false);
				Digiblaster.setState(true);
				Printer.setState(false);
			} else if (menuAdd.equals(digimc)) {
				Switches.digimc = true;
			} else if (menuAdd.equals(digipg)) {
				Switches.digipg = true;
			} else if (menuAdd.equals(catchSCR))
				Switches.saveScr = true;
			if (menuAdd.equals(df3save)) {
				computer.setCurrentDrive(3);
				applyAlwaysOnTop(false);
				saveDsk();
				applyAlwaysOnTop(onTop);
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals("Merge DSK's...")) {
				applyAlwaysOnTop(false);
				dskmerge();
				applyAlwaysOnTop(onTop);
			} else if (menuAdd.equals("Autotype")) {
				applyAlwaysOnTop(false);
				Autotype.typeconsole.setVisible(true);
				// new Autotype();
				applyAlwaysOnTop(onTop);
			} else if (menuAdd.equals("Paste      F11")) {
				Autotype.PasteText();
			} else if (menuAdd.equals(tapeopen)) {
				jemu.system.cpc.CPC.TapeDrive.setVisible(true);
				jemu.system.cpc.CPC.tapedeck = true;
			} else if (menuAdd.equals(tapeLoad)) {
				loadTape(false);
			} else if (menuAdd.equals(ejectTape)) {
				tapeEject();
			} else if (menuAdd.equals(tapeLaunch)) {
				loadTape(true);
			} else if (menuAdd.equals(tapeSave)) {
				computer.tape_WAV_save();
			} else if (menuAdd.equals(tapeOptimize)) {
				jemu.system.cpc.CPC.doOptimize = true;
			} else if (menuAdd.equals(tapeConvert)) {
				computer.CDT2WAV();
			} else if (menuAdd.equals(makCDT)) {
				makeCDT.makeCDT();
			} else if (menuAdd.equals(wav2cdt)) {
				samp2cdt.samp2CDT();
			} else if (menuAdd.equals(df0insert)) {
				Switches.askDrive = false;
				computer.setCurrentDrive(0);
				loaddata();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df1insert)) {
				Switches.askDrive = false;
				computer.setCurrentDrive(1);
				loaddata();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df2insert)) {
				Switches.askDrive = false;
				computer.setCurrentDrive(2);
				loaddata();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df3insert)) {
				Switches.askDrive = false;
				computer.setCurrentDrive(3);
				loaddata();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df0eject)) {
				computer.setCurrentDrive(0);
				mediumeject();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df1eject)) {
				computer.setCurrentDrive(1);
				mediumeject();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df2eject)) {
				computer.setCurrentDrive(2);
				mediumeject();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df3eject)) {
				computer.setCurrentDrive(3);
				mediumeject();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df0create)) {
				computer.setCurrentDrive(0);
				CreateDsk();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df1create)) {
				computer.setCurrentDrive(1);
				CreateDsk();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df2create)) {
				computer.setCurrentDrive(2);
				CreateDsk();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df3create)) {
				computer.setCurrentDrive(3);
				CreateDsk();
				computer.setCurrentDrive(0);
			} else if (menuAdd.equals(df0boot)) {
				computer.bootDisk();
			} else if (menuAdd.equals(df1boot)) {
				computer.bootDiskb();
			} else if (menuAdd.equals(Aoptions)) {
				optionPanel.OptionPanel();
			} else if (menuAdd.equals(Oprinter)) {
				CPCPrinter.MakeVisible();
			} else if (menuAdd.equals(loadTXT))
				Autotype.loadFile();
			else if (menuAdd.equals(Expansioninfo)) {
				ExpansionInfo();
			} else if (menuAdd.equals(HexEdit))
				hexi = new HexEditor();
			else if (menuAdd.equals(snapshot))
				chooseSNA();
			else if (menuAdd.equals(binary))
				Switches.BINImport = true;
			else if (menuAdd.equals(export))
				Switches.export = true;
			else if (menuAdd.equals(cpcpalette))
				Switches.showPalette = true;
			else if (menuAdd.equals(poke))
				Switches.poke = true;
			else if (menuAdd.equals(ENkeys)) {
				keys = "English keyboard  ";
				localkeys = "EN_EN";
				Keys.setLabel(keys);
			} else if (menuAdd.equals(DEkeys)) {
				keys = "German keyboard  ";
				localkeys = "DE_DE";
				Keys.setLabel(keys);
			} else if (menuAdd.equals(ESkeys)) {
				keys = "Spanish keyboard  ";
				localkeys = "ES_ES";
				Keys.setLabel(keys);
			}
		}
	}

	public void changeMem() {
		Switches.Memory = Settings.get(Settings.MEMORY, Switches.Memory);
		String oldSys = getComputer().getName();
		try {
			setComputer(oldSys);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		LoadFiles();
	}

	public void itemStateChanged(final ItemEvent e) {
		if (e.getSource() == debugthis) {
			debugthis.setSelected(false);
			showDebugger();
		}
		if (e.getSource() == doupdate) {
			update();
			doupdate.setSelected(false);
		}
		if (e.getSource() == autocheck) {
			Settings.setBoolean(Settings.AUTOCHECK, autocheck.getState());
			checkupdate = autocheck.getState();
		}
		if (e.getSource() == cname0) {
			cname0.setState(true);
			cname1.setState(false);
			cname2.setState(false);
			cname3.setState(false);
			cname4.setState(false);
			cname5.setState(false);
			cname6.setState(false);
			cname7.setState(false);
			reset();
			Switches.computername = 0;
			Settings.set(Settings.COMPUTERNAME, "0");
		}
		if (e.getSource() == cname1) {
			cname0.setState(false);
			cname1.setState(true);
			cname2.setState(false);
			cname3.setState(false);
			cname4.setState(false);
			cname5.setState(false);
			cname6.setState(false);
			cname7.setState(false);
			reset();
			Switches.computername = 1;
			Settings.set(Settings.COMPUTERNAME, "1");
		}
		if (e.getSource() == cname2) {
			cname0.setState(false);
			cname1.setState(false);
			cname2.setState(true);
			cname3.setState(false);
			cname4.setState(false);
			cname5.setState(false);
			cname6.setState(false);
			cname7.setState(false);
			reset();
			Switches.computername = 2;
			Settings.set(Settings.COMPUTERNAME, "2");
		}
		if (e.getSource() == cname3) {
			cname0.setState(false);
			cname1.setState(false);
			cname2.setState(false);
			cname3.setState(true);
			cname4.setState(false);
			cname5.setState(false);
			cname6.setState(false);
			cname7.setState(false);
			reset();
			Switches.computername = 3;
			Settings.set(Settings.COMPUTERNAME, "3");
		}
		if (e.getSource() == cname4) {
			cname0.setState(false);
			cname1.setState(false);
			cname2.setState(false);
			cname3.setState(false);
			cname4.setState(true);
			cname5.setState(false);
			cname6.setState(false);
			cname7.setState(false);
			reset();
			Switches.computername = 4;
			Settings.set(Settings.COMPUTERNAME, "4");
		}
		if (e.getSource() == cname5) {
			cname0.setState(false);
			cname1.setState(false);
			cname2.setState(false);
			cname3.setState(false);
			cname4.setState(false);
			cname5.setState(true);
			cname6.setState(false);
			cname7.setState(false);
			reset();
			Switches.computername = 5;
			Settings.set(Settings.COMPUTERNAME, "5");
		}
		if (e.getSource() == cname6) {
			cname0.setState(false);
			cname1.setState(false);
			cname2.setState(false);
			cname3.setState(false);
			cname4.setState(false);
			cname5.setState(false);
			cname6.setState(true);
			cname7.setState(false);
			reset();
			Switches.computername = 6;
			Settings.set(Settings.COMPUTERNAME, "6");
		}
		if (e.getSource() == cname7) {
			cname0.setState(false);
			cname1.setState(false);
			cname2.setState(false);
			cname3.setState(false);
			cname4.setState(false);
			cname5.setState(false);
			cname6.setState(false);
			cname7.setState(true);
			reset();
			Switches.computername = 7;
			Settings.set(Settings.COMPUTERNAME, "7");
		}
		if (e.getSource() == memory1) {
			memory1.setState(true);
			memory2.setState(false);
			memory3.setState(false);
			memory4.setState(false);
			memory5.setState(false);
			memory6.setState(false);
			Settings.set(Settings.MEMORY, "TYPE_64K");
			changeMem();
		}
		if (e.getSource() == memory2) {
			memory1.setState(false);
			memory2.setState(true);
			memory3.setState(false);
			memory4.setState(false);
			memory5.setState(false);
			memory6.setState(false);
			Settings.set(Settings.MEMORY, "TYPE_128K");
			changeMem();
		}
		if (e.getSource() == memory3) {
			memory1.setState(false);
			memory2.setState(false);
			memory3.setState(true);
			memory4.setState(false);
			memory5.setState(false);
			memory6.setState(false);
			Settings.set(Settings.MEMORY, "TYPE_256K");
			changeMem();
		}
		if (e.getSource() == memory4) {
			memory1.setState(false);
			memory2.setState(false);
			memory3.setState(false);
			memory4.setState(true);
			memory5.setState(false);
			memory6.setState(false);
			Settings.set(Settings.MEMORY, "TYPE_512K");
			changeMem();
		}
		if (e.getSource() == memory5) {
			memory1.setState(false);
			memory2.setState(false);
			memory3.setState(false);
			memory4.setState(false);
			memory5.setState(true);
			memory6.setState(false);
			Settings.set(Settings.MEMORY, "TYPE_SILICON_DISC");
			changeMem();
		}
		if (e.getSource() == memory6) {
			memory1.setState(false);
			memory2.setState(false);
			memory3.setState(false);
			memory4.setState(false);
			memory5.setState(false);
			memory6.setState(true);
			Settings.set(Settings.MEMORY, "TYPE_128_SILICON_DISC");
			changeMem();
		}
		if (e.getSource() == autofire) {
			Switches.autofire = autofire.getState();
		}

		if (e.getSource() == lightgun) {
			Switches.lightGun = lightgun.getState();
			display.setCursor();
		}
		if (e.getSource() == keepprop) {
			keepDimension = keepprop.getState();
			Settings.setBoolean(Settings.DIMENSION, keepDimension);
		}
		if (e.getSource() == RecKey) {
			KeyRec = RecKey.getState();
			Settings.setBoolean(Settings.KEYREC, KeyRec);
		}
		if (e.getSource() == overrideP) {
			Switches.overrideP = overrideP.getState();
			Settings.setBoolean(Settings.OVERRIDEP, Switches.overrideP);
		}
		if (e.getSource() == showDrive) {
			// display.showDrive = showDrive.getState();
			if (showDrive.getState())
				virtualDrive.Show();
			else
				virtualDrive.Hide();
			Settings.setBoolean(Settings.SHOWDRIVE, showDrive.getState());
		}
		if (e.getSource() == head0L) {
			head0L.setState(true);
			head0U.setState(false);
			Settings.setBoolean(Settings.DF0HEAD, false);
			setFloppyHead(0, 0);
		}
		if (e.getSource() == head0U) {
			head0L.setState(false);
			head0U.setState(true);
			Settings.setBoolean(Settings.DF0HEAD, true);
			setFloppyHead(0, 1);
		}
		if (e.getSource() == head1L) {
			head1L.setState(true);
			head1U.setState(false);
			Settings.setBoolean(Settings.DF1HEAD, false);
			setFloppyHead(1, 0);
		}
		if (e.getSource() == head1U) {
			head1L.setState(false);
			head1U.setState(true);
			Settings.setBoolean(Settings.DF1HEAD, true);
			setFloppyHead(1, 1);
		}
		if (e.getSource() == changePolarity) {
			Switches.changePolarity = changePolarity.getState();
			Settings.setBoolean(Settings.POLARITY, Switches.changePolarity);
		}
		if (e.getSource() == crtc0) {
			Switches.CRTC = 0;
			crtc0.setState(true);
			crtc1.setState(false);
			Settings.set(Settings.CRTC, "0");
		}
		if (e.getSource() == crtc1) {
			Switches.CRTC = 1;
			crtc0.setState(false);
			crtc1.setState(true);
			Settings.set(Settings.CRTC, "1");
		}

		if (e.getSource() == lowsys) {
			Settings.setBoolean(Settings.LOWPERFORMANCE, lowsys.getState());
			Display.lowperformance = lowsys.getState();
			display.changePerformance();
		}
		if (e.getSource() == checkTurbo) {
			System.out.println("Turbo is set " + checkTurbo.getState());
			setTurbo(checkTurbo.getState());
		} else if (e.getSource() == shouldBoot) {
			jemu.system.cpc.CPC.shouldBoot = shouldBoot.getState();
		} else if (e.getSource() == bypass) {
			jemu.system.cpc.CPC.Bypass = bypass.getState();
		} else if (e.getSource() == border) {
			jemu.system.cpc.CPC.changeBorder = border.getState();
		} else if (e.getSource() == intack) {
			Switches.doIntack = intack.getState();
			Settings.setBoolean(Settings.INTACK, intack.getState());
		} else if (e.getSource() == UseGzip) {
			if (UseGzip.getState())
				Switches.uncompressed = false;
			else
				Switches.uncompressed = true;
			Settings.setBoolean(Settings.GZIP, UseGzip.getState());
		} else if (e.getSource() == recrateA) {
			Settings.setBoolean(Settings.KHZ44, true);
			Settings.setBoolean(Settings.KHZ11, false);
			Switches.khz44 = true;
			Switches.khz11 = false;
			recrateA.setState(true);
			recrateB.setState(false);
			recrateC.setState(false);
		} else if (e.getSource() == recrateB) {
			Settings.setBoolean(Settings.KHZ44, false);
			Settings.setBoolean(Settings.KHZ11, false);
			Switches.khz44 = false;
			Switches.khz11 = false;
			recrateA.setState(false);
			recrateB.setState(true);
			recrateC.setState(false);
		} else if (e.getSource() == recrateC) {
			Settings.setBoolean(Settings.KHZ44, false);
			Settings.setBoolean(Settings.KHZ11, true);
			Switches.khz44 = false;
			Switches.khz11 = true;
			recrateA.setState(false);
			recrateB.setState(false);
			recrateC.setState(true);
		} else if (e.getSource() == autosavedsk) {
			Switches.autosave = autosavedsk.getState();
			Settings.setBoolean(Settings.AUTOSAVE, autosavedsk.getState());
			Switches.checksave = false;
			Settings.setBoolean(Settings.CHECKSAVE, false);
			checksave.setState(false);

		} else if (e.getSource() == checksave) {
			Switches.checksave = checksave.getState();
			Settings.setBoolean(Settings.CHECKSAVE, checksave.getState());
			Switches.autosave = false;
			Settings.setBoolean(Settings.AUTOSAVE, false);
			autosavedsk.setState(false);
		} else if (e.getSource() == checkrename) {
			Switches.neverOverwrite = checkrename.getState();
			Settings.setBoolean(Settings.CHECKRENAME, checkrename.getState());
		} else if (e.getSource() == aontop) {
			setAlwaysOnTop(aontop.getState());
		} else if (e.getSource() == joyemu) {
			Settings.setBoolean(Settings.JOYSTICK, joyemu.getState());
			qaop.setEnabled(joyemu.getState());
			if (joyemu.getState())
				Switches.joystick = 1;
			else
				Switches.joystick = 0;
		} else if (e.getSource() == qaop) {
			Settings.setBoolean(Settings.NOTEBOOK, qaop.getState());
			Switches.notebook = qaop.getState();
		} else if (e.getSource() == osdisplay) {
			osd = osdisplay.getState();
			Switches.osddisplay = osdisplay.getState();
			Settings.setBoolean(Settings.OSD, osdisplay.getState());
		} else

		if (e.getSource() == cpc464t) {
			cpc464t.setState(true);
			cpc464.setState(false);
			cpc664.setState(false);
			cpc6128.setState(false);
			symbos.setState(false);
			futureos.setState(false);
			kccompact.setState(false);
			customcpc.setState(false);
			try {
				setComputer("CPC464T");
				// reset();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			LoadFiles();
		} else if (e.getSource() == cpc464) {
			cpc464t.setState(false);
			cpc464.setState(true);
			cpc664.setState(false);
			cpc6128.setState(false);
			symbos.setState(false);
			futureos.setState(false);
			kccompact.setState(false);
			customcpc.setState(false);
			try {
				setComputer("CPC464");
				// reset();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			LoadFiles();
		} else if (e.getSource() == cpc664) {
			cpc464t.setState(false);
			cpc464.setState(false);
			cpc664.setState(true);
			cpc6128.setState(false);
			symbos.setState(false);
			futureos.setState(false);
			kccompact.setState(false);
			customcpc.setState(false);
			try {
				setComputer("CPC664");
				// reset();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			LoadFiles();
		} else if (e.getSource() == symbos) {
			cpc464t.setState(false);
			cpc464.setState(false);
			cpc664.setState(false);
			cpc6128.setState(false);
			symbos.setState(true);
			futureos.setState(false);
			kccompact.setState(false);
			customcpc.setState(false);
			try {
				setComputer("SymbOS");
				// reset();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			LoadFiles();
		} else if (e.getSource() == futureos) {
			cpc464t.setState(false);
			cpc464.setState(false);
			cpc664.setState(false);
			cpc6128.setState(false);
			symbos.setState(false);
			futureos.setState(true);
			kccompact.setState(false);
			customcpc.setState(false);
			try {
				setComputer("FutureOS");
				// reset();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			LoadFiles();
		} else if (e.getSource() == kccompact) {
			cpc464t.setState(false);
			cpc464.setState(false);
			cpc664.setState(false);
			cpc6128.setState(false);
			symbos.setState(false);
			futureos.setState(false);
			kccompact.setState(true);
			customcpc.setState(false);
			try {
				setComputer("KCcomp");
				// reset();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			LoadFiles();
		} else if (e.getSource() == cpc6128) {
			cpc464t.setState(false);
			cpc464.setState(false);
			cpc664.setState(false);
			cpc6128.setState(true);
			symbos.setState(false);
			futureos.setState(false);
			kccompact.setState(false);
			customcpc.setState(false);
			try {
				setComputer("CPC6128");
				// reset();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			LoadFiles();
		} else if (e.getSource() == customcpc) {
			cpc464t.setState(false);
			cpc464.setState(false);
			cpc664.setState(false);
			cpc6128.setState(false);
			customcpc.setState(true);
			symbos.setState(false);
			futureos.setState(false);
			kccompact.setState(false);
			try {
				setComputer("CUSTOM");
				// reset();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			LoadFiles();
		} else if (e.getSource() == checkColor) {
			changeMonitorModeToColour();
		} else if (e.getSource() == checkGreen) {
			changeMonitorModeToGreen();
		} else if (e.getSource() == checkGrey) {
			changeMonitorModeToGray();
		} else if (e.getSource() == checkJColor) {
			changeMonitorModeToColour2();
		}
		if (e.getSource() == checkFPS) {
			if (checkFPS.getState() == true)
				Display.showfps = 1;
			else
				Display.showfps = 0;

		}

		else if (e.getSource() == checkAudio) {
			if (checkAudio.getState()) {
				Switches.audioenabler = 1;
				Settings.setBoolean(Settings.AUDIO, true);
			} else {
				Switches.audioenabler = 0;
				Settings.setBoolean(Settings.AUDIO, false);
			}
		}

		else if (e.getSource() == checkFull) {
			togglesize = checkFull.getState();
			FullSize();
		}

		else if (e.getSource() == checkSimple)
			setSimpleSized();
		else if (e.getSource() == checkDouble)
			setDoubleSized(true);
		else if (e.getSource() == checkTriple)
			setTripleSized(true);

		else if (e.getSource() == checkGate) {
			setFullSized(true);
		} else if (e.getSource() == checkScan) {
			ScanLines();
		} else if (e.getSource() == checkScan) {
			ScanLines();
		} else if (e.getSource() == checkScaneff) {
			ScanLinesEffect();
		}
		if (e.getSource() == checkbilinear) {
			Bilinear();
		} else if (e.getSource() == joymouse) {
			if (mousejoy) {
				Switches.MouseJoy = false;
				Settings.setBoolean(Settings.MOUSEJOY, false);
				mousejoy = false;
			} else {
				Switches.MouseJoy = true;
				Settings.setBoolean(Settings.MOUSEJOY, true);
				mousejoy = true;
			}
		} else

		if (e.getSource() == drivesound) {
			fsoundcheck();
		} else

		if (e.getSource() == unprotect) {
			Switches.unprotect = unprotect.getState();
		}
		if (e.getSource() == Printer) {
			Switches.Printer = Printer.getState();
			Settings.setBoolean(Settings.PRINTER, Printer.getState());
			Switches.digiblaster = false;
			Settings.setBoolean(Settings.DIGIBLASTER, false);
			Digiblaster.setState(false);
		}
		if (e.getSource() == Expansion) {
			Switches.Expansion = Expansion.getState();
			Settings.setBoolean(Settings.EXPANSION, Expansion.getState());
			ExpansionRom();
		} else if (e.getSource() == Digiblaster) {
			Switches.digiblaster = Digiblaster.getState();
			Settings.setBoolean(Settings.DIGIBLASTER, Digiblaster.getState());
			Switches.Printer = false;
			Settings.setBoolean(Settings.PRINTER, false);
			Printer.setState(false);
		} else if (e.getSource() == floppyturbo) {
			Switches.floppyturbo = floppyturbo.getState();
			Settings.setBoolean(Settings.FLOPPYTURBO, floppyturbo.getState());
			checkTurbo.setState(false);
		}

		/*
		 * else { if (hideframe == true) { if (e.getStateChange() == ItemEvent.SELECTED && this.gotGames) { final Object
		 * item = ((JComboBox) e.getSource()).getSelectedItem(); /* if (e.getSource() == this.jcbImage) {
		 * loadFile(((FileDescriptor) item).filename); }else
		 */
		/*
		 * if (e.getSource() == this.jcbDrive) { computer.setCurrentDrive(this.jcbDrive.getSelectedIndex()); final
		 * Vector devices = computer.getDevices(); final int n = devices.size(); for (int i = 0; i < n; i++) { final
		 * Object device = devices.get(i); if (device instanceof UPD765A) { final UPD765A FDC = (UPD765A) device; final
		 * int forcedHead = FDC.getForcedHead(this.jcbDrive.getSelectedIndex());
		 * this.driveHead.setSelectedIndex(forcedHead); } } } else if (e.getSource() == this.driveHead) { final Vector
		 * devices = computer.getDevices(); final int n = devices.size(); for (int i = 0; i < n; i++) { final Object
		 * device = devices.get(i); if (device instanceof UPD765A) { final UPD765A FDC = (UPD765A) device;
		 * FDC.setForcedHead(this.driveHead.getSelectedIndex(), this.jcbDrive.getSelectedIndex()); } } } else { try {
		 * setComputer(((ComputerDescriptor)item).content); findWindow(this).pack(); LoadFiles(); } catch(Exception ex)
		 * { ex.printStackTrace(); } } } } }
		 */
	}

	public void changeMonitorModeToColour() {
		Switches.monitormode = 0;
		Display.monmessage = "Colour monitor";
		Settings.set(Settings.MONITOR, Settings.MONITOR_COLOUR);
		Display.showmon = 150;
		checkColor.setState(true);
		checkGreen.setState(false);
		checkGrey.setState(false);
		checkJColor.setState(false);
		display.changePerformance();
		jemu.system.cpc.CPC.resetInk = 1;
	}

	public void changeMonitorModeToGreen() {
		Switches.monitormode = 2;
		Display.monmessage = "Green monitor";
		Settings.set(Settings.MONITOR, Settings.MONITOR_GREEN);
		Display.showmon = 150;
		checkColor.setState(false);
		checkGreen.setState(true);
		checkGrey.setState(false);
		checkJColor.setState(false);
		display.changePerformance();
		jemu.system.cpc.CPC.resetInk = 1;
	}

	public void changeMonitorModeToGray() {
		Switches.monitormode = 3;
		Display.monmessage = "Grey monitor";
		Settings.set(Settings.MONITOR, Settings.MONITOR_GRAY);
		Display.showmon = 150;
		checkColor.setState(false);
		checkGreen.setState(false);
		checkGrey.setState(true);
		checkJColor.setState(false);
		display.changePerformance();
		jemu.system.cpc.CPC.resetInk = 1;
	}

	public void changeMonitorModeToColour2() {
		Switches.monitormode = 1;
		Display.monmessage = "2nd Colorset";
		Settings.set(Settings.MONITOR, Settings.MONITOR_COLOUR2);
		Display.showmon = 150;
		checkColor.setState(false);
		checkGreen.setState(false);
		checkGrey.setState(false);
		checkJColor.setState(true);
		display.changePerformance();
		jemu.system.cpc.CPC.resetInk = 1;
	}

	public void saveDsk() {
		applyAlwaysOnTop(false);
		FileDialog filedia = getFrameAdapter().createFileDialog("Save DSK...", FileDialog.SAVE);
		filedia.setFile("*.dsk");
		filedia.setVisible(true);
		String filename = filedia.getFile();
		if (filename != null) {
			filename = filedia.getDirectory() + filedia.getFile();
			String savename = filename;
			if (!savename.toLowerCase().endsWith(".dsk"))
				savename = savename + ".dsk";
			File savefile = new File(savename);
			// saveauto disc image
			final Vector devices = computer.getDevices();
			final int n = devices.size();
			for (int i = 0; i < n; i++) {
				final Object device = devices.get(i);
				if (device instanceof UPD765A) {
					final UPD765A FDC = (UPD765A) device;
					Drive diskdrive = FDC.getDrive(computer.getCurrentDrive());
					// DiscImage imager = diskdrive.getDisc(this.driveHead.getSelectedIndex());
					DiscImage imager = diskdrive.getDisc(FDC.getForcedHead(computer.getCurrentDrive()));
					imager.saveImage(savefile);
					String insertdisk = savename;
					loadFile(Computer.TYPE_SNAPSHOT, insertdisk, false);
					System.out.println("loaded " + insertdisk);
					applyAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null, "Sucessfully saved as " + filedia.getFile());
					applyAlwaysOnTop(onTop);
				}
			}
		}
	}

	public void driveActiveChanged(Drive drive, boolean active) {
		// Put Drive LED change code here
		// NOTE: Must OR the active state from all Drives for the value
		if (!active) {
			Drive[] floppies = computer.getFloppyDrives();
			if (floppies != null)
				for (int i = 0; i < floppies.length; i++)
					if (floppies[i] != null && floppies[i].isActive()) {
						active = true;
						break;
					}
		}
		Display.ledOn = active;
		if (Switches.floppyturbo)
			checkTurbo.setState(false);
	}

	public void message() {
		if (!showabout) {
			showabout = true;

			about = new JFrame("About JavaCPC") {
				protected void processWindowEvent(WindowEvent we) {
					super.processWindowEvent(we);
					if (we.getID() == WindowEvent.WINDOW_CLOSING) {
						showabout = false;
					}
				}
			};
			okay.setBorder(null);
			about.setLayout(new GridBagLayout());
			JJemu.setBorder(null);
			about.add(JJemu, getGridBagConstraints(1, 0, 1.0, 1.0, 2, GridBagConstraints.CENTER));
			about.add(okay, getGridBagConstraints(2, 1, 1.0, 1.0, 1, GridBagConstraints.EAST));

			about.setResizable(false);
			about.pack();
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			about.setLocation((d.width - about.getSize().width) / 2, (d.height - about.getSize().height) / 2);
			about.setVisible(true);
			about.setAlwaysOnTop(true);
			Settings.setBoolean(Settings.MESSAGE, false);
		}
	}

	// Standalone
	public void Size() {
		getFrameAdapter().dispose();
		getFrameAdapter().setUndecorated(false);

		computer.stop();
		Switches.stretch = false;
		System.out.println("Resizing Window...");
		if (large == true) {
			large = false;
			setFullSize(large);
			Settings.setBoolean(Settings.LARGE, false);
		} else if (large == false) {
			large = true;
			setFullSize(large);
			Settings.setBoolean(Settings.LARGE, true);
		}

		if (large) {
			System.out.println("Double size - Gare Array is double");
			Display.automessage = "Double size - Gare Array is double";
			Display.showauto = 150;
		} else {

			System.out.println("Simple size - Gare Array is half");
			Display.automessage = "Simple size - Gare Array is half";
			Display.showauto = 150;
		}
		getFrameAdapter().pack();
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		getFrameAdapter().setLocation(screenXstored, screenYstored);
		// getFrameAdapter().setLocation((d.width - getFrameAdapter().getSize().width) / 2, (d.height -
		// getFrameAdapter().getSize().height) / 2);
		winx1 = getFrameAdapter().getSize().width;
		winy1 = getFrameAdapter().getSize().height;

		System.out.println("Window is " + winx1 + " pixels wide & " + winy1 + " pixels high");
		computer.start();
		System.out.println("You can stretch the window now.");
		togglesize = true;
		stretcher = false;
		defaultSize();
		getFrameAdapter().setVisible(true);
		this.display.requestFocus();
	}

	public void Turbocheck() {
		if (Switches.turbo == 1) {
			Switches.turbo = 4;
			Display.automessage = "JavaCPC is running at 300% CPU-Speed!";
			Display.showauto = 150;
		} else {
			Switches.turbo = 1;
			Display.automessage = "JavaCPC is running at 100% CPU-Speed.";
			Display.showauto = 150;
		}
	}

	public void FullSize() {
		Switches.stretch = false;
		final boolean running = computer.isRunning();
		computer.stop();
		if (togglesize) {
			if (skinned) {
				Moniup.setVisible(false);
				Monidown.setVisible(false);
				Monileft.setVisible(false);
				Moniright.setVisible(false);
			}
			getFrameAdapter().dispose();
			getFrameAdapter().removeMenuBar(JavaCPCMenu);
			getFrameAdapter().setUndecorated(true);
			togglesize = false;
			fullscreen = true;
			Settings.setBoolean(Settings.FULLSCREEN, true);
		} else {
			if (skinned) {
				if (!large) {
					Moniup.setVisible(true);
					Monidown.setVisible(true);
					Monileft.setVisible(true);
					Moniright.setVisible(true);
				}
			}
			getFrameAdapter().dispose();
			getFrameAdapter().setUndecorated(false);
			togglesize = true;
			fullscreen = false;
			Settings.setBoolean(Settings.FULLSCREEN, false);

		}
		getFrameAdapter().setVisible(true);
		setOutputSize();
		checkFull.setState(fullscreen);
		this.display.requestFocus();
		if (running)
			computer.start();
		computer.reSync();
	}

	public void defaultSize() {
		if (stretcher)
			Switches.stretch = true;
		stretcher = true;
		computer.stop();
		getFrameAdapter().dispose();
		getFrameAdapter().setUndecorated(false);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		getFrameAdapter().setSize(winx1, winy1);
		togglesize = true;
		Settings.setBoolean(Settings.FULLSCREEN, false);
		getFrameAdapter().pack();
		getFrameAdapter().setVisible(true);

		computer.start();
		this.display.requestFocus();
		Switches.stretch = false;
		getFrameAdapter().dispose();
		getFrameAdapter().pack();
		getFrameAdapter().setLocation(screenXstored, screenYstored);
		// getFrameAdapter().setLocation((d.width - getFrameAdapter().getSize().width) / 2, (d.height -
		// getFrameAdapter().getSize().height) / 2);
		getFrameAdapter().setVisible(true);
		computer.reSync();
	}

	//
	// Settings for new menu
	//

	Frame onlinemenu = new Frame() {

		protected void processWindowEvent(WindowEvent e) {
			super.processWindowEvent(e);
			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				onlinemenu.dispose();
			}
		}

		public synchronized void setTitle(String title) {
			super.setTitle(title);
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		}

	};

	protected boolean showmenu;
	protected boolean showonlinemenu;
	public MenuBar JavaCPCMenu = new MenuBar();
	Menu menue1 = new Menu("File");
	Menu menue2 = new Menu("Emulation");
	Menu menue3 = new Menu("Settings");
	Menu crtc = new Menu("CRTC type");
	Menu menue4 = new Menu("Monitor");
	Menu menue5 = new Menu("Help");
	Menu menue6 = new Menu("Edit");
	Menu compName = new Menu("Computername");
	Menu extra = new Menu("Extras");
	// public static Menu menue7 = new Menu(" ");

	Menu drives = new Menu("Drives");
	Menu dskhandling = new Menu("DSK-Handling");
	Menu df0 = new Menu("DF0:");
	Menu df1 = new Menu("DF1:");
	Menu df2 = new Menu("DF2:");
	Menu df3 = new Menu("DF3:");
	Menu systemmenu = new Menu("CPC models");
	Menu joymenu = new Menu("Joystick");
	Menu keyboard = new Menu("Keyboard");
	Menu sysmem = new Menu("Memory");
	Menu tape = new Menu("Tape");
	CheckboxMenuItem head0L = new CheckboxMenuItem("Select head 0");
	CheckboxMenuItem head0U = new CheckboxMenuItem("Select head 1");
	CheckboxMenuItem head1L = new CheckboxMenuItem("Select head 0");
	CheckboxMenuItem head1U = new CheckboxMenuItem("Select head 1");
	CheckboxMenuItem changePolarity = new CheckboxMenuItem("Change tape-polarity");
	CheckboxMenuItem showDrive = new CheckboxMenuItem("Show virtual floppydrive");
	CheckboxMenuItem overrideP = new CheckboxMenuItem("Override ,P save protection");
	CheckboxMenuItem crtc0 = new CheckboxMenuItem("CRTC type 0");
	CheckboxMenuItem crtc1 = new CheckboxMenuItem("CRTC type 1");
	CheckboxMenuItem keepprop = new CheckboxMenuItem("Keep display proportions");
	CheckboxMenuItem lightgun = new CheckboxMenuItem("Enable Lightgun");
	CheckboxMenuItem memory1 = new CheckboxMenuItem("64K");
	CheckboxMenuItem memory2 = new CheckboxMenuItem("128K");
	CheckboxMenuItem memory3 = new CheckboxMenuItem("64K + 256K Ram expansion");
	CheckboxMenuItem memory4 = new CheckboxMenuItem("64K + 512K Ram expansion");
	CheckboxMenuItem memory5 = new CheckboxMenuItem("64K + 256K Silicon Disc");
	CheckboxMenuItem memory6 = new CheckboxMenuItem("128K + 256K Silicon Disc");
	CheckboxMenuItem autofire = new CheckboxMenuItem("Autofire");
	CheckboxMenuItem lowsys = new CheckboxMenuItem("Low performance PC");
	CheckboxMenuItem shouldBoot = new CheckboxMenuItem("Try to boot game");
	CheckboxMenuItem bypass = new CheckboxMenuItem("Bypass only Data sound");
	CheckboxMenuItem border = new CheckboxMenuItem("Always show stripes on border");
	CheckboxMenuItem intack = new CheckboxMenuItem("Do extra cycles (Z80)");
	CheckboxMenuItem UseGzip = new CheckboxMenuItem("Use GZip compression DSK, SNA, WAV");
	String ENkeys = "Force English keyboard";
	String DEkeys = "Force German keyboard";
	String ESkeys = "Force Spanish keyboard";
	String reportBug = "Report a bug";

	Menu Keys = new Menu(keys);
	CheckboxMenuItem RecKey = new CheckboxMenuItem("Enable keyboard recording");
	String recordkeys = "Keyboard recording info";

	String extra1 = "Play games from cpcgamereviews.com";
	String extra2 = "Play games from JavaCPC games archive";
	String extra3 = "Play games from Homecomputerworld.com";
	String tapeopen = "Show TapeDeck";
	String tapeLoad = "Insert Tape";
	String ejectTape = "Eject Tape";
	String tapeLaunch = "Launch Tape";
	String tapeSave = "Save Tape";
	String tapeOptimize = "Optimize Tape";
	String tapeConvert = "Convert CDT to WAV";
	CheckboxMenuItem recrateA = new CheckboxMenuItem("Use 44.1 khz rate (~13 min.) (best, works with CPCTapeXP!)");
	CheckboxMenuItem recrateB = new CheckboxMenuItem("Use 22.0 khz rate (~25 min.) (Standard quality)");
	CheckboxMenuItem recrateC = new CheckboxMenuItem("Use 11.5 khz rate (~50 min.) (Poor quality, bad results)");
	String df0insert = "Insert";
	String df1insert = "Insert ";
	String df2insert = "Insert  ";
	String df3insert = "Insert    ";
	String df0sav = "Save";
	String df1sav = "Save ";
	String df2sav = "Save  ";
	String df3sav = "Save   ";
	String df0eject = "Eject";
	String df1eject = "Eject ";
	String df2eject = "Eject  ";
	String df3eject = "Eject   ";
	String df0create = "Create DSK";
	String df1create = "Create DSK ";
	String df2create = "Create DSK  ";
	String df3create = "Create DSK   ";
	String df0boot = "Try to boot";
	String df1boot = "Try to boot ";
	String df0save = "Save DSK as...";
	String df1save = "Save DSK as... ";
	String df2save = "Save DSK as...  ";
	String df3save = "Save DSK as...   ";
	String screenShot = "Save Screenshot     Ctrl + F11";
	String catchSCR = "Catch CPC Screen (16k)";
	String Aoptions = "Advanced options     Ctrl + F10";
	String Oprinter = "Open printer console";
	String loadTXT = "Import ASCII file";
	String HexEdit = "Open Hex Editor";
	String snapshot = "Save snapshot file";
	String binary = "Import CPC file";
	String poke = "Poke Memory";
	String export = "Export binary";
	String cpcpalette = "Show actual palette";
	String digi = "Launch Prodatron's Digitracker";
	String digimc = "Launch DigiTracker MOD converter";
	String digipg = "Launch Digitracker Player-Generator";
	String checkup = "Check for update";
	String CPUfollow = "Let disassembler follow CPU";
	String downl = "Visit download-page";
	String homepage = "Visit homepage";
	String sourceforge = "Sourceforge project page";
	String showYM = "Show YM-Control";
	String showRec = "Record audio";
	String showCap = "Record GIF-Sequence";
	// 0 = Isp, 1 = Triumph, 2 = Saisho
	// 3 = Solavox, 4 = Awa, 5 = Schneider
	// 6 = Orion, 7 = Amstrad
	CheckboxMenuItem cname0 = new CheckboxMenuItem("Isp");
	CheckboxMenuItem cname1 = new CheckboxMenuItem("Triumph");
	CheckboxMenuItem cname2 = new CheckboxMenuItem("Saisho");
	CheckboxMenuItem cname3 = new CheckboxMenuItem("Solavox");
	CheckboxMenuItem cname4 = new CheckboxMenuItem("Awa");
	CheckboxMenuItem cname5 = new CheckboxMenuItem("Schneider");
	CheckboxMenuItem cname6 = new CheckboxMenuItem("Orion");
	CheckboxMenuItem cname7 = new CheckboxMenuItem("Amstrad");

	CheckboxMenuItem autocheck = new CheckboxMenuItem("Automatically check for updates");

	CheckboxMenuItem checkTurbo = new CheckboxMenuItem("Turbo");
	CheckboxMenuItem checkColor = new CheckboxMenuItem("CTM644/CM14 - Colour monitor");
	CheckboxMenuItem checkJColor = new CheckboxMenuItem("CTM644/CM14 - 2nd Colorset");
	CheckboxMenuItem checkGreen = new CheckboxMenuItem("GT65 - Green monitor");
	CheckboxMenuItem checkGrey = new CheckboxMenuItem("MM12 - Monochrome monitor");
	CheckboxMenuItem checkFPS = new CheckboxMenuItem("Show FPS");
	CheckboxMenuItem checkAudio = new CheckboxMenuItem("Audio");
	CheckboxMenuItem checkFull = new CheckboxMenuItem("Fullscreen            Alt + Enter");
	CheckboxMenuItem checkSimple = new CheckboxMenuItem("Simple size");
	CheckboxMenuItem checkDouble = new CheckboxMenuItem("Double size");
	CheckboxMenuItem checkTriple = new CheckboxMenuItem("Triple size");
	CheckboxMenuItem checkGate = new CheckboxMenuItem("Full Gatearray");
	CheckboxMenuItem checkScan = new CheckboxMenuItem("Scanlines");
	CheckboxMenuItem checkScaneff = new CheckboxMenuItem("Monitor effect");
	CheckboxMenuItem checkbilinear = new CheckboxMenuItem("Bilinear filter");
	CheckboxMenuItem cpc464t = new CheckboxMenuItem("CPC 464");
	CheckboxMenuItem cpc464 = new CheckboxMenuItem("CPC 464 (Amsdos)");
	CheckboxMenuItem cpc664 = new CheckboxMenuItem("CPC 664");
	CheckboxMenuItem cpc6128 = new CheckboxMenuItem("CPC 6128");
	CheckboxMenuItem symbos = new CheckboxMenuItem("SymbOS");
	CheckboxMenuItem futureos = new CheckboxMenuItem("FutureOS");
	CheckboxMenuItem kccompact = new CheckboxMenuItem("KC compact");
	CheckboxMenuItem customcpc = new CheckboxMenuItem("Custom CPC");
	CheckboxMenuItem osdisplay = new CheckboxMenuItem("OSD");
	CheckboxMenuItem autosavedsk = new CheckboxMenuItem("DSK Autosave");
	CheckboxMenuItem checksave = new CheckboxMenuItem("Ask to save DSK when changed");
	CheckboxMenuItem checkrename = new CheckboxMenuItem("Never overwrite old DSK");
	CheckboxMenuItem joyemu = new CheckboxMenuItem("Joystick");
	CheckboxMenuItem qaop = new CheckboxMenuItem("Alternative control");
	CheckboxMenuItem joymouse = new CheckboxMenuItem("Mousejoystick");
	CheckboxMenuItem aontop = new CheckboxMenuItem("Always on top");
	CheckboxMenuItem drivesound = new CheckboxMenuItem("Drive noises");
	CheckboxMenuItem floppyturbo = new CheckboxMenuItem("Drive turbo");
	CheckboxMenuItem unprotect = new CheckboxMenuItem("Force no write protection");
	CheckboxMenuItem Printer = new CheckboxMenuItem("Enable Printer");
	CheckboxMenuItem Expansion = new CheckboxMenuItem("JavaCPC Expansion ROM");
	CheckboxMenuItem Digiblaster = new CheckboxMenuItem("DigiBlaster output");
	String Expansioninfo = "Expansion Info";

	String makCDT = "Create a CDT";
	String wav2cdt = "Samp2CDT";

	public void SetMenu() {
		// Windows stuff here

		if (isWindows() && executable) {
			extra.add(makCDT);
			extra.add(wav2cdt);
			extra.addSeparator();
		}

		JavaCPCMenu.setFont(new Font("", 0, 11));
		menue1.add("Load...");
		menue1.add("Boot CP/M");
		menue1.add(drives);
		drives.add(df0);
		df0.add(df0insert);
		df0.add(df0boot);
		df0.add(df0eject);
		df0.addSeparator();
		df0.add(head0L);
		df0.add(head0U);
		df0.addSeparator();
		df0.add(df0create);
		df0.add(df0sav);
		df0.add(df0save);
		drives.add(df1);
		df1.add(df1insert);
		df1.add(df1boot);
		df1.add(df1eject);
		df1.addSeparator();
		df1.add(head1L);
		df1.add(head1U);
		df1.addSeparator();
		df1.add(df1create);
		df1.add(df1sav);
		df1.add(df1save);
		drives.addSeparator();
		drives.add(df2);
		df2.add(df2insert);
		df2.add(df2eject);
		df2.addSeparator();
		df2.add(df2create);
		df2.add(df2sav);
		df2.add(df2save);
		drives.add(df3);
		df3.add(df3insert);
		df3.add(df3eject);
		df3.addSeparator();
		df3.add(df3create);
		df3.add(df3sav);
		df3.add(df3save);
		drives.addSeparator();
		drives.add("Eject all");
		menue1.addSeparator();
		menue1.add("Merge DSK's...");
		menue1.addSeparator();
		menue1.add(UseGzip);

		menue1.addSeparator();
		menue1.add(tape);
		tape.add(tapeLoad);
		tape.add(tapeLaunch);
		tape.add(tapeSave);
		tape.add(ejectTape);
		tape.add(tapeOptimize);
		tape.add(tapeConvert);
		tape.add(tapeopen);
		tape.addSeparator();
		tape.add(bypass);
		tape.add(border);
		tape.addSeparator();
		tape.add(recrateA);
		tape.add(recrateB);
		tape.add(recrateC);

		menue1.addSeparator();
		menue1.add(loadTXT);
		menue1.add(binary);
		menue1.add(export);
		menue1.addSeparator();
		menue1.add(snapshot);
		menue1.add(showRec);
		menue1.add(showCap);
		menue1.addSeparator();
		menue1.add("Force quit");
		menue1.add("Quit");
		menue2.add("Pause");
		menue2.add("Reset            Ctrl + F9");
		menue2.add("Reboot");
		menue2.addSeparator();
		menue2.add(systemmenu);
		systemmenu.add(cpc464t);
		systemmenu.add(cpc464);
		systemmenu.add(cpc664);
		systemmenu.add(cpc6128);
		systemmenu.addSeparator();
		systemmenu.add(symbos);
		systemmenu.add(futureos);
		systemmenu.addSeparator();
		systemmenu.add(kccompact);
		systemmenu.addSeparator();
		systemmenu.add(customcpc);

		menue2.addSeparator();
		menue2.add(checkTurbo);
		menue2.add(intack);
		menue2.addSeparator();
		menue2.add(Expansion);
		menue2.add(Expansioninfo);

		menue3.add(checkAudio);
		menue3.addSeparator();
		menue3.add(drivesound);
		menue3.add(floppyturbo);
		menue3.add(overrideP);
		menue3.add(showDrive);
		menue3.add(changePolarity);
		menue3.addSeparator();
		menue3.add(crtc);
		crtc.add(crtc0);
		crtc.add(crtc1);
		menue3.addSeparator();
		menue3.add(sysmem);
		sysmem.add(memory1);
		sysmem.add(memory2);
		sysmem.add(memory3);
		sysmem.add(memory5);
		// sysmem.add(memory6);
		sysmem.add(memory4);
		menue3.addSeparator();
		menue3.add(dskhandling);
		dskhandling.add(autosavedsk);
		dskhandling.add(checkrename);
		dskhandling.add(checksave);
		dskhandling.addSeparator();
		dskhandling.add(unprotect);
		menue3.addSeparator();
		menue3.add(compName);
		compName.add(cname0);
		compName.add(cname1);
		compName.add(cname2);
		compName.add(cname3);
		compName.add(cname4);
		compName.add(cname5);
		compName.add(cname6);
		compName.add(cname7);
		menue3.addSeparator();
		menue3.add("ROM Setup");
		menue3.addSeparator();
		menue3.add(joymenu);
		// joymenu.add(lightgun);
		joymenu.add(joyemu);
		joymenu.add(qaop);
		joymenu.addSeparator();
		joymenu.add(joymouse);
		joymenu.add(autofire);
		menue3.addSeparator();
		menue3.add(keyboard);
		keyboard.add(Keys);
		Keys.add(ENkeys);
		Keys.add(DEkeys);
		Keys.add(ESkeys);
		menue3.addSeparator();
		menue3.add(Printer);
		menue3.add(Digiblaster);
		menue3.addSeparator();
		menue3.add(aontop);
		menue3.addSeparator();
		menue3.add(Aoptions);
		menue3.addSeparator();
		menue3.add(lowsys);
		menue4.add(checkColor);
		menue4.add(checkJColor);
		menue4.add(checkGreen);
		menue4.add(checkGrey);
		menue4.addSeparator();
		menue4.add(checkFull);
		menue4.add(checkSimple);
		menue4.add(checkDouble);
		menue4.add(checkTriple);
		menue4.add(checkGate);
		menue4.addSeparator();
		menue4.add(checkScan);
		menue4.add(checkScaneff);
		menue4.add(checkbilinear);
		menue4.addSeparator();
		menue4.add(keepprop);
		menue4.add(osdisplay);
		menue4.add(checkFPS);
		// menue5.addSeparator();
		menue5.add("Debugger");
		menue5.add(CPUfollow);
		menue5.addSeparator();
		menue5.add("Java console");
		menue5.addSeparator();
		menue5.add(checkup);
		menue5.add(autocheck);
		menue5.addSeparator();
		menue5.add(downl);
		menue5.add(homepage);
		menue5.add(sourceforge);
		menue5.addSeparator();
		menue5.add(reportBug);
		menue5.add("About");

		menue6.add("Autotype");
		menue6.add("Paste      F11");
		menue6.addSeparator();
		menue6.add(RecKey);
		menue6.add(recordkeys);
		menue6.addSeparator();
		menue6.add(poke);
		menue6.addSeparator();
		menue6.add(catchSCR);
		menue6.add(screenShot);
		menue6.addSeparator();
		menue6.add(showYM);
		menue6.addSeparator();
		menue6.add(Oprinter);
		menue6.add(HexEdit);
		menue6.addSeparator();
		menue6.add(cpcpalette);

		extra.add(digi);
		extra.add(digimc);
		extra.add(digipg);
		extra.addSeparator();
		extra.add(extra2);
		extra.add(extra1);
		extra.add(extra3);
		extra.add(shouldBoot);

		JavaCPCMenu.add(menue1);
		JavaCPCMenu.add(menue6);
		JavaCPCMenu.add(menue2);
		JavaCPCMenu.add(menue3);
		JavaCPCMenu.add(menue4);
		JavaCPCMenu.add(extra);
		JavaCPCMenu.add(menue5);

		crtc.addActionListener(this);
		extra.addActionListener(this);
		menue1.addActionListener(this);
		menue2.addActionListener(this);
		menue3.addActionListener(this);
		menue4.addActionListener(this);
		menue5.addActionListener(this);
		menue6.addActionListener(this);
		// menue7.setFont(new Font("Arial",0,9));
		systemmenu.addActionListener(this);
		compName.addActionListener(this);
		cname0.addItemListener(this);
		cname1.addItemListener(this);
		cname2.addItemListener(this);
		cname3.addItemListener(this);
		cname4.addItemListener(this);
		cname5.addItemListener(this);
		cname6.addItemListener(this);
		cname7.addItemListener(this);
		tape.addActionListener(this);

		autocheck.addItemListener(this);

		df0.addActionListener(this);
		df1.addActionListener(this);
		df2.addActionListener(this);
		df3.addActionListener(this);
		dskhandling.addActionListener(this);
		joymenu.addActionListener(this);
		drives.addActionListener(this);
		keyboard.addActionListener(this);
		sysmem.addActionListener(this);
		Keys.addActionListener(this);

		head0L.addItemListener(this);
		head0U.addItemListener(this);
		head1L.addItemListener(this);
		head1U.addItemListener(this);
		changePolarity.addItemListener(this);
		showDrive.addItemListener(this);
		overrideP.addItemListener(this);
		crtc0.addItemListener(this);
		crtc1.addItemListener(this);
		keepprop.addItemListener(this);
		lightgun.addItemListener(this);
		memory1.addItemListener(this);
		memory2.addItemListener(this);
		memory3.addItemListener(this);
		memory4.addItemListener(this);
		memory5.addItemListener(this);
		memory6.addItemListener(this);
		autofire.addItemListener(this);
		lowsys.addItemListener(this);

		RecKey.addItemListener(this);
		shouldBoot.addItemListener(this);
		bypass.addItemListener(this);
		border.addItemListener(this);
		intack.addItemListener(this);
		UseGzip.addItemListener(this);
		recrateA.addItemListener(this);
		recrateB.addItemListener(this);
		recrateC.addItemListener(this);
		checkTurbo.addItemListener(this);
		checkColor.addItemListener(this);
		checkGreen.addItemListener(this);
		checkGrey.addItemListener(this);
		checkJColor.addItemListener(this);
		checkFPS.addItemListener(this);
		checkAudio.addItemListener(this);
		checkFull.addItemListener(this);
		checkSimple.addItemListener(this);
		checkDouble.addItemListener(this);
		checkTriple.addItemListener(this);
		checkGate.addItemListener(this);
		checkScan.addItemListener(this);
		checkScaneff.addItemListener(this);
		checkbilinear.addItemListener(this);
		cpc464t.addItemListener(this);
		cpc464.addItemListener(this);
		cpc664.addItemListener(this);
		cpc6128.addItemListener(this);
		symbos.addItemListener(this);
		futureos.addItemListener(this);
		kccompact.addItemListener(this);
		customcpc.addItemListener(this);
		autosavedsk.addItemListener(this);
		checksave.addItemListener(this);
		checkrename.addItemListener(this);
		osdisplay.addItemListener(this);
		joyemu.addItemListener(this);
		qaop.addItemListener(this);
		joymouse.addItemListener(this);
		aontop.addItemListener(this);
		drivesound.addItemListener(this);
		unprotect.addItemListener(this);
		Printer.addItemListener(this);
		Expansion.addItemListener(this);
		Digiblaster.addItemListener(this);
		floppyturbo.addItemListener(this);

	}

	public void OnlineMenu() {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		onlinemenu.setMenuBar(JavaCPCMenu);
		onlinemenu.setTitle("JavaCPC - OnlineApplet menu");

		onlinemenu.add(JLogo);
		onlinemenu.pack();
		onlinemenu.setLocation((d.width - onlinemenu.getSize().width), (0));
		onlinemenu.setAlwaysOnTop(true);
		onlinemenu.setVisible(true);
	}

	public void mouseMoved(MouseEvent me) {
		if (fullscreen) {
			ypos = me.getYOnScreen();
			if (ypos <= 10 && !showmenu) {
				showmenu = true;
				getFrameAdapter().setMenuBar(JavaCPCMenu);
			} else if (ypos >= 400 && showmenu) {
				showmenu = false;
				getFrameAdapter().removeMenuBar(JavaCPCMenu);
			}

		}
		if (!Switches.blockKeyboard) {
			xoldpos = xpos;
			yoldpos = ypos;
			xpos = me.getXOnScreen();
			ypos = me.getYOnScreen();
			if (mousejoy) {

				if (xoldpos <= xpos - 1) {
					Switches.directR = 0;
					Switches.directxR = "Right";
					Switches.directxL = "Stop";
				} else if (xoldpos >= xpos + 1) {
					Switches.directL = 0;
					Switches.directxR = "Stop";
					Switches.directxL = "Left";
				}

				if (yoldpos >= ypos + 1) {
					Switches.directU = 0;
					Switches.directyU = "Up";
					Switches.directyD = "Stop";
				} else if (yoldpos <= ypos - 1) {
					Switches.directD = 0;
					Switches.directyU = "Stop";
					Switches.directyD = "Down";
				}
			}
		}
	}

	public void mouseDragged(MouseEvent me) {
		if (!Switches.blockKeyboard) {
			xoldpos = xpos;
			yoldpos = ypos;
			xpos = me.getXOnScreen();
			ypos = me.getYOnScreen();
			if (mousejoy) {
				if (xoldpos <= xpos - 1) {
					Switches.directR = 0;
					Switches.directxR = "Right";
					Switches.directxL = "Stop";
				} else if (xoldpos >= xpos + 1) {
					Switches.directL = 0;
					Switches.directxR = "Stop";
					Switches.directxL = "Left";
				}

				if (yoldpos >= ypos + 1) {
					Switches.directU = 0;
					Switches.directyU = "Up";
					Switches.directyD = "Stop";
				} else if (yoldpos <= ypos - 1) {
					Switches.directD = 0;
					Switches.directyU = "Stop";
					Switches.directyD = "Down";
				}

				if ((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
					computer.MouseFire1();
				else
					computer.MouseFire2();
			}
			/*
			 * else { xpos = me.getXOnScreen(); ypos = me.getYOnScreen(); getFrameAdapter().setLocation(xpos -
			 * (frame.getWidth()/2), ypos - (frame.getHeight()/2));
			 * 
			 * }
			 */
		}
	}

	protected void ScanLines() {
		if (checkScan.getState()) {
			Switches.ScanLines = true;
			// checkScaneff.setEnabled(true);
			Settings.setBoolean(Settings.SCANLINES, true);
		} else {
			Switches.ScanLines = false;
			// checkScaneff.setEnabled(false);
			Settings.setBoolean(Settings.SCANLINES, false);
		}
	}

	protected void ScanLinesEffect() {
		if (checkScaneff.getState()) {
			Display.scaneffect = true;
			Settings.setBoolean(Settings.SCANEFFECT, true);
		} else {
			Display.scaneffect = false;
			Settings.setBoolean(Settings.SCANEFFECT, false);
		}
	}

	protected void Bilinear() {
		if (checkbilinear.getState()) {
			Switches.bilinear = true;
			Settings.setBoolean(Settings.BILINEAR, true);
		} else {
			Switches.bilinear = false;
			Settings.setBoolean(Settings.BILINEAR, false);
		}

	}

	protected void fsoundInit() {
		if (fsound) {
			Switches.FloppySound = true;
			System.out.println("Floppysound Enabled");
		} else {
			Switches.FloppySound = false;
			System.out.println("Audio Disabled");
		}
	}

	public void screenshot() {
		applyAlwaysOnTop(false);
		images = new BufferedImage(display.getImage(false).getWidth(sprev), display.getImage(false).getHeight(sprev),
				BufferedImage.SCALE_SMOOTH);
		images.getGraphics().drawImage(display.getImage(false), 0, 0, display.getImage(false).getWidth(sprev),
				display.getImage(false).getHeight(sprev), null);

		screenPreview();

		screenshottimer = 0;
		screentimer = 0;
		computer.reSync();
		applyAlwaysOnTop(onTop);
	}

	public void screencapture() {
		images = new BufferedImage(display.getWidth(), display.getHeight(), BufferedImage.SCALE_SMOOTH);
		images.getGraphics().drawImage(display.image, 0, 0, display.getWidth(), display.getHeight(), null);
		screenControl();
	}

	JButton makeshot = new JButton("Save");
	JButton cancelshot = new JButton("Cancel");
	JButton startRec = new JButton("Capture");
	JButton pauseRec = new JButton("Pause");
	JButton stopRec = new JButton("Stop");

	protected boolean pauserec = false;

	protected void screenPreview() {
		if (screenpreview == null) {
			screenpreview = new JFrame() {

				protected void processWindowEvent(WindowEvent e) {
					super.processWindowEvent(e);
					if (e.getID() == WindowEvent.WINDOW_CLOSING) {
						screenpreview.dispose();
						initRec = false;
					}
				}

				public synchronized void setTitle(String title) {
					super.setTitle(title);
					enableEvents(AWTEvent.WINDOW_EVENT_MASK);
				}

			};
			screenpreview.addKeyListener(this);
		}

		screenpreview.remove(sprev);
		screenpreview.remove(startRec);
		screenpreview.remove(pauseRec);
		screenpreview.remove(stopRec);
		screenpreview.remove(sprev);
		screenpreview.remove(makeshot);
		screenpreview.remove(cancelshot);

		if (!screenpreview.isVisible())
			screenpreview.setVisible(true);
		makeshot.setFocusable(false);
		cancelshot.setFocusable(false);
		makeshot.setBackground(Color.DARK_GRAY);
		cancelshot.setBackground(Color.DARK_GRAY);
		makeshot.setForeground(Color.LIGHT_GRAY);
		cancelshot.setForeground(Color.LIGHT_GRAY);
		makeshot.setBorder(new BevelBorder(BevelBorder.RAISED));
		cancelshot.setBorder(new BevelBorder(BevelBorder.RAISED));
		screenpreview.setLayout(new GridBagLayout());
		screenpreview.setTitle("Screenshot");
		ImageIcon spr = null;
		spr = new ImageIcon(images);
		sprev = new JLabel(spr);
		screenpreview.add(sprev, getGridBagConstraints(1, 1, 0.0, 0.0, 2, GridBagConstraints.BOTH));
		screenpreview.add(makeshot, getGridBagConstraints(1, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		screenpreview.add(cancelshot, getGridBagConstraints(2, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		screenpreview.pack();
		// screenpreview.applyAlwaysOnTop(true); // screenpreview.applyAlwaysOnTop(true);
		screenpreview.setResizable(false);

	}

	protected void screenControl() {
		if (screenpreview == null) {
			screenpreview = new JFrame() {

				protected void processWindowEvent(WindowEvent e) {
					super.processWindowEvent(e);
					if (e.getID() == WindowEvent.WINDOW_CLOSING) {
						screenpreview.dispose();
						initRec = false;
					}
				}

				public synchronized void setTitle(String title) {
					super.setTitle(title);
					enableEvents(AWTEvent.WINDOW_EVENT_MASK);
				}

			};
			screenpreview.addKeyListener(this);
		}

		screenpreview.remove(sprev);
		screenpreview.remove(startRec);
		screenpreview.remove(pauseRec);
		screenpreview.remove(stopRec);
		screenpreview.remove(sprev);
		screenpreview.remove(makeshot);
		screenpreview.remove(cancelshot);
		if (!screenpreview.isVisible())
			screenpreview.setVisible(true);
		screenpreview.setLayout(new GridBagLayout());
		screenpreview.setTitle("Gif-Recorder");
		ImageIcon spr = new ImageIcon(images);
		sprev = new JLabel(spr);
		screenpreview.add(sprev, getGridBagConstraints(1, 1, 0.0, 0.0, 3, GridBagConstraints.BOTH));
		screenpreview.add(startRec, getGridBagConstraints(1, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		screenpreview.add(pauseRec, getGridBagConstraints(2, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		screenpreview.add(stopRec, getGridBagConstraints(3, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		screenpreview.pack();
		// screenpreview.applyAlwaysOnTop(true);
		screenpreview.setResizable(false);
		if (doRec) {
			if (!doingRec) {
				doingRec = true;
				encoder.start(captureName);
				encoder.setDelay(200);
				encoder.setRepeat(0);
				// encoder.setTransparent(Color.BLACK);
				System.out.println("recording started...");
				flasher = 0;
			}
			if (startR) {
				if (!pauserec) {
					encoder.addFrame(images);
					flasher++;
					if (flasher == 4)
						startRec.setBackground(Color.GREEN);
					if (flasher == 8) {
						startRec.setBackground(Color.DARK_GRAY);
						flasher = 0;
					}
				} else {
					flasher++;
					if (flasher == 40)
						startRec.setBackground(Color.BLUE);
					if (flasher == 80) {
						startRec.setBackground(Color.DARK_GRAY);
						flasher = 0;
					}

				}
			} else {
				if (doingRec) {
					encoder.finish();
					doingRec = false;
					doRec = false;
					initRec = false;
					screenpreview.dispose();
					System.out.println("recording finished...");
				}
			}
		} else {
			flasher++;
			if (flasher == 40)
				startRec.setBackground(Color.RED);
			if (flasher == 80) {
				startRec.setBackground(Color.DARK_GRAY);
				flasher = 0;
			}
		}
	}

	public void saveShot() {
		File file;
		FileDialog shotdia = getFrameAdapter()
				.createFileDialog("Save Screenshot (Please add PNG,BMP,GIF or JPG extension)", FileDialog.SAVE);
		shotdia.setFile("*.bmp; *.png; *.jpg; *.gif");
		shotdia.setVisible(true);
		String filename = shotdia.getFile();
		if (filename != null) {
			filename = shotdia.getDirectory() + shotdia.getFile();
			String savename = filename;
			try {
				if (savename.toLowerCase().endsWith(".png")) {
					file = new File(savename);
					ImageIO.write(images, "png", file);
				} else if (savename.toLowerCase().endsWith(".gif")) {
					file = new File(savename);
					ImageIO.write(images, "gif", file);
				} else if (savename.toLowerCase().endsWith(".bmp")) {
					file = new File(savename);
					ImageIO.write(images, "bmp", file);
				} else if (savename.toLowerCase().endsWith(".jpg")) {
					file = new File(savename);
					ImageIO.write(images, "jpg", file);
				} else {
					savename = savename + ".gif";
					file = new File(savename);
					ImageIO.write(images, "gif", file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
		}
	}

	public void UpdateCheck() throws Exception {

		URL url = new URL("http://cpc-live.com/update.txt");
		update = "";
		URLConnection uc = url.openConnection();
		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			update = update + inputLine;
		}

		System.out.println("Your version is: " + version);
		System.out.println("Latest version on server is " + update);

		String versId = version.substring(2);
		String versNo = version.substring(0, 1);
		String upId = update.substring(2);
		String upNo = update.substring(0, 1);
		int versionno = Integer.parseInt(versId + versNo);
		int updateno = Integer.parseInt(upId + upNo);
		if (version.equals(update) && !checkupdate) {
			System.out.println("Your are using the latest version of JavaCPC.");
			JOptionPane.showMessageDialog(null, "Dear JavaCPC-user,\n" + "you are using actual version " + version
					+ ".\n" + "No update available.");
		} else {

			if (beta) {
				JOptionPane.showMessageDialog(null,
						"Dear JavaCPC-user,\n" + "you are using untested beta version " + version + ".\n"
								+ "Please visit http://cpc-live.com\n" + "and download official version " + update
								+ ".");
				return;
			} else if (updateno < versionno) {
				JOptionPane.showMessageDialog(null,
						"Dear JavaCPC-user,\n" + "you are using version " + version + " of JavaCPC.\n"
								+ "Latest version on server is " + update + ".\n"
								+ "Your copy is newer than version on server.");
				return;

			} else if (!version.equals(update)) {
				System.out.println("Please update your copy of JavaCPC...");
				JOptionPane.showMessageDialog(null,
						"Dear JavaCPC-user,\n" + "you are using version " + version + " of JavaCPC.\n"
								+ "Please visit http://cpc-live.com\n" + "and download JavaCPC version " + update
								+ ".");
				try {
					ShowInfo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void RecInfo() {

		JOptionPane.showMessageDialog(null, "Use CRTL + Numpad-1 to start recording\n"
				+ "Use CRTL + Numpad-2 to stop & save recording\n" + "Use CRTL + Numpad-1 to start playback\n");
	}

	public void ShowInfo() throws Exception {
		URL url = new URL("http://cpc-live.com/updateinfo.txt");
		update = "";
		URLConnection uc = url.openConnection();
		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			update = update + inputLine + "\n";
		}

		System.out.println(update);
		JOptionPane.showMessageDialog(null, update);
	}

	public static void openWebPage(String weburl) {
		try {
			URL url = new URL(weburl);
			if (isWindows())
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			else
				Runtime.getRuntime().exec("firefox " + url);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void ExpansionRom() {
		String oldSys = getComputer().getName();
		System.out.println("Oldsys is:" + oldSys);
		try {
			setComputer(oldSys);
		} catch (final Exception ex) {
		}
		LoadFiles();
	}

	public void ExpansionInfo() {
		String info = "|SHOW - open printer console\n|HIDE - hide printer console\n"
				+ "|ONLINE - sets printer online\n|OFFLINE - sets printer offline\n"
				+ "|CLEAR - clears printer console content\n|BIGGER - increases fontsize of printer\n"
				+ "|SMALLER - decreases fontsize\n|SIZE,[size] - sets fontsize in pt\n"
				+ "|FONT1 - |FONT9 - sets printerfont\n|QUIT - quits JavaCPC emulator\n"
				+ "|GREEN - emulates greenmonitor\n|GRAY / |GREY - emulates graymonitor\n"
				+ "|COLOR / |COLOUR - colourmonitor\n|32INKS - emulates JavaCPC specialinks\n"
				+ "|SCREENSHOT - Saves a screenshot\n|SAVEDSK - Lets the user save actual DSK in DF0\n"
				+ "|MAKEDSK - creates a DATA formatted DSK\n|SCANLINES - enables scanlines\n"
				+ "|SCANLINESOFF - disables scanlines\n|INFO / |HELP - this info";
		JOptionPane.showMessageDialog(this, info);
	}

	public void addSkin() {
		getFrameAdapter().setResizable(false);
		if (Monitoruppanel == null) {
			Monitoruppanel = getControlPanel();
			Monitoruppanel.setBackground(Color.DARK_GRAY);
			Monitordownpanel = getControlPanel();
			Monitordownpanel.setBackground(Color.DARK_GRAY);
			Monitorleftpanel = getControlPanel();
			Monitorleftpanel.setBackground(Color.DARK_GRAY);
			Monitorrightpanel = getControlPanel();
			Monitorrightpanel.setBackground(Color.DARK_GRAY);
		}
		int[] pixelsc = new int[16 * 16];
		Image imagec = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixelsc, 0, 16));
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(imagec, new Point(0, 0), "invisibleCursor");
		Monitoruppanel.setCursor(blankCursor);
		Monitordownpanel.setCursor(blankCursor);
		Monitorleftpanel.setCursor(blankCursor);
		Monitorrightpanel.setCursor(blankCursor);
		Moniup.addMouseListener(this);
		Moniup.addMouseMotionListener(this);
		Monidown.addMouseListener(this);
		Monidown.addMouseMotionListener(this);
		Monileft.addMouseListener(this);
		Monileft.addMouseMotionListener(this);
		Moniright.addMouseListener(this);
		Moniright.addMouseMotionListener(this);
		Moniup.addKeyListener(this);
		Monidown.addKeyListener(this);
		Monileft.addKeyListener(this);
		Moniright.addKeyListener(this);
		Monitoruppanel.add(this.Moniup, getGridBagConstraints(0, 0, 0.0, 0.0, 1, GridBagConstraints.BOTH));
		Monitordownpanel.add(this.Monidown, getGridBagConstraints(0, 0, 0.0, 0.0, 1, GridBagConstraints.BOTH));
		Monitorleftpanel.add(this.Monileft, getGridBagConstraints(0, 0, 0.0, 0.0, 1, GridBagConstraints.BOTH));
		Monitorrightpanel.add(this.Moniright, getGridBagConstraints(0, 0, 0.0, 0.0, 1, GridBagConstraints.BOTH));
	}

	protected JCheckBox compress;

	public void chooseSNA() {
		Settings.set(Settings.SNAPSHOT_FILE, "");
		snaChooser = new JFrame("Save CPC Snapshot-file") {

			protected void processWindowEvent(WindowEvent e) {
				super.processWindowEvent(e);
				if (e.getID() == WindowEvent.WINDOW_CLOSING) {
					computer.start();
					dialogsnap = false;
					snaChooser.dispose();
				}
			}

		};
		computer.stop();
		snaChooser.setLayout(new GridBagLayout());
		s64.addActionListener(this);
		s128.addActionListener(this);
		s256.addActionListener(this);
		s512.addActionListener(this);
		cancel.addActionListener(this);
		s64.setFocusable(false);
		s128.setFocusable(false);
		s256.setFocusable(false);
		s512.setFocusable(false);

		Sn.setForeground(Color.DARK_GRAY);
		s64.setBackground(Color.LIGHT_GRAY);
		s64.setForeground(Color.DARK_GRAY);
		s64.setBorder(new BevelBorder(BevelBorder.RAISED));
		s128.setBackground(Color.LIGHT_GRAY);
		s128.setForeground(Color.DARK_GRAY);
		s128.setBorder(new BevelBorder(BevelBorder.RAISED));
		s256.setBackground(Color.LIGHT_GRAY);
		s256.setForeground(Color.DARK_GRAY);
		s256.setBorder(new BevelBorder(BevelBorder.RAISED));
		s512.setBackground(Color.LIGHT_GRAY);
		s512.setForeground(Color.DARK_GRAY);
		s512.setBorder(new BevelBorder(BevelBorder.RAISED));
		cancel.setBackground(Color.LIGHT_GRAY);
		cancel.setForeground(Color.DARK_GRAY);
		cancel.setBorder(new BevelBorder(BevelBorder.RAISED));
		cancel.setFocusable(false);
		snaChooser.setBackground(Color.LIGHT_GRAY);

		BufferedImage imager = new BufferedImage(288, 203, BufferedImage.SCALE_SMOOTH);
		imager.getGraphics().drawImage(display.getImage(true), 0, 0, 288, 203, null);
		imager.getGraphics().drawImage(imager, 0, 0, 288, 203, null);
		JLabel SNADisplay = new JLabel(new ImageIcon(imager));
		snaChooser.add(SNADisplay, getGridBagConstraints(1, 0, 1.0, 1.0, 4, GridBagConstraints.BOTH));
		snaChooser.add(Sn, getGridBagConstraints(1, 1, 1.0, 1.0, 4, GridBagConstraints.BOTH));
		snaChooser.add(s64, getGridBagConstraints(1, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		snaChooser.add(s128, getGridBagConstraints(2, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		snaChooser.add(s256, getGridBagConstraints(3, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		snaChooser.add(s512, getGridBagConstraints(4, 2, 1.0, 1.0, 1, GridBagConstraints.BOTH));
		snaChooser.add(cancel, getGridBagConstraints(3, 3, 1.0, 1.0, 2, GridBagConstraints.BOTH));
		snaChooser.pack();
		snaChooser.setAlwaysOnTop(true);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		snaChooser.setLocation((d.width - snaChooser.getSize().width) / 2,
				(d.height - snaChooser.getSize().height) / 2);
		snaChooser.addKeyListener(this);
		snaChooser.setVisible(true);
		dialogsnap = true;
	}

	boolean dialogsnap = false;

	public void Quit() {
		QuitTimer = 1;
	}

	public void setSimpleSized() {
		GateArray.doRender = false;
		rendertimer = 1;
		checkSimple.setState(true);
		if (large)
			setFullSize(false);
		Switches.triplesize = false;
		Switches.doublesize = false;
		if (Switches.doublesize) {
			if (skinned) {
				Moniup.setVisible(false);
				Monidown.setVisible(false);
				Monileft.setVisible(false);
				Moniright.setVisible(false);
			}
		} else {
			if (skinned) {
				if (!large) {
					Moniup.setVisible(true);
					Monidown.setVisible(true);
					Monileft.setVisible(true);
					Moniright.setVisible(true);
				}
			}
		}
		// fullscreen = false;
		final boolean running = computer.isRunning();
		computer.stop();
		computer.setLarge(large);
		this.display.setImageSize(computer.getDisplaySize(large), computer.getDisplayScale(large));
		computer.setDisplay(this.display);
		if (running)
			computer.start();
		this.display.requestFocus();
		Settings.setBoolean((Settings.LARGE), large);
		Settings.setBoolean(Settings.DOUBLE, Switches.doublesize);
		Settings.setBoolean(Settings.TRIPLE, Switches.triplesize);
		checkDouble.setState(Switches.doublesize);
		checkTriple.setState(Switches.triplesize);
		checkGate.setState(large);
		setOutputSize();
	}

	public void setDoubleSized(final boolean value) {
		GateArray.doRender = false;
		rendertimer = 1;
		if (value)
			checkSimple.setState(false);
		if (large)
			setFullSize(false);
		Switches.triplesize = false;
		Switches.doublesize = value;
		if (Switches.doublesize) {
			if (skinned) {
				Moniup.setVisible(false);
				Monidown.setVisible(false);
				Monileft.setVisible(false);
				Moniright.setVisible(false);
			}
		} else {
			if (skinned) {
				if (!large) {
					Moniup.setVisible(true);
					Monidown.setVisible(true);
					Monileft.setVisible(true);
					Moniright.setVisible(true);
				}
			}
		}
		// fullscreen = false;
		final boolean running = computer.isRunning();
		computer.stop();
		computer.setLarge(large);
		this.display.setImageSize(computer.getDisplaySize(large), computer.getDisplayScale(large));
		computer.setDisplay(this.display);
		if (running)
			computer.start();
		this.display.requestFocus();
		Settings.setBoolean((Settings.LARGE), large);
		Settings.setBoolean(Settings.DOUBLE, Switches.doublesize);
		Settings.setBoolean(Settings.TRIPLE, Switches.triplesize);
		checkDouble.setState(Switches.doublesize);
		checkTriple.setState(Switches.triplesize);
		checkGate.setState(large);
		setOutputSize();
	}

	public void setTripleSized(final boolean value) {
		GateArray.doRender = false;
		rendertimer = 1;
		if (value)
			checkSimple.setState(false);
		if (large)
			setFullSize(false);
		Switches.doublesize = false;
		Switches.triplesize = value;
		if (Switches.triplesize) {
			if (skinned) {
				Moniup.setVisible(false);
				Monidown.setVisible(false);
				Monileft.setVisible(false);
				Moniright.setVisible(false);
			}
		} else {
			if (skinned) {
				if (large != true) {
					Moniup.setVisible(true);
					Monidown.setVisible(true);
					Monileft.setVisible(true);
					Moniright.setVisible(true);
				}
			}
		}
		// fullscreen = false;
		final boolean running = computer.isRunning();
		computer.stop();
		computer.setLarge(large);
		this.display.setImageSize(computer.getDisplaySize(large), computer.getDisplayScale(large));
		computer.setDisplay(this.display);
		if (running)
			computer.start();
		this.display.requestFocus();
		Settings.setBoolean((Settings.LARGE), large);
		Settings.setBoolean(Settings.DOUBLE, Switches.doublesize);
		Settings.setBoolean(Settings.TRIPLE, Switches.triplesize);
		checkDouble.setState(Switches.doublesize);
		checkTriple.setState(Switches.triplesize);
		checkGate.setState(large);
		setOutputSize();
	}

	public void setFullSized(boolean value) {
		GateArray.doRender = false;
		rendertimer = 1;
		Switches.triplesize = false;
		Switches.doublesize = false;
		large = value;
		if (large) {
			if (skinned) {
				Moniup.setVisible(false);
				Monidown.setVisible(false);
				Monileft.setVisible(false);
				Moniright.setVisible(false);
			}
		} else {
			if (skinned) {
				Moniup.setVisible(true);
				Monidown.setVisible(true);
				Monileft.setVisible(true);
				Moniright.setVisible(true);
			}
		}
		final boolean running = computer.isRunning();
		computer.stop();
		computer.setLarge(large);
		display.setImageSize(computer.getDisplaySize(large), computer.getDisplayScale(large));
		computer.setDisplay(display);
		if (running)
			computer.start();
		this.display.requestFocus();
		checkDouble.setState(Switches.doublesize);
		checkTriple.setState(Switches.triplesize);
		Settings.setBoolean((Settings.LARGE), value);
		Settings.setBoolean(Settings.DOUBLE, Switches.doublesize);
		Settings.setBoolean(Settings.TRIPLE, Switches.triplesize);
		if (large) {
			checkSimple.setState(false);
		} else {
			checkSimple.setState(true);
		}
		checkGate.setState(large);
		setOutputSize();
	}

	private void setOutputSize() {
		GateArray.doRender = false;
		rendertimer = 1;
		if (executable) {
			getFrameAdapter().removeMenuBar(JavaCPCMenu);
			final boolean running = computer.isRunning();
			computer.stop();
			final Dimension p = Toolkit.getDefaultToolkit().getScreenSize();
			int w = 384;
			int h = 272;
			if (Switches.doublesize || large) {
				w = 384 * 2;
			}
			if (Switches.triplesize) {
				w = 384 * 3;
			}
			h = (w / 24) * 17;
			if (fullscreen) {
				w = p.width;
				h = p.height;
			}
			display.setSize(w, h);
			if (!fullscreen)
				getFrameAdapter().setMenuBar(JavaCPCMenu);
			getFrameAdapter().pack();
			getFrameAdapter().setLocation(screenXstored, screenYstored);
			if (fullscreen)
				getFrameAdapter().setLocation((p.width - getFrameAdapter().getSize().width) / 2,
						(p.height - getFrameAdapter().getSize().height) / 2);
			if (running) {
				computer.start();
				computer.reSync();
			}
		}
	}

	public void showDevices() {
		final Vector devices = computer.getDevices();
		final int n = devices.size();
		for (int i = 0; i < n; i++) {
			final Object device = devices.get(i);
			System.out.println("Device connected: " + device);
		}
	}

	private class DropListener extends DropTargetAdapter {
		public void drop(DropTargetDropEvent dtde) {
			try {
				Transferable t = dtde.getTransferable();
				if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					Object userObject = t.getTransferData(DataFlavor.javaFileListFlavor);
					if (userObject instanceof List) {
						String fileName = ((List) userObject).get(0).toString();
						Switches.askDrive = true;
						loadFile(0, fileName, false);
					}
					dtde.dropComplete(true);
				}
			} catch (Exception ex) {
				System.out.println("[MainForm::DropListener]" + ex);
			}
		}
	}

	private static void registerDropListener(ArrayList<DropTarget> list, Container basePanel, DropListener myListener) {
		list.add(new DropTarget(basePanel, myListener));

		Component[] components = basePanel.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component instanceof Container)
				registerDropListener(list, (Container) component, myListener);
			else
				list.add(new DropTarget(component, myListener));
		}
	}

	protected void askDrive(final String filename) throws Exception {
		final Frame ask = new Frame("Select drive");
		JButton driveA = new JButton(discA);
		JButton driveB = new JButton(discB);
		ask.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 12));
		ask.setBackground(Color.DARK_GRAY);
		driveA.setBorder(new BevelBorder(BevelBorder.RAISED));
		driveA.setBackground(Color.LIGHT_GRAY);
		driveA.setForeground(Color.WHITE);
		driveB.setBorder(new BevelBorder(BevelBorder.RAISED));
		driveB.setBackground(Color.LIGHT_GRAY);
		driveB.setForeground(Color.WHITE);

		driveA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computer.setCurrentDrive(0);
				try {
					loadFile(0, filename, false);
				} catch (Exception er) {
					System.out.println("Error while loading");
				}
				ask.dispose();
			}
		});
		driveB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computer.setCurrentDrive(1);
				try {
					loadFile(0, filename, false);
				} catch (Exception er) {
					System.out.println("Error while loading");
				}
				ask.dispose();
			}
		});

		ask.add(driveA);
		ask.add(driveB);
		ask.setResizable(false);
		ask.pack();
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		ask.setLocation((d.width - ask.getSize().width) / 2, (d.height - ask.getSize().height) / 2);
		ask.setAlwaysOnTop(true);
		ask.setVisible(true);
		Switches.askDrive = false;
	}

	protected void selectDialog() {
		Frame selectDSK = new Frame("Choose Disk");
		selectDSK.setLayout(new GridBagLayout());
		if (keepA != null) {
			KeepA = new JButton(titleA);
			KeepA.setBackground(Color.GREEN);
			KeepA.addActionListener(this);
			selectDSK.add(KeepA, getGridBagConstraints(1, 1, 1.0, 1.0, 2, GridBagConstraints.CENTER));
		}
		if (keepB != null) {
			KeepB = new JButton(titleB);
			KeepB.setBackground(Color.LIGHT_GRAY);
			KeepB.addActionListener(this);
			selectDSK.add(KeepB, getGridBagConstraints(1, 2, 1.0, 1.0, 2, GridBagConstraints.CENTER));
		}
		if (keepC != null) {
			KeepC = new JButton(titleC);
			KeepC.addActionListener(this);
			KeepC.setBackground(Color.LIGHT_GRAY);
			selectDSK.add(KeepC, getGridBagConstraints(1, 3, 1.0, 1.0, 2, GridBagConstraints.CENTER));
		}
		if (keepD != null) {
			KeepD = new JButton(titleD);
			KeepD.addActionListener(this);
			KeepD.setBackground(Color.LIGHT_GRAY);
			selectDSK.add(KeepD, getGridBagConstraints(1, 4, 1.0, 1.0, 2, GridBagConstraints.CENTER));
		}
		if (keepE != null) {
			KeepE = new JButton(titleE);
			KeepE.addActionListener(this);
			KeepE.setBackground(Color.LIGHT_GRAY);
			selectDSK.add(KeepE, getGridBagConstraints(1, 5, 1.0, 1.0, 2, GridBagConstraints.CENTER));
		}
		if (keepF != null) {
			KeepF = new JButton(titleF);
			KeepF.addActionListener(this);
			KeepF.setBackground(Color.LIGHT_GRAY);
			selectDSK.add(KeepF, getGridBagConstraints(1, 6, 1.0, 1.0, 2, GridBagConstraints.CENTER));
		}
		selectDSK.pack();
		selectDSK.setAlwaysOnTop(true);
		selectDSK.setResizable(false);
		selectDSK.setVisible(true);
	}

	public void setFloppyHead(int drive, int head) {
		final Vector devices = computer.getDevices();
		final int n = devices.size();
		for (int i = 0; i < n; i++) {
			final Object device = devices.get(i);
			if (device instanceof UPD765A) {
				final UPD765A FDC = (UPD765A) device;
				FDC.setForcedHead(head, drive);
				System.out.println("Floppy head choosen: Drive " + drive + " - Head " + head);
			}
		}
	}

	public Display getDisplay() {
		return display;
	}

	public void setStandalone(boolean standalone) {
		this.isStandalone = standalone;
	}

	public boolean isRunning() {
		return computer.isRunning();
	}

	public boolean isControlKeysEnabled() {
		return controlKeysEnabled;
	}

	public void setControlKeysEnabled(boolean controlKeysEnabled) {
		this.controlKeysEnabled = controlKeysEnabled;
	}

	public boolean isMouseClickActionsEnabled() {
		return mouseClickActionsEnabled;
	}

	public void setMouseClickActionsEnabled(boolean mouseClickActionsEnabled) {
		this.mouseClickActionsEnabled = mouseClickActionsEnabled;
	}

	public void addPauseListener(PauseListener listener) {
		getPauseListeners().add(listener);
	}

	public void removePauseListener(PauseListener listener) {
		getPauseListeners().remove(listener);
	}

	private List<PauseListener> getPauseListeners() {
		return pauseListeners;
	}

	public static interface PauseListener {

		void pauseStateChanged(JEMU jemu, boolean paused);

	}

}