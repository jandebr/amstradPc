package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.io.AmstradFileType;
import org.maia.amstrad.pc.AmstradPc;

public class AutoTypeFileAction extends FileChooserAction {

	public AutoTypeFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Autotype from file...");
	}

	public AutoTypeFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int returnValue = getFileChooser().showOpenDialog(getAmstradPc().getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			updateCurrentDirectoryFromSelectedFile();
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = getSelectedFile();
					try {
						getAmstradPc().getKeyboard().typeFileContents(file);
					} catch (Exception e) {
						System.err.println("Failed to read text file: " + e.getMessage());
						showErrorMessageDialog("Error reading text file", "Failed to read " + file.getName(), e);
					}
				}
			}).start();
		}
	}

	@Override
	protected JFileChooser buildFileChooser(File currentDirectory) {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		fileChooser.setDialogTitle(getName());
		String extText = "txt";
		String extBasic = AmstradFileType.BASIC_SOURCE_CODE_FILE.getFileExtensionWithoutDot();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Text files (*." + extText + ", *." + extBasic + ")", extText, extBasic);
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

}