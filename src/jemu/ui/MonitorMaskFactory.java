package jemu.ui;

import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MonitorMaskFactory {

	private static MonitorMaskFactory instance;

	/**
	 * Colour monitor
	 */
	private MonitorMask CTM644;

	/**
	 * Green monitor
	 */
	private MonitorMask GT65;

	/**
	 * Minimalistic monitor
	 */
	private MonitorMask MINIMAL;

	private MonitorMaskFactory() {
	}

	public MonitorMask getDefaultMonitorMask() {
		return getCTM644MonitorMask();
	}

	public MonitorMask getCTM644MonitorMask() {
		if (CTM644 == null) {
			Image image = null;
			try {
				image = ImageIO.read(getClass().getResource("image/ctm644.png"));
			} catch (IOException e) {
				System.err.println(e);
			}
			CTM644 = new MonitorMask(image, new Insets(24, 25, 24, 25));
		}
		return CTM644;
	}

	public MonitorMask getGT65MonitorMask() {
		if (GT65 == null) {
			Image image = null;
			try {
				image = ImageIO.read(getClass().getResource("image/gt65.png"));
			} catch (IOException e) {
				System.err.println(e);
			}
			GT65 = new MonitorMask(image, new Insets(40, 48, 64, 48));
		}
		return GT65;
	}

	public MonitorMask getMinimalMonitorMask() {
		if (MINIMAL == null) {
			Image image = null;
			try {
				image = ImageIO.read(getClass().getResource("image/tv-minimal.png"));
			} catch (IOException e) {
				System.err.println(e);
			}
			MINIMAL = new MonitorMask(image, new Insets(16, 15, 12, 15));
		}
		return MINIMAL;
	}

	public static MonitorMaskFactory getInstance() {
		if (instance == null) {
			setInstance(new MonitorMaskFactory());
		}
		return instance;
	}

	private static synchronized void setInstance(MonitorMaskFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

}