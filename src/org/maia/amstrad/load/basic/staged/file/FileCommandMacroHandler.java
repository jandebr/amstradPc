package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.load.basic.staged.StagedBasicMacro;
import org.maia.amstrad.load.basic.staged.StagedBasicMacroHandler;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;

public abstract class FileCommandMacroHandler extends StagedBasicMacroHandler implements AmstradMemoryTrapHandler {

	private FileCommandResolver resolver;

	protected FileCommandMacroHandler(StagedBasicMacro macro, StagedBasicProgramLoaderSession session,
			FileCommandResolver resolver) {
		super(macro, session);
		this.resolver = resolver;
	}

	@Override
	public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
		FileCommand command = getResolver().resolve(memoryValue);
		if (command != null) {
			execute(command);
		}
	}

	protected abstract void execute(FileCommand command);

	private FileCommandResolver getResolver() {
		return resolver;
	}

}