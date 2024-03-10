package org.maia.amstrad.program.load.basic.staged;

import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.program.load.AmstradProgramRuntime;

public abstract class StagedBasicProgramTrappedRuntimeListener extends StagedBasicProgramRuntimeListener {

	private int memoryTrapAddress;

	protected StagedBasicProgramTrappedRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
		super(session);
		this.memoryTrapAddress = memoryTrapAddress;
	}

	@Override
	public void amstradProgramIsAboutToRun(AmstradProgramRuntime programRuntime) {
		addMemoryTrap(getMemoryTrapAddress(), createMemoryTrapHandler());
	}

	@Override
	public void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
		removeMemoryTrapsAt(getMemoryTrapAddress());
	}

	protected abstract AmstradMemoryTrapHandler createMemoryTrapHandler();

	private void addMemoryTrap(int memoryAddress, AmstradMemoryTrapHandler handler) {
		AmstradMemory memory = getSession().getAmstradPc().getMemory();
		memory.addMemoryTrap(memoryAddress, true, handler);
	}

	private void removeMemoryTrapsAt(int memoryAddress) {
		AmstradMemory memory = getSession().getAmstradPc().getMemory();
		memory.removeMemoryTrapsAt(memoryAddress);
	}

	public int getMemoryTrapAddress() {
		return memoryTrapAddress;
	}

}