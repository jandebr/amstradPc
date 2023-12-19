package org.maia.amstrad;

import java.io.File;
import java.util.Properties;

import org.maia.amstrad.program.repo.cleaner.GetdownProgramFileRepositoryCleaner;

public class AmstradMain {

	public static final String SETTING_OVERRIDE_PREFIX = "javacpc.";

	public static void main(String[] args) throws Exception {
		AmstradContext context = AmstradFactory.getInstance().getAmstradContext();
		context.initSystemLogs();
		System.out.println("Launching AmstradPc");
		overrideSettingsFromSytemProperties(context.getUserSettings());
		cleanManagedProgramRepository(context);
		context.getMode().launch(args);
	}

	private static void overrideSettingsFromSytemProperties(AmstradSettings settings) {
		Properties props = System.getProperties();
		for (String prop : props.stringPropertyNames()) {
			if (prop.startsWith(SETTING_OVERRIDE_PREFIX)) {
				String key = prop.substring(SETTING_OVERRIDE_PREFIX.length());
				String value = props.getProperty(prop);
				settings.set(key, value);
			}
		}
	}

	private static void cleanManagedProgramRepository(AmstradContext context) {
		if (context.isManagedProgramRepositoryCleanupEnabled()) {
			File managedFolder = context.getManagedProgramRepositoryRootFolder();
			if (managedFolder != null) {
				if (context.isLaunchedByGetdown()) {
					// Getdown does not by itself cleanup files left out in newer versions
					new GetdownProgramFileRepositoryCleaner().cleanProgramRepository(managedFolder, true);
				}
			}
		}
	}

}