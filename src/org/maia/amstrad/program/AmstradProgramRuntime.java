package org.maia.amstrad.program;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcProgramListener;
import org.maia.amstrad.pc.AmstradPcStateAdapter;

public abstract class AmstradProgramRuntime extends AmstradPcStateAdapter implements AmstradPcProgramListener {

	private AmstradProgram program;

	private AmstradPc amstradPc;

	private boolean run;

	private boolean disposed;

	private List<AmstradProgramRuntimeListener> listeners;

	protected AmstradProgramRuntime(AmstradProgram program, AmstradPc amstradPc) {
		this.program = program;
		this.amstradPc = amstradPc;
		this.listeners = new Vector<AmstradProgramRuntimeListener>();
		amstradPc.addStateListener(this);
		amstradPc.addProgramListener(this);
	}

	public void addListener(AmstradProgramRuntimeListener listener) {
		getListeners().add(listener);
	}

	public void removeListener(AmstradProgramRuntimeListener listener) {
		getListeners().remove(listener);
	}

	public final void run() throws AmstradProgramException {
		checkNotDisposed();
		doRun();
		setRun(true);
		for (AmstradProgramRuntimeListener listener : getListeners()) {
			listener.amstradProgramIsRun(this);
		}
	}

	protected abstract void doRun() throws AmstradProgramException;

	public void dispose() {
		dispose(false);
	}

	private void dispose(boolean programRemainsLoaded) {
		setDisposed(true);
		getAmstradPc().removeStateListener(this);
		getAmstradPc().removeProgramListener(this);
		for (AmstradProgramRuntimeListener listener : getListeners()) {
			listener.amstradProgramIsDisposed(this, programRemainsLoaded);
		}
	}

	@Override
	public void amstradProgramLoaded(AmstradPc amstradPc) {
		dispose(false); // another program got loaded
	}

	@Override
	public void doubleEscapeKey(AmstradPc amstradPc) {
		if (isRun()) {
			dispose(true);
		}
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		dispose(false);
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
		dispose(false);
	}

	protected void checkNotDisposed() {
		if (isDisposed())
			throw new IllegalStateException("This program runtime is disposed");
	}

	public AmstradProgram getProgram() {
		return program;
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	public boolean isRun() {
		return run;
	}

	private void setRun(boolean run) {
		this.run = run;
	}

	public boolean isDisposed() {
		return disposed;
	}

	private void setDisposed(boolean disposed) {
		this.disposed = disposed;
	}

	protected List<AmstradProgramRuntimeListener> getListeners() {
		return listeners;
	}

}