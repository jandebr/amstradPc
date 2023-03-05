package org.maia.amstrad.gui.browser.components;

import java.util.Collection;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.load.basic.staged.file.DiscoveredFileReference;
import org.maia.amstrad.load.basic.staged.file.FileReferenceDiscoveryService;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class ProgramFileReferencesMenuItem extends ProgramMenuItem {

	private boolean failed;

	private boolean attention;

	public ProgramFileReferencesMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "File refs");
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			getBrowser().closeModalWindow();
			getBrowser().openProgramFileReferencesModalWindow(getProgram());
		}
	}

	@Override
	public boolean isEnabled() {
		if (!failed) {
			AmstradProgram program = getProgram();
			try {
				Collection<DiscoveredFileReference> refs = new FileReferenceDiscoveryService(getAmstradPc())
						.discover(program);
				attention = false;
				for (DiscoveredFileReference ref : refs) {
					if (program.lookupFileReference(ref.getSourceFilenameWithoutFlags()) == null) {
						attention = true;
						break;
					}
				}
				return !refs.isEmpty();
			} catch (AmstradProgramException e) {
				System.err.println(e);
				failed = true;
			}
		}
		return false;
	}

	@Override
	public String getLabel() {
		String label = super.getLabel();
		if (failed) {
			label += ' ';
			label += (char) 225;
		} else if (attention) {
			label += ' ';
			label += (char) 187;
		}
		return label;
	}

	@Override
	public int getLabelColor() {
		return attention ? 16 : super.getLabelColor();
	}

	@Override
	public int getFocusBackgroundColor() {
		return attention ? 3 : super.getFocusBackgroundColor();
	}

}