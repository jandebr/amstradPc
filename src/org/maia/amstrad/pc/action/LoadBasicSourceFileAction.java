package org.maia.amstrad.pc.action;

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
		int returnValue = getFileChooser().showOpenDialog(getDisplayComponent());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			updateCurrentDirectoryFromSelectedFile();
			runInSeparateThread(new Runnable() {
				@Override
				public void run() {
					if (!getAmstradPc().getBasicRuntime().isReady()) {
						showErrorMessageDialog("Cannot load now",
								"Another program is still running. Stop it and then retry");
					} else {
						File file = getSelectedFile();
						try {
							getAmstradPc().getTape().loadSourceCodeFromFile(file);
						} catch (Exception e) {
							System.err.println("Failed to load Basic source file: " + e.getMessage());
							showErrorMessageDialog("Error loading Basic source file",
									"Failed to load " + file.getName(), e);
						}
					}
				}
			});
		}
	}

}