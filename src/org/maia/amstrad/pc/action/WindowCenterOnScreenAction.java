package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.frame.AmstradPcFrame;

public class WindowCenterOnScreenAction extends AmstradPcAction {

	public WindowCenterOnScreenAction(AmstradPc amstradPc) {
		this(amstradPc, "Center on screen");
	}

	public WindowCenterOnScreenAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		AmstradPcFrame frame = getAmstradPc().getFrame();
		if (frame != null)
			frame.centerOnScreen();
	}

}