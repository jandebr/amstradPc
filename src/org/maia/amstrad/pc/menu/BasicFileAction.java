package org.maia.amstrad.pc.menu;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.pc.AmstradPc;

public abstract class BasicFileAction extends FileChooserAction {

	protected BasicFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	protected BasicFileAction(AmstradPc amstradPc, String name, Icon icon) {
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