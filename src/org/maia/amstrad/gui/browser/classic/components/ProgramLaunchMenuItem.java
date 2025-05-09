package org.maia.amstrad.gui.browser.classic.components;

import org.maia.amstrad.gui.browser.classic.ClassicProgramBrowserDisplaySource;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.load.AmstradProgramLoader;
import org.maia.amstrad.program.load.AmstradProgramLoaderFactory;

public abstract class ProgramLaunchMenuItem extends ProgramMenuItem {

	private long executeStartTime;

	private boolean failed;

	protected ProgramLaunchMenuItem(ProgramMenu menu, String label) {
		super(menu, label);
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			executeStartTime = System.currentTimeMillis();
			new Thread(new Runnable() {
				@Override
				public void run() {
					ClassicProgramBrowserDisplaySource browser = getBrowserDisplaySource();
					AmstradMonitorMode mode = getProgram().getPreferredMonitorMode();
					try {
						browser.releaseKeyboard();
						browser.getAmstradPc().reboot(true, true);
						launchProgram();
						setFailed(false);
						// browser.closeModalWindow();
						browser.close();
						if (mode != null) {
							browser.getAmstradPc().getMonitor().setMode(mode);
						}
					} catch (AmstradProgramException exc) {
						exc.printStackTrace();
						browser.acquireKeyboard();
						setFailed(true);
					} finally {
						executeStartTime = 0L;
					}
				}
			}).start();
		}
	}

	protected abstract void launchProgram() throws AmstradProgramException;

	protected AmstradProgramLoader getProgramLoader() {
		return AmstradProgramLoaderFactory.getInstance().createLoaderFor(getProgram(), getAmstradPc());
	}

	@Override
	public boolean isEnabled() {
		if (isFailed())
			return false;
		if (getProgram().isNoLaunch())
			return false;
		return true;
	}

	@Override
	public String getLabel() {
		String label = super.getLabel();
		if (executeStartTime > 0L) {
			int t = (int) ((System.currentTimeMillis() - executeStartTime) / 100L);
			label += ' ';
			label += (char) (192 + t % 4);
		} else if (isFailed()) {
			label += ' ';
			label += (char) 225;
		}
		return label;
	}

	protected boolean isFailed() {
		return failed;
	}

	protected void setFailed(boolean failed) {
		this.failed = failed;
	}

}