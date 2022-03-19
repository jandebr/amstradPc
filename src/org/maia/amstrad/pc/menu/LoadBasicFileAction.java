package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.pc.AmstradPc;

public class LoadBasicFileAction extends FileChooserAction {

	public LoadBasicFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Load Basic file...");
	}

	public LoadBasicFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public LoadBasicFileAction(AmstradPc amstradPc, String name, Icon icon) {
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
						getAmstradPc().getBasicRuntime().load(getFileChooser().getSelectedFile());
					} catch (Exception e) {
						System.err.println("Failed to load Basic source file: " + e.getMessage());
					}
				}
			}).start();
		}
	}

	@Override
	protected JFileChooser buildFileChooser() {
		JFileChooser fileChooser = new JFileChooser(getHomeDirectory());
		fileChooser.setDialogTitle(getName());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Basic source files (*.bas, *.txt)", "bas", "txt");
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

}