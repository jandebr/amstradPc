package org.maia.amstrad.load;

import java.io.File;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramStoredInFile;

public abstract class AmstradProgramLoader {

	private AmstradPc amstradPc;

	protected AmstradProgramLoader(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	public final synchronized AmstradProgramRuntime load(AmstradProgram program) throws AmstradProgramException {
		AmstradProgramRuntime programRuntime = createProgramRuntime(program);
		AmstradProgramLoaderSession session = createLoaderSession(programRuntime);
		loadProgramIntoAmstradPc(program, session);
		if (program instanceof AmstradProgramStoredInFile) {
			File file = ((AmstradProgramStoredInFile) program).getFile();
			AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
			System.out.println("Loaded program from file " + file.getPath());
		}
		new AmstradProgramRuntimeDisposer(programRuntime).startTracking();
		return programRuntime;
	}

	protected abstract AmstradProgramRuntime createProgramRuntime(AmstradProgram program)
			throws AmstradProgramException;

	protected AmstradProgramLoaderSession createLoaderSession(AmstradProgramRuntime programRuntime) {
		return new AmstradProgramLoaderSession(this, programRuntime);
	}

	protected abstract void loadProgramIntoAmstradPc(AmstradProgram program, AmstradProgramLoaderSession session)
			throws AmstradProgramException;

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private static class AmstradProgramRuntimeDisposer extends AmstradPcStateAdapter
			implements AmstradProgramRuntimeListener {

		private AmstradProgramRuntime programRuntime;

		public AmstradProgramRuntimeDisposer(AmstradProgramRuntime programRuntime) {
			this.programRuntime = programRuntime;
		}

		public void startTracking() {
			getProgramRuntime().addListener(this);
			getProgramRuntime().getAmstradPc().addStateListener(this);
		}

		@Override
		public void amstradPcProgramLoaded(AmstradPc amstradPc) {
			getProgramRuntime().dispose(false, 0); // another program got loaded
		}

		@Override
		public void amstradPcRebooting(AmstradPc amstradPc) {
			getProgramRuntime().dispose(false, 0);
		}

		@Override
		public void amstradPcTerminated(AmstradPc amstradPc) {
			getProgramRuntime().dispose(false, 0);
		}

		@Override
		public void amstradProgramIsAboutToRun(AmstradProgramRuntime programRuntime) {
			// no action
		}

		@Override
		public void amstradProgramIsRun(AmstradProgramRuntime programRuntime) {
			// no action
		}

		@Override
		public void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
			stopTracking();
		}

		private void stopTracking() {
			getProgramRuntime().removeListener(this);
			getProgramRuntime().getAmstradPc().removeStateListener(this);
		}

		public AmstradProgramRuntime getProgramRuntime() {
			return programRuntime;
		}

	}

}