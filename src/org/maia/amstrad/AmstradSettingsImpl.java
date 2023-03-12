package org.maia.amstrad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.maia.amstrad.util.AmstradUtils;

public class AmstradSettingsImpl extends AmstradSettings {

	private Properties properties;

	private boolean dirty;

	private long saveTimeoutMillis;

	private Thread saveThread;

	private static final String DEFAULT_INI_FILE = "javacpc.ini";

	public static final String SYSTEM_PROPERTY_INI_FILE_IN = "javacpc.ini";

	public static final String SYSTEM_PROPERTY_INI_FILE_OUT = "javacpc.ini.out";

	public AmstradSettingsImpl() {
		reset();
	}

	@Override
	public synchronized void reset() {
		flush();
		this.properties = loadProperties();
	}

	@Override
	public synchronized void flush() {
		if (isDirty()) {
			saveProperties();
		}
	}

	@Override
	public synchronized String get(String key, String defaultValue) {
		String value = getProperties().getProperty(key);
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	@Override
	public synchronized void set(String key, String value) {
		if (!value.equals(get(key, null))) {
			getProperties().setProperty(key, value);
			setDirty(true);
			setSaveTimeoutMillis(System.currentTimeMillis() + 100L);
			if (getSaveThread() == null) {
				Thread t = new Thread(new DeferredSaver());
				setSaveThread(t);
				t.setDaemon(true);
				t.start();
			}
			if (!key.equals("frame_xpos") && !key.equals("frame_ypos")) {
				System.out.println("User setting '" + key + "' changed to '" + value + "'");
			}
		}
	}

	private Properties loadProperties() {
		Properties props = new Properties();
		File file = getPropertiesInputFile();
		try {
			props.load(new FileInputStream(file));
			System.out.println("Loaded " + props.size() + " user settings from " + file.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Can't load user settings (" + e.getMessage() + ")");
		}
		return props;
	}

	private void saveProperties() {
		File file = getPropertiesOutputFile();
		if (file != null) {
			try {
				getProperties().store(new FileOutputStream(file), "[Settings]");
				System.out.println("Saved user settings to " + file.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Can't save user settings (" + e.getMessage() + ")");
			}
		}
		setDirty(false);
	}

	private File getPropertiesInputFile() {
		return new File(System.getProperty(SYSTEM_PROPERTY_INI_FILE_IN, DEFAULT_INI_FILE));
	}

	private File getPropertiesOutputFile() {
		File file = null;
		String path = System.getProperty(SYSTEM_PROPERTY_INI_FILE_OUT);
		if (path != null) {
			file = new File(path);
		}
		return file;
	}

	private Properties getProperties() {
		return properties;
	}

	private boolean isDirty() {
		return dirty;
	}

	private void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	private long getSaveTimeoutMillis() {
		return saveTimeoutMillis;
	}

	private void setSaveTimeoutMillis(long saveTimeoutMillis) {
		this.saveTimeoutMillis = saveTimeoutMillis;
	}

	private Thread getSaveThread() {
		return saveThread;
	}

	private void setSaveThread(Thread saveThread) {
		this.saveThread = saveThread;
	}

	private class DeferredSaver implements Runnable {

		public DeferredSaver() {
		}

		@Override
		public void run() {
			while (true) {
				synchronized (AmstradSettingsImpl.this) {
					if (isDirty() && System.currentTimeMillis() >= getSaveTimeoutMillis()) {
						saveProperties();
					}
				}
				AmstradUtils.sleep(50L);
			}
		}

	}

}