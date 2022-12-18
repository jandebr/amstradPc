package org.maia.amstrad.program.loader;

import java.io.IOException;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcSnapshotFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;

public class AmstradSnapshotLoader extends AmstradProgramLoader {

	public AmstradSnapshotLoader(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected AmstradProgramRuntime doLoad(AmstradProgram program) throws AmstradProgramException {
		if (program instanceof AmstradPcSnapshotFile) {
			AmstradPcSnapshotFile snapshotFile = (AmstradPcSnapshotFile) program;
			try {
				getAmstradPc().load(snapshotFile);
				return new AmstradSnapshotRuntime(snapshotFile, getAmstradPc());
			} catch (IOException e) {
				throw new AmstradProgramException(program,
						"Failed to load snapshot file " + snapshotFile.getFile().getPath(), e);
			}
		} else {
			throw new AmstradProgramException(program, program.getProgramName() + " is not a snapshot file");
		}
	}

	private static class AmstradSnapshotRuntime extends AmstradProgramRuntime {

		public AmstradSnapshotRuntime(AmstradPcSnapshotFile snapshotFile, AmstradPc amstradPc) {
			super(snapshotFile, amstradPc);
		}

		@Override
		protected void doRun() {
			// a snapshot resumes from loading, no run needed
		}

	}

}