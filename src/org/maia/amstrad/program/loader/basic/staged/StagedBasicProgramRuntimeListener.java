package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.AmstradProgramRuntimeListener;

public abstract class StagedBasicProgramRuntimeListener implements AmstradProgramRuntimeListener {

	private StagedBasicProgramLoaderSession session;

	protected StagedBasicProgramRuntimeListener(StagedBasicProgramLoaderSession session) {
		this.session = session;
	}

	@Override
	public final void amstradProgramIsRun(AmstradProgramRuntime programRuntime) {
		stagedProgramIsRun();
	}

	protected abstract void stagedProgramIsRun();

	@Override
	public final void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
		stagedProgramIsDisposed(programRemainsLoaded);
	}

	protected abstract void stagedProgramIsDisposed(boolean programRemainsLoaded);

	protected void addMemoryTrap(int memoryAddress, AmstradMemoryTrapHandler handler) {
		AmstradMemory memory = getSession().getAmstradPc().getMemory();
		memory.addMemoryTrap(memoryAddress, false, handler);
	}

	protected void removeMemoryTrapsAt(int memoryAddress) {
		AmstradMemory memory = getSession().getAmstradPc().getMemory();
		memory.removeMemoryTrapsAt(memoryAddress);
	}

	protected StagedBasicProgramLoaderSession getSession() {
		return session;
	}

}