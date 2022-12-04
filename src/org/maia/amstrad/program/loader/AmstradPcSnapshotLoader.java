package org.maia.amstrad.program.loader;

import java.io.IOException;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcSnapshotFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;

public class AmstradPcSnapshotLoader extends AmstradProgramLoader {

	public AmstradPcSnapshotLoader(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public AmstradProgramRuntime load(AmstradProgram program) throws AmstradProgramException {
		if (program instanceof AmstradPcSnapshotFile) {
			AmstradPcSnapshotFile snapshotFile = (AmstradPcSnapshotFile) program;
			try {
				getAmstradPc().load(snapshotFile);
				return new AmstradPcSnapshotRuntime(snapshotFile, getAmstradPc());
			} catch (IOException e) {
				throw new AmstradProgramException(program, "Failed to load snapshot file " + program.getProgramName(),
						e);
			}
		} else {
			throw new AmstradProgramException(program, program.getProgramName() + " is not a snapshot file");
		}
	}

	private static class AmstradPcSnapshotRuntime extends AmstradProgramRuntime {

		public AmstradPcSnapshotRuntime(AmstradPcSnapshotFile snapshotFile, AmstradPc amstradPc) {
			super(snapshotFile, amstradPc);
		}

		@Override
		public void run() {
			// a snapshot resumes from loading, no run needed
		}

	}

}