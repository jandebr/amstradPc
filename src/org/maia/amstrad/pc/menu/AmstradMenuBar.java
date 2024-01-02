package org.maia.amstrad.pc.menu;

import javax.swing.JMenuBar;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;

public class AmstradMenuBar extends JMenuBar implements AmstradMenu {

	private AmstradPc amstradPc;

	public AmstradMenuBar(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	@Override
	public void install() {
		AmstradPcFrame frame = getFrame();
		if (frame != null) {
			frame.installMenuBar(this);
		}
	}

	@Override
	public void uninstall() {
		AmstradPcFrame frame = getFrame();
		if (frame != null) {
			frame.uninstallMenuBar();
		}
	}

	protected AmstradPcFrame getFrame() {
		return getAmstradPc().getFrame();
	}

	@Override
	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}