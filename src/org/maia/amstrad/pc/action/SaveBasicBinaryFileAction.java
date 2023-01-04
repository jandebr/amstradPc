package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.maia.amstrad.io.AmstradFileType;
import org.maia.amstrad.pc.AmstradPc;

public class SaveBasicBinaryFileAction extends BasicBinaryFileAction {

	public SaveBasicBinaryFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Save Basic binary file...");
	}

	public SaveBasicBinaryFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int returnValue = getFileChooser().showSaveDialog(getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			updateCurrentDirectoryFromSelectedFile();
			runInSeparateThread(new Runnable() {
				@Override
				public void run() {
					File file = getSelectedFileWithExtension(AmstradFileType.BASIC_BYTE_CODE_FILE.getFileExtension());
					try {
						getAmstradPc().getBasicRuntime().exportByteCodeToFile(file);
					} catch (Exception e) {
						System.err.println("Failed to save Basic binary file: " + e.getMessage());
						showErrorMessageDialog("Error saving Basic binary file", "Failed to save " + file.getName(), e);
					}
				}
			});
		}
	}

}