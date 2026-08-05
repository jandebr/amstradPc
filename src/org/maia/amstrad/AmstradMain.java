package org.maia.amstrad;

import java.io.File;
import java.util.Properties;

import org.maia.amstrad.program.repo.cleaner.GetdownProgramFileRepositoryCleaner;
import org.maia.util.SystemUtils;
import org.maia.util.SystemUtils.PackageStrackTraceFilter;

public class AmstradMain {

	public static final String SETTING_OVERRIDE_PREFIX = "javacpc.";

	public static final String SETTING_OVERRIDE_FLAG_INIT_VALUE = "??";

	public static final String SETTING_DEBUG_STACKTRACES = "debug.stacktraces";

	public static void main(String[] args) throws Exception {
		AmstradContext context = AmstradFactory.getInstance().getAmstradContext();
		AmstradSettings settings = context.getUserSettings();
		overrideSettingsFromSystemProperties(settings);
		enableDebugOptions(settings);
		cleanManagedProgramRepository(context);
		context.setupAmstradSystem().launch(args);
	}

	private static void overrideSettingsFromSystemProperties(AmstradSettings settings) {
		Properties props = System.getProperties();
		for (String prop : props.stringPropertyNames()) {
			if (prop.startsWith(SETTING_OVERRIDE_PREFIX)) {
				String key = prop.substring(SETTING_OVERRIDE_PREFIX.length());
				String value = props.getProperty(prop);
				if (value.startsWith(SETTING_OVERRIDE_FLAG_INIT_VALUE)) {
					if (settings.get(key, null) == null) {
						value = value.substring(SETTING_OVERRIDE_FLAG_INIT_VALUE.length());
						settings.set(key, value);
					}
				} else {
					settings.set(key, value);
				}
			}
		}
	}

	private static void enableDebugOptions(AmstradSettings settings) {
		if (settings.getBool(SETTING_DEBUG_STACKTRACES, false)) {
			SystemUtils.printAllStackTracesPeriodically(30, new PackageStrackTraceFilter("jemu", "org.maia"));
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