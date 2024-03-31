package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;
import java.util.Vector;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.program.load.AmstradProgramRuntime;

public abstract class StagedBasicProgramTrappedRuntimeListener extends StagedBasicProgramRuntimeListener {

	private int memoryTrapAddress;

	private Collection<StagedCommandReference> commandReferences;

	private static final int MAX_REFERENCE_NUMBER = 0xff; // 8bit number that can be POKE'd

	protected StagedBasicProgramTrappedRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
		super(session);
		this.memoryTrapAddress = memoryTrapAddress;
		this.commandReferences = new Vector<StagedCommandReference>();
	}

	public StagedCommandReference registerCommand(StagedCommand command) throws BasicException {
		int referenceNumber = getCommandReferences().size() + 1;
		if (referenceNumber <= MAX_REFERENCE_NUMBER) {
			StagedCommandReference reference = new StagedCommandReference(command, referenceNumber);
			getCommandReferences().add(reference);
			return reference;
		} else {
			throw new BasicException("Reached the maximum staged command references (" + MAX_REFERENCE_NUMBER + ")");
		}
	}

	@Override
	public void amstradProgramIsAboutToRun(AmstradProgramRuntime programRuntime) {
		addMemoryTrap(getMemoryTrapAddress(), createMemoryTrapHandler());
	}

	@Override
	public void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
		removeMemoryTrapsAt(getMemoryTrapAddress());
	}

	protected final StagedBasicMacroHandler createMemoryTrapHandler() {
		return createMacroHandler(createResolver());
	}

	protected abstract StagedBasicMacroHandler createMacroHandler(StagedCommandResolver resolver);

	private StagedCommandResolver createResolver() {
		return new StagedCommandResolver() {

			@Override
			public StagedCommand resolve(int referenceNumber) {
				for (StagedCommandReference reference : getCommandReferences()) {
					if (reference.getReferenceNumber() == referenceNumber)
						return reference.getCommand();
				}
				return null;
			}
		};
	}

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

	private Collection<StagedCommandReference> getCommandReferences() {
		return commandReferences;
	}

}