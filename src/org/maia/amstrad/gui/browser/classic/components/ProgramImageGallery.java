package org.maia.amstrad.gui.browser.classic.components;

import org.maia.amstrad.gui.components.ScrollableItemList;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.util.StringUtils;

public class ProgramImageGallery extends ScrollableItemList<AmstradProgramImage> {

	private AmstradProgram program;

	public ProgramImageGallery(AmstradProgram program) {
		super(1);
		this.program = program;
	}

	@Override
	public int size() {
		return getProgram().getImages().size();
	}

	@Override
	public AmstradProgramImage getItem(int index) {
		return getProgram().getImages().get(index);
	}

	public boolean hasCaptions() {
		for (int i = 0; i < size(); i++) {
			if (!StringUtils.isEmpty(getItem(i).getCaption()))
				return true;
		}
		return false;
	}

	public AmstradProgram getProgram() {
		return program;
	}

}