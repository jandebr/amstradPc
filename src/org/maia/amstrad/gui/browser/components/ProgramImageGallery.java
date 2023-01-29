package org.maia.amstrad.gui.browser.components;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.ProgramImage;
import org.maia.amstrad.util.StringUtils;

public class ProgramImageGallery extends ItemList {

	private AmstradProgram program;

	public ProgramImageGallery(AmstradProgram program) {
		super(1);
		this.program = program;
	}

	public ProgramImage getCurrentImage() {
		return getImage(getIndexOfSelectedItem());
	}

	public ProgramImage getImage(int index) {
		return getProgram().getImages().get(index);
	}

	@Override
	public int size() {
		return getProgram().getImages().size();
	}

	public boolean hasCaptions() {
		for (int i = 0; i < size(); i++) {
			if (!StringUtils.isEmpty(getImage(i).getCaption()))
				return true;
		}
		return false;
	}

	public AmstradProgram getProgram() {
		return program;
	}

}