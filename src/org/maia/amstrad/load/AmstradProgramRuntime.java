package org.maia.amstrad.load;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public abstract class AmstradProgramRuntime {

	private AmstradProgram program;

	private AmstradPc amstradPc;

	private boolean run;

	private boolean disposed;

	private List<AmstradProgramRuntimeListener> listeners;

	protected AmstradProgramRuntime(AmstradProgram program, AmstradPc amstradPc) {
		this.program = program;
		this.amstradPc = amstradPc;
		this.listeners = new Vector<AmstradProgramRuntimeListener>();
	}

	public void addListener(AmstradProgramRuntimeListener listener) {
		getListeners().add(listener);
	}

	public void removeListener(AmstradProgramRuntimeListener listener) {
		getListeners().remove(listener);
	}

	public final synchronized void run(String... args) throws AmstradProgramException {
		checkNotDisposed();
		List<AmstradProgramRuntimeListener> listeners = getListenersFixedList();
		for (AmstradProgramRuntimeListener listener : listeners) {
			listener.amstradProgramIsAboutToRun(this);
		}
		doRun(args);
		setRun(true);
		for (AmstradProgramRuntimeListener listener : listeners) {
			listener.amstradProgramIsRun(this);
		}
	}

	protected abstract void doRun(String... args) throws AmstradProgramException;

	public synchronized void dispose(boolean programRemainsLoaded) {
		if (!isDisposed()) {
			setDisposed(true);
			for (AmstradProgramRuntimeListener listener : getListenersFixedList()) {
				listener.amstradProgramIsDisposed(this, programRemainsLoaded);
			}
		}
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

	private List<AmstradProgramRuntimeListener> getListenersFixedList() {
		return new Vector<AmstradProgramRuntimeListener>(getListeners());
	}

	protected List<AmstradProgramRuntimeListener> getListeners() {
		return listeners;
	}

}