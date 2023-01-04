package org.maia.amstrad.basic.locomotive.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class LocomotiveBasicListAction extends LocomotiveBasicAction {

	public LocomotiveBasicListAction(AmstradPc amstradPc) {
		this(amstradPc, "LIST");
	}

	public LocomotiveBasicListAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		runInSeparateThread(new Runnable() {

			@Override
			public void run() {
				getBasicRuntime().command_list();
			}
		});
	}

}