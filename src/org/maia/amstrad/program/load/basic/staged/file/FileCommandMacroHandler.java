package org.maia.amstrad.program.load.basic.staged.file;

import java.io.File;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramStoredInFile;
import org.maia.amstrad.program.load.basic.staged.StagedBasicMacro;
import org.maia.amstrad.program.load.basic.staged.StagedBasicMacroHandler;
import org.maia.amstrad.program.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.program.load.basic.staged.StagedCommand;
import org.maia.amstrad.program.load.basic.staged.StagedCommandResolver;

public abstract class FileCommandMacroHandler extends StagedBasicMacroHandler {

	private BasicSourceCode sourceCode;

	private AmstradProgram program;

	protected FileCommandMacroHandler(StagedBasicMacro macro, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session, StagedCommandResolver resolver) {
		super(macro, session, resolver);
		this.sourceCode = sourceCode;
		this.program = session.getLastProgramInChain();
	}

	@Override
	public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
		StagedCommand command = getResolver().resolve(memoryValue);
		if (command != null && command instanceof FileCommand) {
			FileCommand fileCommand = (FileCommand) command;
			execute(fileCommand, lookupFileReference(fileCommand));
		}
	}

	protected abstract void execute(FileCommand command, FileReference fileReference);

	protected AmstradProgram getReferencedProgram(FileReference fileReference) {
		AmstradProgram refProgram = null;
		if (fileReference != null && fileReference.getTargetFile().exists()) {
			try {
				refProgram = AmstradFactory.getInstance().createBasicDescribedProgram(fileReference.getTargetFile(),
						fileReference.getMetadataFile());
			} catch (AmstradProgramException e) {
				System.err.println("Failed to instantiate the referenced program: " + fileReference);
			}
		}
		return refProgram;
	}

	private FileReference lookupFileReference(FileCommand command) {
		FileReference reference = null;
		String sourceFilename = command.getSourceFilenameWithoutFlags();
		if (sourceFilename != null) {
			reference = getProgram().lookupFileReference(sourceFilename);
			if (reference == null && !sourceFilename.isEmpty() && getProgram() instanceof AmstradProgramStoredInFile) {
				// Fallback : resolve file against the directory of the program
				File directory = ((AmstradProgramStoredInFile) getProgram()).getFile().getParentFile();
				reference = new FallbackFileReference(sourceFilename, directory);
			}
		}
		return reference;
	}

	protected BasicSourceCode getSourceCode() {
		return sourceCode;
	}

	private AmstradProgram getProgram() {
		return program;
	}

	private static class FallbackFileReference extends FileReference {

		private File directory;

		public FallbackFileReference(String filename, File directory) {
			super(filename, filename);
			this.directory = directory;
		}

		@Override
		protected File getFile(String filename) {
			return new File(getDirectory(), filename);
		}

		private File getDirectory() {
			return directory;
		}

	}

}