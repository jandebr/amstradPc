package jemu.ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import jemu.core.Util;
import jemu.core.samples.Samples;
import jemu.settings.Settings;

/**
 * Title: JEMU Description: The Java Emulation Platform Copyright: Copyright (c) 2002 Company:
 * 
 * @author
 * @version 1.0
 */

public class Display extends JComponent {

	public boolean showDrive = false;
	public static int noisecount = 200;
	public static boolean lowperformance;
	public int mouseX;
	public int mouseY;

	protected Font displayFont;
	public static int turbotimer, audio, fader;
	protected boolean floppyturbo = false;
	public static String track = "00";
	public static String sector = "00";
	public static String drive = "0";
	public static int skin = 1;

	protected boolean printjob = false;
	final URL cursorim = getClass().getResource("image/crosshair.gif");
	final Image lightGun = getToolkit().getImage(cursorim);
	final Image imagec;
	private boolean debug = false;
	final URL floppyicon = getClass().getResource("image/read_small.png");
	final Image floppy = getToolkit().getImage(floppyicon);
	final URL floppyicon2 = getClass().getResource("image/read.png");
	final Image floppy2 = getToolkit().getImage(floppyicon2);
	final URL floppyiconws = getClass().getResource("image/write_small.png");
	final Image floppyws = getToolkit().getImage(floppyiconws);
	final URL floppyiconw = getClass().getResource("image/write.png");
	final Image floppyw = getToolkit().getImage(floppyiconw);
	final URL aboutim = getClass().getResource("image/onlogo.png");
	final Image about = getToolkit().getImage(aboutim);
	final URL atype = getClass().getResource("image/auto.png");
	final Image autotyped = getToolkit().getImage(atype);
	final URL atype2 = getClass().getResource("image/auto_small.png");
	final Image autotyped_small = getToolkit().getImage(atype2);
	final URL mute = getClass().getResource("image/mute.png");
	final Image muted = getToolkit().getImage(mute);
	final URL mutes = getClass().getResource("image/mute_small.png");
	final Image muteds = getToolkit().getImage(mutes);
	final URL pauses = getClass().getResource("image/pause_small.png");
	final Image pauseds = getToolkit().getImage(pauses);
	final URL pause = getClass().getResource("image/pause.png");
	final Image paused = getToolkit().getImage(pause);
	final URL print = getClass().getResource("image/print.png");
	final Image printed = getToolkit().getImage(print);
	final URL prints = getClass().getResource("image/print_small.png");
	final Image printeds = getToolkit().getImage(prints);
	final URL dbl = getClass().getResource("image/chip.png");
	final Image digi = getToolkit().getImage(dbl);
	final URL dbls = getClass().getResource("image/chip_small.png");
	final Image digis = getToolkit().getImage(dbls);
	final URL taped = getClass().getResource("image/tape.png");
	final Image tapeb = getToolkit().getImage(taped);
	final URL tapedb = getClass().getResource("image/tape_small.png");
	final Image tapes = getToolkit().getImage(tapedb);
	final URL recym = getClass().getResource("image/mix_record.png");
	final Image ymrec = getToolkit().getImage(recym);
	final URL playym = getClass().getResource("image/player_play.png");
	final Image ymplay = getToolkit().getImage(playym);
	final URL ymmode = getClass().getResource("image/ymmode.png");
	final Image YMmode = getToolkit().getImage(ymmode);
	final URL ymmode2 = getClass().getResource("image/cpc.png");
	final Image YMmode2 = getToolkit().getImage(ymmode2);
	final URL smallball = getClass().getResource("image/ball_small.png");
	final Image ball = getToolkit().getImage(smallball);
	final URL bigball = getClass().getResource("image/ball.png");
	final Image ballb = getToolkit().getImage(bigball);
	final URL bball = getClass().getResource("image/ball3.png");
	final Image ballbb = getToolkit().getImage(bball);

	final URL kplayb = getClass().getResource("image/keyplay_big.png");
	final Image playb = getToolkit().getImage(kplayb);
	final URL krecb = getClass().getResource("image/keyrec_big.png");
	final Image recb = getToolkit().getImage(krecb);

	final URL kplays = getClass().getResource("image/keyplay_small.png");
	final Image plays = getToolkit().getImage(kplays);
	final URL krecs = getClass().getResource("image/keyrec_small.png");
	final Image recs = getToolkit().getImage(krecs);

	// final URL monimask = getClass().getResource("image/monitor.png");
	// final Image monitor = getToolkit().getImage(monimask);
	final Cursor blankCursor;
	final Cursor gunCursor;
	public static String title = "";
	public static String author = "";
	public static String creator = "";
	long mLastFPSTime;
	int mNextFPS;
	int mCurrFPS;

	public static boolean scaneffect = false;
	public static boolean horizontal = true;

	private static final long serialVersionUID = 1L;
	public static int loadgames = 0;
	public static int bootgames = 0;
	public static int printer = -1;
	public static int dummy = 0;
	public static int blaster = 0;
	public static int tape = 0;
	public static int autotype = 0;
	public static int loadtimer = 0;
	public static int showboot = 0;
	public static int atmessage = 0;
	public static int left = 0;
	public static int right = 0;
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice gs = ge.getDefaultScreenDevice();
	GraphicsConfiguration gc = gs.getDefaultConfiguration();
	public static String monmessage = "Colour monitor"; // Message which monitor (CPC)
	public static String automessage = "Autosave is OFF";
	public static int showmon = 250;
	public static int showauto = 250;
	public static int showpause = 0;
	public static int showfps = 0;
	public static int showmodel = 0;
	protected int timer = 0;
	public static String model;
	public static Color FADE = new Color(0x00, 0x00, 0x00, 0x00);
	public static Color LED = new Color(0xff, 0x00, 0x00, 0x40);
	public static Color GREEN = new Color(0x00, 0xff, 0x00, 0x40);
	public static Color GREEN_BORDER = new Color(0x00, 0x90, 0x00, 0x40);
	public static Color LED_BORDER = new Color(0x90, 0x00, 0x00, 0x40);
	public static Color LED_OFF = new Color(0x90, 0x00, 0x00);
	public static Color WHITEA = new Color(0xff, 0xff, 0xff);
	public static Color BLACKA = new Color(0x00, 0x00, 0x00);
	public static Color SCAN = new Color(0x00, 0x00, 0x00, 0x60);
	public static Color TRANS = new Color(0xff, 0xff, 0xff, 0xb0);
	public static Color TRANSBLACK = new Color(0, 0, 0, 0x40);
	public static final Color REDA = new Color(0xff, 0x00, 0x00);
	public static final Color YELLOWA = new Color(0xff, 0xFF, 0x00);
	public static final Color ALERT = new Color(0xff, 0x00, 0x00);
	public static final Color BLUEA = new Color(0x50, 0x90, 0xff);
	public static int txtpos = Switches.showLogoAtLaunch ? 300 : 0;
	public static int txtstart = Switches.showLogoAtLaunch ? 300 : 0;
	public static boolean ledOn = false;
	public int led = 0;
	public static int showdisk = 0;
	public static final Dimension SCALE_1 = new Dimension(1, 1);
	public static final Dimension SCALE_2 = new Dimension(2, 2);
	public static final Dimension SCALE_1x2 = new Dimension(1, 2);

	// Rendering
	protected DisplayRenderDelegate renderDelegate;
	protected int[] pixels;
	protected int imageWidth, imageHeight;
	protected int scaleWidth, scaleHeight, scaleW, scaleH;
	public static Rectangle imageRect = new Rectangle();
	public static int ztext = 0 - 20;
	public static int ytext = ztext;

	private SecondaryDisplaySource secondaryDisplaySource; // when set, takes precedence over 'image'
	private DisplayOverlay customDisplayOverlay;
	private DisplayOverlay systemDisplayOverlay; // draws above custom

	private List<DisplayPaintListener> displayPaintListeners;
	private List<PrimaryDisplaySourceListener> primaryDisplaySourceListeners;

	// Performance observation
	private long performanceMonitoringStartTime = -1L;
	private int framesPainted;
	private int imagesUpdated;
	private List<DisplayPerformanceListener> performanceListeners;

	public Display() {
		this(new DisplayClassicRenderDelegate());
	}

	public Display(DisplayRenderDelegate renderDelegate) {
		this.renderDelegate = renderDelegate;
		setDoubleBuffered(renderDelegate.isDoubleBufferingEnabled());
		setupDisplayOverlays();
		displayPaintListeners = new Vector<DisplayPaintListener>();
		primaryDisplaySourceListeners = new Vector<PrimaryDisplaySourceListener>();
		performanceListeners = new Vector<DisplayPerformanceListener>();
		InputStream in = getClass().getResourceAsStream("amstrad.ttf");
		try {
			displayFont = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(0, 8);
		} catch (Exception e) {
		}
		int[] pixelsc = new int[16 * 16];
		imagec = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixelsc, 0, 16));
		blankCursor = Cursor.getDefaultCursor();// Toolkit.getDefaultToolkit().createCustomCursor(imagec, new Point(0,
												// 0), "invisibleCursor");
		gunCursor = Toolkit.getDefaultToolkit().createCustomCursor(lightGun, new Point(16, 16), "invisibleCursor");
		setCursor(blankCursor);
		enableEvents(AWTEvent.FOCUS_EVENT_MASK);
		setFocusTraversalKeysEnabled(true);
		setRequestFocusEnabled(true);
		renderDelegate.init(this);
	}

	private void setupDisplayOverlays() {
		setSystemDisplayOverlay(new MonitorMaskDisplayOverlay());
		uninstallCustomDisplayOverlay();
	}

	public void uninstallCustomDisplayOverlay() {
		installCustomDisplayOverlay(getDefaultCustomDisplayOverlay());
	}

	public void installCustomDisplayOverlay(DisplayOverlay overlay) {
		if (getCustomDisplayOverlay() != null) {
			getCustomDisplayOverlay().dispose(this);
		}
		if (overlay != null) {
			overlay.init(this);
			setCustomDisplayOverlay(overlay);
		} else {
			uninstallCustomDisplayOverlay();
		}
	}

	private DisplayOverlay getDefaultCustomDisplayOverlay() {
		return new JemuDisplayOverlay();
	}

	public void installSecondaryDisplaySource(SecondaryDisplaySource displaySource) {
		if (hasSecondaryDisplaySource()) {
			uninstallSecondaryDisplaySource();
		}
		if (displaySource != null) {
			displaySource.init(this);
			setSecondaryDisplaySource(displaySource);
			getRenderDelegate().refreshDisplay();
		}
	}

	public void uninstallSecondaryDisplaySource() {
		SecondaryDisplaySource displaySource = getSecondaryDisplaySource();
		if (displaySource != null) {
			setSecondaryDisplaySource(null);
			displaySource.dispose(this);
			getRenderDelegate().refreshDisplay();
		}
	}

	public boolean hasSecondaryDisplaySource() {
		return getSecondaryDisplaySource() != null;
	}

	public void setImageSize(Dimension size, Dimension scale) {
		imageWidth = size.width;
		imageHeight = size.height;
		pixels = new int[imageWidth * imageHeight];
		Arrays.fill(pixels, 0xff000000);
		if (scale == null)
			scale = SCALE_1;
		// System.out.println("Display image size " + size.width + "x" + size.height + " with scale " + scale.width +
		// "x"+ scale.height);
		scaleWidth = imageWidth * scale.width;
		scaleHeight = imageHeight * scale.height;
		scaleW = scaleWidth;
		scaleH = scaleHeight;
		checkSize();
		notifyPrimaryDisplaySourceResolutionChanged(new Dimension(size));
		Graphics g = getGraphics();
		if (g != null) {
			size = getSize();
			g.setColor(getBackground());
			g.fillRect(0, 0, size.width, size.height);
			paint(g);
			g.dispose();
		}
		getRenderDelegate().displayImageChangedSize(imageWidth, imageHeight);
	}

	public void dispose() {
		getRenderDelegate().dispose();
	}

	@Override
	public synchronized void setBounds(int x, int y, int width, int height) {
		boolean changedSize = width != getWidth() || height != getHeight();
		super.setBounds(x, y, width, height);
		checkSize();
		if (changedSize) {
			getRenderDelegate().displayChangedSize(width, height);
		}
	}

	protected void checkDouble() {
		if (Switches.doublesize) {
			scaleWidth = scaleW * 2;
			scaleHeight = scaleH * 2;
		} else if (Switches.triplesize) {
			scaleWidth = scaleW * 3;
			scaleHeight = scaleH * 3;
		} else {
			scaleWidth = scaleW;
			scaleHeight = scaleH;
		}
	}

	public void setCursor() {
		if (Switches.lightGun)
			setCursor(gunCursor);
		else
			setCursor(blankCursor);
	}

	public void changePerformance() {
		if (lowperformance) {
			LED = new Color(0xff, 0x00, 0x00);
			GREEN = new Color(0x00, 0xff, 0x00);
			GREEN_BORDER = new Color(0x00, 0x90, 0x00);
			LED_BORDER = new Color(0x90, 0x00, 0x00);
			SCAN = new Color(0x00, 0x00, 0x00);
			TRANS = new Color(0xff, 0xff, 0xff);
			TRANSBLACK = new Color(0x00, 0x00, 0x00);
		} else {
			LED = new Color(0xff, 0x00, 0x00, 0x40);
			GREEN = new Color(0x00, 0xff, 0x00, 0x40);
			GREEN_BORDER = new Color(0x00, 0x90, 0x00, 0x40);
			LED_BORDER = new Color(0x90, 0x00, 0x00, 0x40);
			TRANS = new Color(0xff, 0xff, 0xff, 0xb0);
			TRANSBLACK = new Color(0x00, 0x00, 0x00, 0x40);
			if (Switches.monitormode == 0 || Switches.monitormode == 1) {
				SCAN = new Color(0x00, 0x00, 0x00, 0x30);
			} else
				SCAN = new Color(0x00, 0x00, 0x00, 0x90);
		}
	}

	protected void checkSize() {
		checkDouble();
		Dimension size = getSize();
		Insets insets = getInsets();
		int clientWidth = size.width - insets.left - insets.right;
		int clientHeight = size.height - insets.top - insets.bottom;
		if (Switches.stretch) {
			imageRect = new Rectangle(insets.left + (clientWidth - scaleWidth) / 2,
					insets.top + (clientHeight - scaleHeight) / 2, scaleWidth, scaleHeight);
		} else {
			imageRect = new Rectangle(insets.left, insets.top, clientWidth, clientHeight);
		}
	}

	public int[] getPixels() {
		return pixels;
	}

	public void updateImage(boolean wait) {
		if (imageWidth > 0 && imageHeight > 0) {
			getRenderDelegate().displayPixelsReadyForPainting();
			imagesUpdated++;
		}
	}

	public boolean processGun() {
		int rgb = getRGB(mouseX, mouseY);
		System.out.println("Colour is:" + Util.hex(rgb));
		if (rgb == 0xFFFFFFFF)
			return true;
		return false;
	}

	public int getColor() {
		return getRGB(1, 1);
	}

	private int getRGB(int imageX, int imageY) {
		int rgb = 0;
		if (imageX >= 0 && imageX < imageWidth && imageY >= 0 && imageY < imageHeight) {
			rgb = pixels[imageY * imageWidth + imageX];
		}
		return rgb;
	}

	private void doTouchFPS() {
		long time = System.currentTimeMillis();
		mNextFPS++;
		if (time - mLastFPSTime >= 1000) {
			mCurrFPS = mNextFPS;
			mNextFPS = 0;
			mLastFPSTime = time;
		}
	}

	@Override
	public void setBackground(Color bg) {
	}

	@Override
	public void paintComponent(Graphics g) {
		if (imageWidth > 0 && imageHeight > 0) {
			if (showfps >= 1)
				doTouchFPS();
			if (showfps <= -1 && debug) {
				showfps++;
				doTouchFPS();
			}
			if (turbotimer == 10) {
				if (!floppyturbo) {
					Switches.turbo = 3;
					floppyturbo = true;
				}
			}
			if (turbotimer >= 2) {
				turbotimer--;
			}
			if (turbotimer == 1) {
				Switches.turbo = 1;
				floppyturbo = false;
				turbotimer--;
			}
			getRenderDelegate().paintDisplayOnscreen(g, isMonitorEffectEnabled());
			framesPainted++;
		}
		setPainted(true);
		notifyDisplayGotPainted();
		updatePerformanceMonitoring();
	}

	public BufferedImage getImage(boolean monitorEffect) {
		BufferedImage off_Image = new BufferedImage(imageRect.width, imageRect.height, BufferedImage.TYPE_INT_RGB);
		Graphics g = off_Image.createGraphics();
		getRenderDelegate().paintDisplayOffscreen(g, monitorEffect);
		g.dispose();
		return off_Image;
	}

	public static void setFade(int fade) {
		if (fade >= 24)
			FADE = new Color(0x00, 0x00, 0x00, fade);
		if (fade <= -24)
			if (Switches.monitormode != 2)
				FADE = new Color(0xff, 0xff, 0xff, 0x00 - fade);
			else
				FADE = new Color(0x00, 0xff, 0x00, 0x00 - fade);
		fader = fade;
	}

	@Override
	public Dimension getPreferredSize() {
		Insets insets = getInsets();
		return new Dimension(imageRect.width + insets.left + insets.right,
				imageRect.height + insets.top + insets.bottom);
	}

	public Font getDisplayFont() {
		return displayFont;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getScaledWidth() {
		return scaleWidth;
	}

	public int getScaledHeight() {
		return scaleHeight;
	}

	public boolean isLargeDisplay() {
		return imageRect.height >= 540;
	}

	public void setPainted(boolean value) {
		getRenderDelegate().setPainted(value);
	}

	private void updatePerformanceMonitoring() {
		long now = System.currentTimeMillis();
		if (performanceMonitoringStartTime < 0L || now >= performanceMonitoringStartTime + 1000L) {
			if (performanceMonitoringStartTime >= 0L) {
				notifyPerformanceUpdate(now - performanceMonitoringStartTime, framesPainted, imagesUpdated);
			}
			performanceMonitoringStartTime = now;
			framesPainted = 0;
			imagesUpdated = 0;
		}
	}

	@Override
	protected void processFocusEvent(FocusEvent e) {
		super.processFocusEvent(e);
		if (e.getID() == FocusEvent.FOCUS_GAINED) {
			// System.out.println("Display Focused");
		} else {
			// System.out.println("Display Lost Focus");
		}
	}

	public boolean isScanLinesEnabled() {
		return Switches.ScanLines && (!lowperformance || allowScanLinesWhenLowPerformance());
	}

	public boolean isBilinearEnabled() {
		return Switches.bilinear && (!lowperformance || allowBilinearWhenLowPerformance());
	}

	public boolean isMonitorEffectEnabled() {
		return scaneffect && (!lowperformance || allowScanEffectWhenLowPerformance());
	}

	private boolean allowScanLinesWhenLowPerformance() {
		return Settings.getBoolean(Settings.ALLOW_SCANLINES_LOWPERFORMANCE, false);
	}

	private boolean allowScanEffectWhenLowPerformance() {
		return Settings.getBoolean(Settings.ALLOW_SCANEFFECT_LOWPERFORMANCE, false);
	}

	private boolean allowBilinearWhenLowPerformance() {
		return Settings.getBoolean(Settings.ALLOW_BILINEAR_LOWPERFORMANCE, false);
	}

	public MonitorMask getMonitorMask() {
		MonitorMask mask = null;
		MonitorMaskFactory factory = MonitorMaskFactory.getInstance();
		String monitor = Settings.get(Settings.MONITOR, "");
		if (Settings.MONITOR_COLOUR.equals(monitor)) {
			mask = factory.getCTM644MonitorMask();
		} else if (Settings.MONITOR_GREEN.equals(monitor)) {
			mask = factory.getGT65MonitorMask();
		} else {
			mask = factory.getDefaultMonitorMask();
		}
		return mask;
	}

	public DisplayOverlay getCustomDisplayOverlay() {
		return customDisplayOverlay;
	}

	private void setCustomDisplayOverlay(DisplayOverlay customDisplayOverlay) {
		this.customDisplayOverlay = customDisplayOverlay;
	}

	public DisplayOverlay getSystemDisplayOverlay() {
		return systemDisplayOverlay;
	}

	private void setSystemDisplayOverlay(DisplayOverlay systemDisplayOverlay) {
		this.systemDisplayOverlay = systemDisplayOverlay;
	}

	public SecondaryDisplaySource getSecondaryDisplaySource() {
		return secondaryDisplaySource;
	}

	private void setSecondaryDisplaySource(SecondaryDisplaySource displaySource) {
		secondaryDisplaySource = displaySource;
	}

	public DisplayRenderDelegate getRenderDelegate() {
		return renderDelegate;
	}

	public void addDisplayPaintListener(DisplayPaintListener listener) {
		getDisplayPaintListeners().add(listener);
	}

	public void removeDisplayPaintListener(DisplayPaintListener listener) {
		getDisplayPaintListeners().remove(listener);
	}

	private List<DisplayPaintListener> getDisplayPaintListeners() {
		return displayPaintListeners;
	}

	private void notifyDisplayGotPainted() {
		for (DisplayPaintListener listener : getDisplayPaintListeners()) {
			listener.displayGotPainted(this);
		}
	}

	public void addPrimaryDisplaySourceListener(PrimaryDisplaySourceListener listener) {
		getPrimaryDisplaySourceListeners().add(listener);
	}

	public void removePrimaryDisplaySourceListener(PrimaryDisplaySourceListener listener) {
		getPrimaryDisplaySourceListeners().remove(listener);
	}

	private List<PrimaryDisplaySourceListener> getPrimaryDisplaySourceListeners() {
		return primaryDisplaySourceListeners;
	}

	private void notifyPrimaryDisplaySourceResolutionChanged(Dimension resolution) {
		for (PrimaryDisplaySourceListener listener : getPrimaryDisplaySourceListeners()) {
			listener.primaryDisplaySourceResolutionChanged(this, resolution);
		}
	}

	public void addPerformanceListener(DisplayPerformanceListener listener) {
		getPerformanceListeners().add(listener);
	}

	public void removePerformanceListener(DisplayPerformanceListener listener) {
		getPerformanceListeners().remove(listener);
	}

	public List<DisplayPerformanceListener> getPerformanceListeners() {
		return performanceListeners;
	}

	private void notifyPerformanceUpdate(long timeIntervalMillis, int framesPainted, int imagesUpdated) {
		for (DisplayPerformanceListener listener : getPerformanceListeners())
			listener.displayPerformanceUpdate(this, timeIntervalMillis, framesPainted, imagesUpdated);
	}

	public static interface DisplayPaintListener {

		void displayGotPainted(Display display);

	}

	public static interface PrimaryDisplaySourceListener {

		void primaryDisplaySourceResolutionChanged(Display display, Dimension resolution);

	}

	private class JemuDisplayOverlay implements DisplayOverlay {

		public JemuDisplayOverlay() {
		}

		@Override
		public void init(JComponent displayComponent) {
			// no action
		}

		@Override
		public void renderOntoDisplay(DisplayView displayView, Rectangle displayBounds, MonitorMask monitorMask,
				boolean offscreenImage) {
			if (offscreenImage)
				return;
			Graphics2D g = displayView.createDisplayViewport(displayBounds.x, displayBounds.y, displayBounds.width,
					displayBounds.height);
			ImageObserver imageObserver = Display.this;
			boolean largeDisplay = isLargeDisplay();
			// Floppy
			if (ledOn)
				led = 200;
			if (led > 0 && !lowperformance) {
				int trackpos = Integer.parseInt(track);
				trackpos = 0;
				if (largeDisplay) {
					g.setFont(displayFont);
					g.setColor(Color.BLACK);
					if (Switches.write)
						g.drawImage(floppyw, imageRect.width - 80, 12 + trackpos, imageObserver);
					else
						g.drawImage(floppy2, imageRect.width - 80, 12 + trackpos, imageObserver);

					g.drawString(track, imageRect.width - 58, 30 + trackpos);
					g.drawString(sector, imageRect.width - 58, 46 + trackpos);
					g.drawString(drive, imageRect.width - 48, 68 + trackpos);
				} else {
					g.setFont(displayFont);
					g.setColor(Color.BLACK);
					if (Switches.write)
						g.drawImage(floppyws, imageRect.width - 40, 6 + trackpos, imageObserver);
					else
						g.drawImage(floppy, imageRect.width - 40, 6 + trackpos, imageObserver);
					g.drawString(track, imageRect.width - 29, 15 + trackpos);
					g.drawString(sector, imageRect.width - 29, 23 + trackpos);
					g.drawString(drive, imageRect.width - 24, 34 + trackpos);
				}
				led--;
			}
			// About
			if (txtpos > 0 && !lowperformance) {
				g.drawImage(about, imageRect.width / 2 - 110, ytext - 20, imageObserver);
				txtpos = txtpos - 1;
				if (txtpos > 255)
					ytext = ytext + 1;
				if (txtpos < 50)
					ytext = ytext - 1;
			}
			// FPS
			if (showfps >= 1 || (showfps <= -1 && debug)) {
				g.setFont(displayFont);
				String cpu = "CPU:" + Switches.turbo * 100 + "%";
				String fps = "FPS: " + mCurrFPS;
				g.setColor(ALERT);
				g.drawString(fps, imageRect.width - 96, imageRect.height - 16);
				g.drawString(cpu, imageRect.width - 96, imageRect.height - 6);
			}
			// Autotype
			if (atmessage > 0 && !lowperformance) {
				g.setFont(displayFont);
				g.setColor(BLACKA);
				g.drawString("Existing content in autotype console", imageRect.width / 2 - 90, imageRect.height - 10);
				g.setColor(BLUEA);
				g.drawString("Existing content in autotype console", imageRect.width / 2 - 91, imageRect.height - 11);
				atmessage--;
			}
			if (Switches.osddisplay && !lowperformance) {
				// Selected monitor
				if (showmon >= 0) {
					g.setFont(displayFont);
					g.setColor(BLACKA);
					g.drawString(monmessage, imageRect.width - 100, imageRect.height - 20);
					g.setColor(YELLOWA);
					g.drawString(monmessage, imageRect.width - 101, imageRect.height - 22);
					showmon = showmon - 2;
				}
				// Selected model
				if (showmodel >= 0) {
					g.setFont(displayFont);
					g.setColor(BLACKA);
					g.drawString(model, imageRect.width - (model.length() - 1) * 8, imageRect.height - 34);
					g.setColor(REDA);
					g.drawString(model, imageRect.width - ((model.length() - 1) * 8) + 1, imageRect.height - 36);
					showmodel = showmodel - 2;
				}
				// Autosave state
				if (showauto >= 0) {
					g.setFont(displayFont);
					g.setColor(BLACKA);
					g.drawString(automessage, 6, imageRect.height - 54);
					g.setColor(GREEN);
					g.drawString(automessage, 6, imageRect.height - 52);
					showauto = showauto - 2;
				}
				// Drive content
				if (showdisk >= 0) {
					g.setFont(displayFont);
					g.setColor(BLACKA);
					g.drawString("DF0: " + Switches.loaddrivea, 6, imageRect.height - 37);
					g.drawString("DF1: " + Switches.loaddriveb, 6, imageRect.height - 26);
					g.drawString("DF2: " + Switches.loaddrivec, 6, imageRect.height - 15);
					g.drawString("DF3: " + Switches.loaddrived, 6, imageRect.height - 4);
					g.setColor(WHITEA);
					g.drawString("DF0: " + Switches.loaddrivea, 5, imageRect.height - 39);
					g.drawString("DF1: " + Switches.loaddriveb, 5, imageRect.height - 28);
					g.drawString("DF2: " + Switches.loaddrivec, 5, imageRect.height - 17);
					g.drawString("DF3: " + Switches.loaddrived, 5, imageRect.height - 6);
					showdisk = showdisk - 2;
				}
			}
			// Pause
			if (showpause > 0) {
				if (largeDisplay)
					g.drawImage(paused, imageRect.width - 80, 12, imageObserver);
				else
					g.drawImage(pauseds, imageRect.width - 40, 6, imageObserver);
			}
			// Printer
			if (printer >= 0 && !lowperformance) {
				if (!printjob) {
					printjob = true;
					Samples.PRINTER.loop();
				}
				if (largeDisplay)
					g.drawImage(printed, imageRect.width - 80, 12, imageObserver);
				else
					g.drawImage(printeds, imageRect.width - 40, 6, imageObserver);
				printer--;
			} else {
				if (printjob) {
					Samples.PRINTER.stop();
					printjob = false;
				}
			}
			// Blaster
			if (blaster >= 0 && !lowperformance) {
				if (largeDisplay)
					g.drawImage(digi, imageRect.width - 80, 12, imageObserver);
				else
					g.drawImage(digis, imageRect.width - 40, 6, imageObserver);
				blaster--;
			}
			// Tape
			if (tape >= 0 && !lowperformance) {
				if (largeDisplay)
					g.drawImage(tapeb, imageRect.width - 80, 12, imageObserver);
				else
					g.drawImage(tapes, imageRect.width - 40, 6, imageObserver);
				tape--;
			}
			// Muted
			if (Switches.audioenabler != 1 && !lowperformance) {
				if (largeDisplay)
					g.drawImage(muted, 10, 12, imageObserver);
				else
					g.drawImage(muteds, 5, 6, imageObserver);
			}
			// Autotype
			if (autotype >= 1) {
				if (isLargeDisplay())
					g.drawImage(autotyped, imageRect.width - 80, 12, imageObserver);
				else
					g.drawImage(autotyped_small, imageRect.width - 40, 6, imageObserver);
			}
			// Autoboot games
			if (bootgames >= 1) {
				g.setColor(ALERT);
				g.drawString("AUTOBOOT IN PROGRESS ", imageRect.width / 2 - 70, imageRect.height / 2);
				g.drawString("AUTOBOOT IN PROGRESS ", imageRect.width / 2 - 71, imageRect.height / 2);
				bootgames--;
			}
			// Autoboot
			if (showboot >= 1) {
				g.setColor(ALERT);
				g.setFont(displayFont);
				g.drawString("AUTOBOOT IN PROGRESS " + showboot, imageRect.width / 2 - 80, imageRect.height / 2);
				g.drawString("AUTOBOOT IN PROGRESS " + showboot, imageRect.width / 2 - 81, imageRect.height / 2);
				showboot--;
			}
			// Autoload DSK and SNA
			if (loadgames >= 1) {
				g.setColor(ALERT);
				g.drawString("AUTOSTART IN PROGRESS ", imageRect.width / 2 - 70, imageRect.height / 2);
				g.drawString("AUTOSTART IN PROGRESS ", imageRect.width / 2 - 71, imageRect.height / 2);
				loadgames--;
				if (loadgames == 20) {
					JEMU.autoloader = 1;
				}
				if (loadgames == 4) {
					JEMU.autoloader = 2;
				}
			}
			if (loadtimer > 0) {
				g.setColor(ALERT);
				g.drawString(" AUTOLOAD IN PROGRESS ", imageRect.width / 2 - 70, imageRect.height / 2);
				g.drawString(" AUTOLOAD IN PROGRESS ", imageRect.width / 2 - 71, imageRect.height / 2);
			}
			g.dispose();
		}

		@Override
		public void dispose(JComponent displayComponent) {
			// no action
		}

	}

	private class MonitorMaskDisplayOverlay implements DisplayOverlay {

		public MonitorMaskDisplayOverlay() {
		}

		@Override
		public void init(JComponent displayComponent) {
			// no action
		}

		@Override
		public void renderOntoDisplay(DisplayView displayView, Rectangle displayBounds, MonitorMask monitorMask,
				boolean offscreenImage) {
			if (monitorMask != null) {
				Graphics2D g = displayView.createDisplayViewport(displayBounds.x, displayBounds.y, displayBounds.width,
						displayBounds.height);
				g.drawImage(monitorMask.getImage(), 0, 0, displayBounds.width, displayBounds.height, Display.this);
				g.dispose();
			}
		}

		@Override
		public void dispose(JComponent displayComponent) {
			// no action
		}

	}

}