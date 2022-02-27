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

	protected File getSelectedFileWithExtension(String... allowedFileExtensions) {
		File file = getSelectedFile();
		if (file != null) {
			boolean allowed = false;
			String fname = file.getName().toLowerCase();
			for (String ext : allowedFileExtensions) {
				if (fname.endsWith(ext))
					allowed = true;
			}
			if (!allowed) {
				file = new File(file.getPath() + allowedFileExtensions[0]);
			}
		}
		return file;
	}

	protected File getSelectedFile() {
		return getFileChooser().getSelectedFile();
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