package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradContext;
import org.maia.amstrad.pc.AmstradPc;

public class SaveBasicSourceFileAction extends BasicSourceFileAction {

	public SaveBasicSourceFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Save Basic source file...");
	}

	public SaveBasicSourceFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public SaveBasicSourceFileAction(AmstradPc amstradPc, String name, Icon icon) {
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
						File file = getSelectedFileWithExtension(".bas");
						getAmstradPc().getBasicRuntime().exportSourceCodeToFile(file);
						AmstradContext.printInfoMessage(getAmstradPc(), "Saved " + file.getName());
					} catch (Exception e) {
						System.err.println("Failed to save Basic source file: " + e.getMessage());
					}
				}
			}).start();
		}
	}

}