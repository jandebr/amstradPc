package org.maia.amstrad.load.snapshot;

import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradPcSnapshotFile;

public class AmstradPcSnapshotRuntime extends AmstradProgramRuntime {

	public AmstradPcSnapshotRuntime(AmstradPcSnapshotFile snapshotFile, AmstradPc amstradPc) {
		super(snapshotFile, amstradPc);
	}

	@Override
	protected void doRun(String... args) {
		// a snapshot resumes from loading, no run actions needed
	}

}