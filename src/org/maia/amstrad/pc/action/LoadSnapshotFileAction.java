package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradPc;

public class LoadSnapshotFileAction extends SnapshotFileAction {

	public LoadSnapshotFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Load snapshot file...");
	}

	public LoadSnapshotFileAction(AmstradPc amstradPc, String name) {
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
						getAmstradPc().launch(file, true);
					} catch (Exception e) {
						System.err.println("Failed to load snapshot file: " + e.getMessage());
						showErrorMessageDialog("Error loading snapshot file", "Failed to load " + file.getName(), e);
					}
				}
			}).start();
		}
	}

}