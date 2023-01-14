package org.maia.amstrad.program.loader;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardListener;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.AmstradProgramRuntimeListener;

public abstract class AmstradProgramLoader {

	private AmstradPc amstradPc;

	protected AmstradProgramLoader(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	public final synchronized AmstradProgramRuntime load(AmstradProgram program) throws AmstradProgramException {
		AmstradProgramRuntime programRuntime = createProgramRuntime(program);
		AmstradProgramLoaderSession session = createLoaderSession(programRuntime);
		loadProgramIntoAmstradPc(program, session);
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

	protected AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private static class AmstradProgramRuntimeDisposer extends AmstradPcStateAdapter
			implements AmstradKeyboardListener, AmstradProgramRuntimeListener {

		private AmstradProgramRuntime programRuntime;

		public AmstradProgramRuntimeDisposer(AmstradProgramRuntime programRuntime) {
			this.programRuntime = programRuntime;
		}

		public void startTracking() {
			getProgramRuntime().addListener(this);
			AmstradPc amstradPc = getProgramRuntime().getAmstradPc();
			amstradPc.addStateListener(this);
			amstradPc.getKeyboard().addKeyboardListener(this);
		}

		@Override
		public void amstradPcProgramLoaded(AmstradPc amstradPc) {
			getProgramRuntime().dispose(false); // another program got loaded
		}

		@Override
		public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
			// no action
		}

		@Override
		public void amstradKeyboardBreakEscaped(AmstradKeyboard keyboard) {
			if (getProgramRuntime().isRun()) {
				getProgramRuntime().dispose(true);
			}
		}

		@Override
		public void amstradPcRebooting(AmstradPc amstradPc) {
			getProgramRuntime().dispose(false);
		}

		@Override
		public void amstradPcTerminated(AmstradPc amstradPc) {
			getProgramRuntime().dispose(false);
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
			AmstradPc amstradPc = getProgramRuntime().getAmstradPc();
			amstradPc.removeStateListener(this);
			amstradPc.getKeyboard().removeKeyboardListener(this);
		}

		public AmstradProgramRuntime getProgramRuntime() {
			return programRuntime;
		}

	}

}