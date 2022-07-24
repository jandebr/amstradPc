package org.maia.amstrad.pc;

import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;

public interface AmstradPcStateListener {

	void amstradPcStarted(AmstradPc amstradPc);

	void amstradPcPausing(AmstradPc amstradPc);

	void amstradPcResuming(AmstradPc amstradPc);

	void amstradPcRebooting(AmstradPc amstradPc);

	void amstradPcTerminated(AmstradPc amstradPc);

	void amstradPcDisplaySourceChanged(AmstradPc amstradPc, AmstradAlternativeDisplaySource alternativeDisplaySource);

}