package org.maia.amstrad.basic.locomotive.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class LocomotiveBasicClsAction extends LocomotiveBasicAction {

	public LocomotiveBasicClsAction(AmstradPc amstradPc) {
		this(amstradPc, "CLS");
	}

	public LocomotiveBasicClsAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		runInSeparateThread(new Runnable() {

			@Override
			public void run() {
				getBasicRuntime().command_cls();
			}
		});
	}

}