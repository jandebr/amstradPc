package org.maia.amstrad.load.basic.staged.file;

import java.util.Collection;
import java.util.Vector;

import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramRuntimeListener;
import org.maia.amstrad.program.AmstradProgramRuntime;

public abstract class FileCommandRuntimeListener extends StagedBasicProgramRuntimeListener {

	private int memoryTrapAddress;

	private Collection<FileCommandReference> commandReferences;

	protected FileCommandRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
		super(session);
		this.memoryTrapAddress = memoryTrapAddress;
		this.commandReferences = new Vector<FileCommandReference>();
	}

	public FileCommandReference registerCommand(FileCommand command) {
		int n = getCommandReferences().size() + 1;
		FileCommandReference reference = new FileCommandReference(command, n);
		getCommandReferences().add(reference);
		return reference;
	}

	public void install() {
		AmstradProgramRuntime rt = getSession().getProgramRuntime();
		rt.addListener(this);
		if (rt.isRun()) {
			// already running
			stagedProgramIsRun();
		}
	}

	@Override
	protected void stagedProgramIsRun() {
		addMemoryTrap(getMemoryTrapAddress(), createMacroHandler(createResolver()));
	}

	@Override
	protected void stagedProgramIsDisposed(boolean programRemainsLoaded) {
		removeMemoryTrapsAt(getMemoryTrapAddress());
	}

	protected abstract FileCommandMacroHandler createMacroHandler(FileCommandResolver resolver);

	private FileCommandResolver createResolver() {
		return new FileCommandResolver() {

			@Override
			public FileCommand resolve(int referenceNumber) {
				for (FileCommandReference reference : getCommandReferences()) {
					if (reference.getReferenceNumber() == referenceNumber)
						return reference.getCommand();
				}
				return null;
			}
		};
	}

	public int getMemoryTrapAddress() {
		return memoryTrapAddress;
	}

	private Collection<FileCommandReference> getCommandReferences() {
		return commandReferences;
	}

}