package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradPc;

public class SaveBasicFileAction extends BasicFileAction {

	public SaveBasicFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Save Basic file...");
	}

	public SaveBasicFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public SaveBasicFileAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int returnValue = getFileChooser().showSaveDialog(getAmstradPc().getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						getAmstradPc().getBasicRuntime().save(getSelectedFileWithExtension(".bas", ".txt"));
					} catch (Exception e) {
						System.err.println("Failed to save Basic source file: " + e.getMessage());
					}
				}
			}).start();
		}
	}

}