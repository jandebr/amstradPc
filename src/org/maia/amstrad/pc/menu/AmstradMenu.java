package org.maia.amstrad.pc.menu;

import org.maia.amstrad.pc.AmstradPc;

public interface AmstradMenu {

	void install();

	void uninstall();

	AmstradPc getAmstradPc();

}