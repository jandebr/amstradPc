package org.maia.amstrad.pc.menu;

import java.io.File;

import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradPc;

public abstract class FileChooserAction extends AmstradPcAction {

	private JFileChooser fileChooser;

	protected FileChooserAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
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