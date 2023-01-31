package org.maia.amstrad.load.basic.staged.file;

import java.util.Collection;
import java.util.Vector;

import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramRuntimeListener;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;

public abstract class FileCommandRuntimeListener extends StagedBasicProgramRuntimeListener {

	private Collection<FileCommandReference> commandReferences;

	protected FileCommandRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
		super(session, memoryTrapAddress);
		this.commandReferences = new Vector<FileCommandReference>();
	}

	public FileCommandReference registerCommand(FileCommand command) {
		int n = getCommandReferences().size() + 1;
		FileCommandReference reference = new FileCommandReference(command, n);
		getCommandReferences().add(reference);
		return reference;
	}

	@Override
	protected final AmstradMemoryTrapHandler createMemoryTrapHandler() {
		return createMacroHandler(createResolver());
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

	private Collection<FileCommandReference> getCommandReferences() {
		return commandReferences;
	}

}