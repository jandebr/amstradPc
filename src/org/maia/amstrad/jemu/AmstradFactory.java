package org.maia.amstrad.jemu;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.jemu.impl.AmstradContextImpl;
import org.maia.amstrad.jemu.impl.AmstradPcImpl;
import org.maia.amstrad.jemu.impl.AmstradSettingsImpl;
import org.maia.amstrad.jemu.impl.JemuFrameBridge;

public class AmstradFactory {

	private static AmstradFactory instance;

	private static File userSettingsFile = new File("javacpc.ini");

	private AmstradContext context;

	private AmstradFactory() {
	}

	public AmstradContext getAmstradContext() {
		if (context == null) {
			AmstradSettings userSettings = createUserSettings();
			if (userSettings == null) {
				System.err.println("Can't load user settings");
				System.exit(1);
			}
			context = new AmstradContextImpl(userSettings, System.out, System.err);
		}
		return context;
	}

	private AmstradSettings createUserSettings() {
		AmstradSettings userSettings = null;
		try {
			userSettings = new AmstradSettingsImpl(userSettingsFile);
		} catch (IOException e) {
			System.err.println("Can't load user settings: " + e.getMessage());
		}
		return userSettings;
	}

	public AmstradPc createAmstradPc() {
		JemuFrameBridge frameBridge = new JemuFrameBridge();
		AmstradPc amstradPc = new AmstradPcImpl(frameBridge);
		AmstradPcFrame frame = new AmstradPcFrame(amstradPc);
		frameBridge.setFrame(frame);
		return amstradPc;
	}

	public static AmstradFactory getInstance() {
		if (instance == null) {
			setInstance(new AmstradFactory());
		}
		return instance;
	}

	private static synchronized void setInstance(AmstradFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

}