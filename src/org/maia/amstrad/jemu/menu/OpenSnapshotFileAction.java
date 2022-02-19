package org.maia.amstrad.jemu.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.maia.amstrad.jemu.AmstradPc;

public class OpenSnapshotFileAction extends SnapshotFileAction {

	public OpenSnapshotFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Open snapshot file...");
	}

	public OpenSnapshotFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public OpenSnapshotFileAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int returnValue = getFileChooser().showOpenDialog(getAmstradPc().getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						getAmstradPc().launch(getFileChooser().getSelectedFile());
					} catch (Exception e) {
						System.err.println("Failed to open snapshot file: " + e.getMessage());
					}
				}
			}).start();
		}
	}

}