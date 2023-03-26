package org.maia.amstrad.load.snapshot;

import java.io.IOException;

import org.maia.amstrad.load.AmstradProgramLoader;
import org.maia.amstrad.load.AmstradProgramLoaderSession;
import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class AmstradPcSnapshotLoader extends AmstradProgramLoader {

	public AmstradPcSnapshotLoader(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected AmstradProgramRuntime createProgramRuntime(AmstradProgram program) throws AmstradProgramException {
		if (program instanceof AmstradPcSnapshotFile) {
			AmstradPcSnapshotFile snapshotFile = (AmstradPcSnapshotFile) program;
			return new AmstradPcSnapshotRuntime(snapshotFile, getAmstradPc());
		} else {
			throw new AmstradProgramException(program, program.getProgramName() + " is not a snapshot file");
		}
	}

	@Override
	protected void loadProgramIntoAmstradPc(AmstradProgram program, AmstradProgramLoaderSession session)
			throws AmstradProgramException {
		AmstradPcSnapshotFile snapshotFile = (AmstradPcSnapshotFile) program;
		try {
			getAmstradPc().getTape().load(snapshotFile);
		} catch (IOException e) {
			throw new AmstradProgramException(program,
					"Failed to load snapshot file " + snapshotFile.getFile().getPath(), e);
		}
	}

}