package org.maia.amstrad.program.load.snapshot;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.amstrad.program.load.AmstradProgramRuntime;

public class AmstradPcSnapshotRuntime extends AmstradProgramRuntime {

	public AmstradPcSnapshotRuntime(AmstradPcSnapshotFile snapshotFile, AmstradPc amstradPc) {
		super(snapshotFile, amstradPc);
	}

	@Override
	protected void doRun(String... args) {
		// a snapshot resumes from loading, no run actions needed
	}

}