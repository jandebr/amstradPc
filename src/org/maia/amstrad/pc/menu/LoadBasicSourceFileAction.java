package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.maia.amstrad.pc.AmstradContext;
import org.maia.amstrad.pc.AmstradPc;

public class LoadBasicSourceFileAction extends BasicSourceFileAction {

	public LoadBasicSourceFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Load Basic source file...");
	}

	public LoadBasicSourceFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public LoadBasicSourceFileAction(AmstradPc amstradPc, String name, Icon icon) {
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
						File file = getSelectedFile();
						getAmstradPc().getBasicRuntime().loadSourceCodeFromFile(file);
						AmstradContext.printInfoMessage(getAmstradPc(), "Loaded " + file.getName());
					} catch (Exception e) {
						System.err.println("Failed to load Basic source file: " + e.getMessage());
					}
				}
			}).start();
		}
	}

}