package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.pc.AmstradPc;

public abstract class LocomotiveBasicRuntime extends BasicRuntime {

	public LocomotiveBasicRuntime(AmstradPc amstradPc) {
		super(amstradPc);
	}

	public void cls() {
		getKeyboard().enter("CLS");
	}

	public void list() {
		getKeyboard().enter("LIST");
	}

	public void run() {
		getKeyboard().enter("RUN");
	}

	public void run(int lineNumber) {
		getKeyboard().enter("RUN " + lineNumber);
	}

}