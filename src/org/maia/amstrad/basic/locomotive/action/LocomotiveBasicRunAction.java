package org.maia.amstrad.basic.locomotive.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class LocomotiveBasicRunAction extends LocomotiveBasicAction {

	public LocomotiveBasicRunAction(AmstradPc amstradPc) {
		this(amstradPc, "RUN");
	}

	public LocomotiveBasicRunAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		runInSeparateThread(new Runnable() {

			@Override
			public void run() {
				getBasicRuntime().run();
			}
		});
	}

}