package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradPc;

public class SaveBasicBinaryFileAction extends BasicBinaryFileAction {

	public SaveBasicBinaryFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Save Basic binary file...");
	}

	public SaveBasicBinaryFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public SaveBasicBinaryFileAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int returnValue = getFileChooser().showSaveDialog(getAmstradPc().getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = getSelectedFileWithExtension(".bin");
					try {
						getAmstradPc().getBasicRuntime().exportByteCodeToFile(file);
					} catch (Exception e) {
						System.err.println("Failed to save Basic binary file: " + e.getMessage());
						showErrorMessageDialog("Error saving Basic binary file", "Failed to save " + file.getName(), e);
					}
				}
			}).start();
		}
	}

}