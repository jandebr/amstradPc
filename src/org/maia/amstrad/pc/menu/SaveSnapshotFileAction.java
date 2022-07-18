package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradPc;

public class SaveSnapshotFileAction extends SnapshotFileAction {

	public SaveSnapshotFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Save snapshot file...");
	}

	public SaveSnapshotFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public SaveSnapshotFileAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int returnValue = getFileChooser().showSaveDialog(getAmstradPc().getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = getSelectedFileWithExtension(".sna", ".snz");
					try {
						getAmstradPc().saveSnapshot(file);
					} catch (Exception e) {
						System.err.println("Failed to save snapshot file: " + e.getMessage());
						showErrorMessageDialog("Error saving snapshot file", "Failed to save " + file.getName(), e);
					}
				}
			}).start();
		}
	}

}