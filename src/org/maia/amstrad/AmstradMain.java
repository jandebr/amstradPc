package org.maia.amstrad;

import java.io.File;
import java.util.Properties;

import org.maia.amstrad.util.getdown.GetdownProgramFileRepositoryCleaner;

public class AmstradMain {

	public static final String SETTING_OVERRIDE_PREFIX = "javacpc.";

	public static void main(String[] args) throws Exception {
		overrideSettingsFromSytemProperties();
		cleanManagedProgramRepository();
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

	private static void cleanManagedProgramRepository() {
		AmstradContext context = AmstradFactory.getInstance().getAmstradContext();
		if (context.isLaunchedByGetdown()) {
			File managedFolder = context.getManagedProgramRepositoryRootFolder();
			if (managedFolder != null) {
				new GetdownProgramFileRepositoryCleaner().cleanProgramRepository(managedFolder, true);
			}
		}
	}

}