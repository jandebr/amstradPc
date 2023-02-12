package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.AmstradProgramRuntimeListener;

public abstract class StagedBasicProgramRuntimeListener implements AmstradProgramRuntimeListener {

	private StagedBasicProgramLoaderSession session;

	private int memoryTrapAddress;

	protected StagedBasicProgramRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
		this.session = session;
		this.memoryTrapAddress = memoryTrapAddress;
	}

	public void install() {
		AmstradProgramRuntime rt = getSession().getProgramRuntime();
		rt.addListener(this);
		if (rt.isRun()) {
			// already running
			amstradProgramIsRun();
		}
	}

	public void amstradProgramIsRun() {
		amstradProgramIsRun(getSession().getProgramRuntime());
	}

	@Override
	public void amstradProgramIsRun(AmstradProgramRuntime programRuntime) {
		addMemoryTrap(getMemoryTrapAddress(), createMemoryTrapHandler());
	}

	@Override
	public void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
		removeMemoryTrapsAt(getMemoryTrapAddress());
	}

	protected abstract AmstradMemoryTrapHandler createMemoryTrapHandler();

	private void addMemoryTrap(int memoryAddress, AmstradMemoryTrapHandler handler) {
		AmstradMemory memory = getSession().getAmstradPc().getMemory();
		memory.addMemoryTrap(memoryAddress, false, handler);
	}

	private void removeMemoryTrapsAt(int memoryAddress) {
		AmstradMemory memory = getSession().getAmstradPc().getMemory();
		memory.removeMemoryTrapsAt(memoryAddress);
	}

	protected StagedBasicProgramLoaderSession getSession() {
		return session;
	}

	public int getMemoryTrapAddress() {
		return memoryTrapAddress;
	}

}