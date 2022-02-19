package org.maia.amstrad.jemu.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.maia.amstrad.jemu.AmstradSettings;

public class AmstradSettingsImpl extends AmstradSettings {

	private File propertiesFile;

	private Properties properties;

	private boolean dirty;

	private long saveTimeoutMillis;

	private Thread saveThread;

	public AmstradSettingsImpl(File propertiesFile) throws IOException {
		this.propertiesFile = propertiesFile;
		this.properties = loadProperties();
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
		}
	}

	private Properties loadProperties() throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(getPropertiesFile()));
		System.out.println("Loaded " + props.size() + " user settings from " + getPropertiesFile().getAbsolutePath());
		return props;
	}

	private void saveProperties() {
		try {
			getProperties().store(new FileOutputStream(getPropertiesFile()), "[Settings]");
			setDirty(false);
			System.out.println("Saved user settings to " + getPropertiesFile().getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Can't save user settings (" + e.getMessage() + ")");
		}
	}

	private File getPropertiesFile() {
		return propertiesFile;
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
				try {
					Thread.sleep(50L);
				} catch (InterruptedException e) {
				}
			}
		}

	}

}