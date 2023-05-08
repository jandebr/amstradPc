package org.maia.amstrad;

import java.io.File;
import java.util.Properties;

import org.maia.amstrad.util.getdown.GetdownProgramFileRepositoryCleaner;

public class AmstradMain {

	public static final String SETTING_OVERRIDE_PREFIX = "javacpc.";

	private static final String GETDOWN_HINT_PROPERTY = "com.threerings.getdown";

	public static void main(String[] args) throws Exception {
		overrideSettingsFromSytemProperties();
		cleanProgramRepository();
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

	private static void cleanProgramRepository() {
		if (isLaunchedByGetdown()) {
			File rootFolder = AmstradFactory.getInstance().getAmstradContext().getProgramRepositoryRootFolder();
			new GetdownProgramFileRepositoryCleaner().cleanProgramRepository(rootFolder, true);
		}
	}

	private static boolean isLaunchedByGetdown() {
		return Boolean.parseBoolean(System.getProperty(GETDOWN_HINT_PROPERTY, "false"));
	}

}