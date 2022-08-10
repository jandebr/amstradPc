package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradPc;

public class LoadBasicSourceFileAction extends BasicSourceFileAction {

	public LoadBasicSourceFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Load Basic source file...");
	}

	public LoadBasicSourceFileAction(AmstradPc amstradPc, String name) {
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
						getAmstradPc().getBasicRuntime().loadSourceCodeFromFile(file);
					} catch (Exception e) {
						System.err.println("Failed to load Basic source file: " + e.getMessage());
						showErrorMessageDialog("Error loading Basic source file", "Failed to load " + file.getName(), e);
					}
				}
			}).start();
		}
	}

}