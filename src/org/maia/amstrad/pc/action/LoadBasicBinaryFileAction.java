package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.maia.amstrad.basic.BasicRuntime;
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
			runInSeparateThread(new Runnable() {
				@Override
				public void run() {
					BasicRuntime rt = getAmstradPc().getBasicRuntime();
					if (!rt.isReady()) {
						showErrorMessageDialog("Cannot load now",
								"Another program is still running. Stop it and then retry");
					} else {
						File file = getSelectedFile();
						try {
							getAmstradPc().getTape().loadByteCodeFromFile(file);
						} catch (Exception e) {
							System.err.println("Failed to load Basic binary file: " + e.getMessage());
							showErrorMessageDialog("Error loading Basic binary file",
									"Failed to load " + file.getName(), e);
						}
					}
				}
			});
		}
	}

}