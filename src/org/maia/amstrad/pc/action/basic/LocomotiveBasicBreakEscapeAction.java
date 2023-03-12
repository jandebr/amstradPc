package org.maia.amstrad.pc.action.basic;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class LocomotiveBasicBreakEscapeAction extends LocomotiveBasicAction {

	public LocomotiveBasicBreakEscapeAction(AmstradPc amstradPc) {
		this(amstradPc, "Break ESC");
	}

	public LocomotiveBasicBreakEscapeAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		runInSeparateThread(new Runnable() {

			@Override
			public void run() {
				getBasicRuntime().breakEscape();
			}
		});
	}

}