package org.maia.amstrad.jemu.menu;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.maia.amstrad.jemu.AmstradPc;

public abstract class FileChooserAction extends AmstradPcAction {

	private JFileChooser fileChooser;

	protected FileChooserAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	protected FileChooserAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = buildFileChooser();
		}
		return fileChooser;
	}

	protected abstract JFileChooser buildFileChooser();

	protected File getHomeDirectory() {
		return new File(".");
	}

}