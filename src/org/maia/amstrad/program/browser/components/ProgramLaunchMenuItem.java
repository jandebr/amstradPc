package org.maia.amstrad.program.browser.components;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.loader.AmstradProgramLoader;
import org.maia.amstrad.program.loader.AmstradProgramLoaderFactory;

public abstract class ProgramLaunchMenuItem extends ProgramMenuItem {

	private long executeStartTime;

	private boolean failed;

	protected ProgramLaunchMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program, String label) {
		super(browser, program, label);
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			executeStartTime = System.currentTimeMillis();
			new Thread(new Runnable() {
				@Override
				public void run() {
					ProgramBrowserDisplaySource browser = getBrowser();
					AmstradMonitorMode mode = getProgram().getPreferredMonitorMode();
					try {
						browser.releaseKeyboard();
						browser.getAmstradPc().reboot(true, true);
						launchProgram(getProgram());
						failed = false;
						// browser.closeModalWindow();
						browser.close(); // restores monitor mode & settings
						if (mode != null) {
							browser.getAmstradPc().getMonitor().setMonitorMode(mode);
						}
					} catch (AmstradProgramException exc) {
						System.err.println(exc);
						browser.acquireKeyboard();
						failed = true;
					} finally {
						executeStartTime = 0L;
					}
				}
			}).start();
		}
	}

	protected abstract void launchProgram(AmstradProgram program) throws AmstradProgramException;

	protected AmstradProgramLoader getProgramLoader(AmstradProgram program) {
		return AmstradProgramLoaderFactory.getInstance().createLoaderFor(program, getAmstradPc());
	}

	@Override
	public boolean isEnabled() {
		return !failed;
	}

	@Override
	public String getLabel() {
		String label = super.getLabel();
		if (executeStartTime > 0L) {
			int t = (int) ((System.currentTimeMillis() - executeStartTime) / 100L);
			label += ' ';
			label += (char) (192 + t % 4);
		} else if (failed) {
			label += ' ';
			label += (char) 225;
		}
		return label;
	}

}