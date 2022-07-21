package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.TestDisplaySource;

public class AlternativeDisplaySourceAction extends AmstradPcAction {

	public AlternativeDisplaySourceAction(AmstradPc amstradPc) {
		this(amstradPc, "Alternative display");
	}

	public AlternativeDisplaySourceAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public AlternativeDisplaySourceAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		if (state) {
			getAmstradPc().changeDisplaySource(new TestDisplaySource());
		} else {
			getAmstradPc().resetDisplaySource();
		}
	}

}