package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.pc.AmstradPc;

public class AutoTypeFileAction extends FileChooserAction {

	public AutoTypeFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Autotype from file...");
	}

	public AutoTypeFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public AutoTypeFileAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int returnValue = getFileChooser().showOpenDialog(getAmstradPc().getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = getSelectedFile();
					try {
						getAmstradPc().getBasicRuntime().keyboardTypeFileContents(file);
					} catch (Exception e) {
						System.err.println("Failed to read text file: " + e.getMessage());
						showErrorMessageDialog("Error reading text file", "Failed to read " + file.getName(), e);
					}
				}
			}).start();
		}
	}

	@Override
	protected JFileChooser buildFileChooser() {
		JFileChooser fileChooser = new JFileChooser(getHomeDirectory());
		fileChooser.setDialogTitle(getName());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files (*.txt, *.bas)", "txt", "bas");
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

}