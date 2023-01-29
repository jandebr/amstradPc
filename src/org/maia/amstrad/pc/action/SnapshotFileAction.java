package org.maia.amstrad.pc.action;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.AmstradFileType;
import org.maia.amstrad.pc.AmstradPc;

public abstract class SnapshotFileAction extends FileChooserAction {

	protected SnapshotFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	protected JFileChooser buildFileChooser(File currentDirectory) {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		fileChooser.setDialogTitle(getName());
		String extU = AmstradFileType.JAVACPC_SNAPSHOT_FILE_UNCOMPRESSED.getFileExtensionWithoutDot();
		String extC = AmstradFileType.JAVACPC_SNAPSHOT_FILE_COMPRESSED.getFileExtensionWithoutDot();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Snapshot files (*." + extU + ", *." + extC + ")",
				extU, extC);
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

}