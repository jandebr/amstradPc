package org.maia.amstrad.pc.action.basic;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class LocomotiveBasicNewAction extends LocomotiveBasicAction {

	public LocomotiveBasicNewAction(AmstradPc amstradPc) {
		this(amstradPc, "NEW");
	}

	public LocomotiveBasicNewAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		runInSeparateThread(new Runnable() {

			@Override
			public void run() {
				getBasicRuntime().command_new();
			}
		});
	}

}