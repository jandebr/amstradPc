package org.maia.amstrad.pc.action;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.io.util.IOUtils;

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
			fileChooser.addAncestorListener(new AncestorListener() {

				@Override
				public void ancestorRemoved(AncestorEvent event) {
					// A dialog (such as the ones created by JFileChooser) catches key events when in focus. When the
					// dialog is invoked by a key combination involving modifiers, this may leave the JEMU instance and
					// JEMU computer in an obsolete key modifier state causing artefacts when resuming focus. To prevent
					// this, we reset modifiers when a dialog is closed.
					getAmstradPc().getKeyboard().getController().resetKeyModifiers();
				}

				@Override
				public void ancestorMoved(AncestorEvent event) {
				}

				@Override
				public void ancestorAdded(AncestorEvent event) {
				}
			});
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
			return IOUtils.isFileInsideFolder(file, managedFolder);
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