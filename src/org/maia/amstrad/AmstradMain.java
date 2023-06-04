package org.maia.amstrad;

import java.io.File;
import java.util.Properties;

import org.maia.amstrad.util.getdown.GetdownProgramFileRepositoryCleaner;

public class AmstradMain {

	public static final String SETTING_OVERRIDE_PREFIX = "javacpc.";

	public static void main(String[] args) throws Exception {
		AmstradContext context = AmstradFactory.getInstance().getAmstradContext();
		context.initJavaConsole();
		System.out.println("Launching AmstradPc");
		overrideSettingsFromSytemProperties(context);
		cleanManagedProgramRepository(context);
		context.getMode().launch(args);
	}

	private static void overrideSettingsFromSytemProperties(AmstradContext context) {
		Properties props = System.getProperties();
		for (String prop : props.stringPropertyNames()) {
			if (prop.startsWith(SETTING_OVERRIDE_PREFIX)) {
				String key = prop.substring(SETTING_OVERRIDE_PREFIX.length());
				String value = props.getProperty(prop);
				context.getUserSettings().set(key, value);
			}
		}
	}

	private static void cleanManagedProgramRepository(AmstradContext context) {
		if (context.isManagedProgramRepositoryCleanupEnabled()) {
			File managedFolder = context.getManagedProgramRepositoryRootFolder();
			if (managedFolder != null) {
				if (context.isLaunchedByGetdown()) {
					new GetdownProgramFileRepositoryCleaner().cleanProgramRepository(managedFolder, true);
				}
			}
		}
	}

}