package org.maia.amstrad;

import java.util.Properties;

public class AmstradMain {

	public static final String SETTING_OVERRIDE_PREFIX = "javacpc.";

	public static void main(String[] args) throws Exception {
		overrideSettingsFromSytemProperties();
		AmstradFactory.getInstance().getAmstradContext().getMode().launch(args);
	}

	private static void overrideSettingsFromSytemProperties() {
		AmstradSettings settings = AmstradFactory.getInstance().getAmstradContext().getUserSettings();
		Properties props = System.getProperties();
		for (String prop : props.stringPropertyNames()) {
			if (prop.startsWith(SETTING_OVERRIDE_PREFIX)) {
				String key = prop.substring(SETTING_OVERRIDE_PREFIX.length());
				String value = props.getProperty(prop);
				settings.set(key, value);
			}
		}
	}

}