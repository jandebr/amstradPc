package org.maia.amstrad.pc.action;

import java.io.File;

import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.util.AmstradIO;

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
		File currentDir = getCurrentDirectory();
		if (fileChooser == null) {
			fileChooser = buildFileChooser(currentDir);
		} else {
			File chooserCurrentDir = fileChooser.getCurrentDirectory();
			if (chooserCurrentDir == null || !chooserCurrentDir.equals(currentDir)) {
				fileChooser.setCurrentDirectory(currentDir);
			}
		}
		return fileChooser;
	}

	protected abstract JFileChooser buildFileChooser(File currentDirectory);

	protected boolean checkFileOutputDestination(File file) {
		if (isFileInsideManagedFolder(file)) {
			showErrorMessageDialog("Invalid destination",
					"Cannot write inside the managed folder "
							+ getAmstradContext().getManagedProgramRepositoryRootFolder().getAbsolutePath()
							+ "\nTry using a different destination");
			return false;
		} else {
			return true;
		}
	}

	protected boolean isFileInsideManagedFolder(File file) {
		File managedFolder = getAmstradContext().getManagedProgramRepositoryRootFolder();
		if (managedFolder != null) {
			return AmstradIO.isFileInsideFolder(file, managedFolder);
		} else {
			return false;
		}
	}

	protected void updateCurrentDirectoryFromSelectedFile() {
		File file = getSelectedFile();
		if (file != null) {
			setCurrentDirectory(file.getParentFile());
		}
	}

	private File getCurrentDirectory() {
		return getAmstradContext().getCurrentDirectory();
	}

	private void setCurrentDirectory(File currentDirectory) {
		getAmstradContext().setCurrentDirectory(currentDirectory);
	}

}