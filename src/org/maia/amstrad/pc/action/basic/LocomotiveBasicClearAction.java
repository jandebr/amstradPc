package org.maia.amstrad.pc.action.basic;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class LocomotiveBasicClearAction extends LocomotiveBasicAction {

	public LocomotiveBasicClearAction(AmstradPc amstradPc) {
		this(amstradPc, "CLEAR");
	}

	public LocomotiveBasicClearAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		runInSeparateThread(new Runnable() {

			@Override
			public void run() {
				getBasicRuntime().command_clear();
			}
		});
	}

}