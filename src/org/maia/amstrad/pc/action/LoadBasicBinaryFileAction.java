package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradPc;

public class LoadBasicBinaryFileAction extends BasicBinaryFileAction {

	public LoadBasicBinaryFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Load Basic binary file...");
	}

	public LoadBasicBinaryFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int returnValue = getFileChooser().showOpenDialog(getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			updateCurrentDirectoryFromSelectedFile();
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = getSelectedFile();
					try {
						getAmstradPc().getBasicRuntime().loadByteCodeFromFile(file);
					} catch (Exception e) {
						System.err.println("Failed to load Basic binary file: " + e.getMessage());
						showErrorMessageDialog("Error loading Basic binary file", "Failed to load " + file.getName(),
								e);
					}
				}
			}).start();
		}
	}

}