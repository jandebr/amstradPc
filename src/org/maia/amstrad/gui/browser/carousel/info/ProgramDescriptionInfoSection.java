package org.maia.amstrad.gui.browser.carousel.info;

import org.maia.amstrad.gui.browser.carousel.CarouselComponentFactory;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.util.StringUtils;

public class ProgramDescriptionInfoSection extends ProgramInfoSection {

	public ProgramDescriptionInfoSection(InfoIcon icon, CarouselComponentFactory factory, AmstradProgram program) {
		super(icon, factory, program);
	}

	public static boolean hasInfo(AmstradProgram program) {
		return true;
	}

	@Override
	protected SlidingItemListComponent createInfoComponent() {
		InfoText infoText = createInfoText();
		String text = getProgram().getProgramDescription();
		if (StringUtils.isEmpty(text)) {
			infoText.makeEmpty();
		} else {
			infoText.appendText(text, getTheme().getProgramDescriptionColor(), getTheme().getProgramDescriptionFont());
		}
		return infoText.getComponent();
	}

}