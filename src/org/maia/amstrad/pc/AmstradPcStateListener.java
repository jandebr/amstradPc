package org.maia.amstrad.pc;

import org.maia.amstrad.util.AmstradListener;

public interface AmstradPcStateListener extends AmstradListener {

	void amstradPcStarted(AmstradPc amstradPc);

	void amstradPcPausing(AmstradPc amstradPc);

	void amstradPcResuming(AmstradPc amstradPc);

	void amstradPcRebooting(AmstradPc amstradPc);

	void amstradPcTerminated(AmstradPc amstradPc);

	void amstradPcProgramLoaded(AmstradPc amstradPc);

}