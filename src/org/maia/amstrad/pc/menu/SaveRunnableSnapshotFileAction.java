package org.maia.amstrad.pc.menu;

import javax.swing.Icon;

import org.maia.amstrad.pc.AmstradPc;

public class SaveRunnableSnapshotFileAction extends SaveSnapshotFileAction {

	public SaveRunnableSnapshotFileAction(AmstradPc amstradPc) {
		this(amstradPc, "Make runnable snapshot file...");
	}

	public SaveRunnableSnapshotFileAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public SaveRunnableSnapshotFileAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	protected boolean isSnapshotRunnable() {
		return true;
	}

}