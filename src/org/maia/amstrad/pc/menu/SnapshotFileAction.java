package org.maia.amstrad.pc.menu;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.pc.AmstradPc;

public abstract class SnapshotFileAction extends FileChooserAction {

	protected SnapshotFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	protected SnapshotFileAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	protected JFileChooser buildFileChooser() {
		JFileChooser fileChooser = new JFileChooser(getHomeDirectory());
		fileChooser.setDialogTitle(getName());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Snapshot files (*.sna, *.snz)", "sna", "snz");
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

}