package org.maia.amstrad.pc;

import org.maia.util.GenericListener;

public interface AmstradPcStateListener extends GenericListener {

	void amstradPcStarted(AmstradPc amstradPc);

	void amstradPcPausing(AmstradPc amstradPc);

	void amstradPcResuming(AmstradPc amstradPc);

	void amstradPcRebooting(AmstradPc amstradPc);

	void amstradPcTerminated(AmstradPc amstradPc);

	void amstradPcProgramLoaded(AmstradPc amstradPc);

}