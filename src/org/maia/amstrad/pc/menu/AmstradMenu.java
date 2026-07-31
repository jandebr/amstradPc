package org.maia.amstrad.pc.menu;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;

public interface AmstradMenu {

	void install();

	void uninstall();

	boolean containsAction(AmstradPcAction action);

	AmstradPc getAmstradPc();

}