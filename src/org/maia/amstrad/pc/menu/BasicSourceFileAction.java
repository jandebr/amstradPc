package org.maia.amstrad.pc.menu;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.pc.AmstradPc;

public abstract class BasicSourceFileAction extends FileChooserAction {

	protected BasicSourceFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	protected BasicSourceFileAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	protected JFileChooser buildFileChooser() {
		JFileChooser fileChooser = new JFileChooser(getHomeDirectory());
		fileChooser.setDialogTitle(getName());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Basic source files (*.bas, *.txt)", "bas", "txt");
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

}