package org.maia.amstrad.load.basic.staged.file;

import java.io.IOException;

import org.maia.amstrad.load.basic.staged.StagedBasicMacro;
import org.maia.amstrad.load.basic.staged.StagedBasicMacroHandler;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.AmstradProgramBuilder;

public abstract class FileCommandMacroHandler extends StagedBasicMacroHandler {

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

	protected AmstradProgram getReferencedProgram(FileReference fileReference) {
		AmstradProgram refProgram = null;
		if (fileReference != null) {
			refProgram = new AmstradBasicProgramFile(fileReference.getTargetFile());
			AmstradProgramBuilder builder = AmstradProgramBuilder.createFor(refProgram);
			try {
				builder.loadAmstradMetaData(fileReference.getMetadataFile());
			} catch (IOException e) {
				System.err.println("Failed to load the metadata of the referenced program: " + fileReference);
			}
			refProgram = builder.build();
		}
		return refProgram;
	}

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