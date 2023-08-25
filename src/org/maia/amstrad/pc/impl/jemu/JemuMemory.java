package org.maia.amstrad.pc.impl.jemu;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.memory.AmstradMemory;

public abstract class JemuMemory extends AmstradMemory {

	private boolean jemuRunningAtStartOfTES;

	protected JemuMemory(JemuAmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public final void startThreadExclusiveSession() {
		super.startThreadExclusiveSession();
		if (!isNestedThreadExclusiveSession()) {
			// Pause computer when running
			synchronized (getAmstradPc()) {
				setJemuRunningAtStartOfTES(getAmstradPc().isRunning());
				if (isJemuRunningAtStartOfTES()) {
					pauseComputerInstantly();
				}
			}
		}
	}

	@Override
	public final void endThreadExclusiveSession() {
		if (!isNestedThreadExclusiveSession()) {
			// Resume computer when paused
			if (isJemuRunningAtStartOfTES()) {
				synchronized (getAmstradPc()) {
					resumeComputerInstantly();
				}
			}
		}
		super.endThreadExclusiveSession();
	}

	protected abstract void pauseComputerInstantly();

	protected abstract void resumeComputerInstantly();

	private boolean isJemuRunningAtStartOfTES() {
		return jemuRunningAtStartOfTES;
	}

	private void setJemuRunningAtStartOfTES(boolean running) {
		this.jemuRunningAtStartOfTES = running;
	}

}