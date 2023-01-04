package org.maia.amstrad.basic.locomotive.action;

import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;

public abstract class LocomotiveBasicAction extends AmstradPcAction {

	public LocomotiveBasicAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		if (!amstradPc.getBasicRuntime().getLanguage().equals(BasicLanguage.LOCOMOTIVE_BASIC))
			throw new IllegalArgumentException("AmstradPc does not have a Locomotive Basic runtime");
	}

	protected LocomotiveBasicRuntime getBasicRuntime() {
		return (LocomotiveBasicRuntime) getAmstradPc().getBasicRuntime();
	}

}