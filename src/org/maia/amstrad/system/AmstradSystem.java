package org.maia.amstrad.system;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradException;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.menu.AmstradMenu;
import org.maia.amstrad.system.impl.AmstradSystemCoreTermination;
import org.maia.amstrad.system.impl.AmstradSystemJemuLogs;

import jemu.settings.Settings;

public abstract class AmstradSystem {

	private AmstradPc amstradPc;

	private AmstradSystemSettings systemSettings;

	private AmstradSystemLogs systemLogs;

	protected AmstradSystem() {
		this.amstradPc = createAmstradPc();
		this.systemSettings = createSystemSettings();
		this.systemLogs = createSystemLogs();
		init();
	}

	protected void init() {
		getSystemLogs().init();
	}

	public final void launch(String[] args) throws AmstradException {
		System.out.println("Launching Amstrad system " + getName());
		overrideUserSettingsBeforeLaunch();
		doLaunch(getAmstradPc(), args);
	}

	public final void terminate() {
		System.out.println("Terminating Amstrad system");
		createTermination().terminate(this);
	}

	protected void overrideUserSettingsBeforeLaunch() {
		getUserSettings().setBool(Settings.SHOWMENU, getSystemSettings().isUsingOriginalJemuMenu());
	}

	protected abstract void doLaunch(AmstradPc amstradPc, String[] args) throws AmstradException;

	protected AmstradPc createAmstradPc() {
		return AmstradFactory.getInstance().createAmstradPc();
	}

	protected AmstradSystemTermination createTermination() {
		return new AmstradSystemCoreTermination();
	}

	protected abstract AmstradSystemSettings createSystemSettings();

	protected AmstradSystemLogs createSystemLogs() {
		return new AmstradSystemJemuLogs();
	}

	protected AmstradMenu createMenuBar() {
		return AmstradFactory.getInstance().createMenuBar(getAmstradPc());
	}

	protected AmstradMenu createPopupMenu() {
		return AmstradFactory.getInstance().createPopupMenu(getAmstradPc());
	}

	public AmstradSettings getUserSettings() {
		return getAmstradContext().getUserSettings();
	}

	public AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	public abstract String getName();

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	public AmstradSystemSettings getSystemSettings() {
		return systemSettings;
	}

	public AmstradSystemLogs getSystemLogs() {
		return systemLogs;
	}

}