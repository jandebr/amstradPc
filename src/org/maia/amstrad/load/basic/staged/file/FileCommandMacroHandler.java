package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.load.basic.staged.StagedBasicMacro;
import org.maia.amstrad.load.basic.staged.StagedBasicMacroHandler;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.FileReference;

public abstract class FileCommandMacroHandler extends StagedBasicMacroHandler implements AmstradMemoryTrapHandler {

	private FileCommandResolver resolver;

	private AmstradProgram program;

	protected FileCommandMacroHandler(StagedBasicMacro macro, StagedBasicProgramLoaderSession session,
			FileCommandResolver resolver) {
		super(macro, session);
		this.resolver = resolver;
		this.program = session.getLastProgramInChain();
	}

	@Override
	public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
		FileCommand command = getResolver().resolve(memoryValue);
		if (command != null) {
			execute(command, lookupFileReference(command));
		}
	}

	protected abstract void execute(FileCommand command, FileReference fileReference);

	private FileReference lookupFileReference(FileCommand command) {
		return getProgram().lookupFileReference(command.getSourceFilenameWithoutFlags());
	}

	private FileCommandResolver getResolver() {
		return resolver;
	}

	private AmstradProgram getProgram() {
		return program;
	}

}