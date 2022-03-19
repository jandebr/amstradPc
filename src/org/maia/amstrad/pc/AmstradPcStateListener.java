package org.maia.amstrad.pc;

public interface AmstradPcStateListener {

	void amstradPcStarted(AmstradPc amstradPc);

	void amstradPcPausing(AmstradPc amstradPc);

	void amstradPcResuming(AmstradPc amstradPc);

	void amstradPcRebooting(AmstradPc amstradPc);

	void amstradPcTerminated(AmstradPc amstradPc);

}