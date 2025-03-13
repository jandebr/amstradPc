package org.maia.amstrad.gui.browser.carousel.info;

import org.maia.amstrad.gui.browser.carousel.CarouselComponentFactory;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.animate.textslide.SlidingTextComponent.LinesTextSpacer;
import org.maia.util.StringUtils;

public class ProgramAuthoringInfoSection extends ProgramInfoSection {

	public ProgramAuthoringInfoSection(InfoIcon icon, CarouselComponentFactory factory, AmstradProgram program) {
		super(icon, factory, program);
	}

	public static boolean hasInfo(AmstradProgram program) {
		if (!StringUtils.isEmpty(program.getAuthoringInformation()))
			return true;
		if (!StringUtils.isEmpty(program.getAuthor()))
			return true;
		if (!StringUtils.isEmpty(program.getNameOfTape()))
			return true;
		if (program.getPreferredMonitorMode() != null)
			return true;
		if (program.getBlocksOnTape() > 0)
			return true;
		if (program.getProductionYear() > 0)
			return true;
		return false;
	}

	@Override
	protected SlidingItemListComponent createInfoComponent() {
		InfoText infoText = createInfoText();
		AmstradProgram program = getProgram();
		if (!StringUtils.isEmpty(program.getAuthoringInformation())) {
			infoText.appendText(program.getAuthoringInformation(), getTheme().getProgramAuthoringColor(),
					getTheme().getProgramAuthoringFont());
		}
		if (program.getPreferredMonitorMode() != null) {
			appendKeyValue(infoText, "Monitor", program.getPreferredMonitorMode().name());
		}
		if (!StringUtils.isEmpty(program.getAuthor())) {
			appendKeyValue(infoText, "Author", program.getAuthor());
		}
		if (program.getProductionYear() > 0) {
			appendKeyValue(infoText, "Year", String.valueOf(program.getProductionYear()));
		}
		if (!StringUtils.isEmpty(program.getNameOfTape())) {
			appendKeyValue(infoText, "Tape", program.getNameOfTape());
		}
		if (program.getBlocksOnTape() > 0) {
			appendKeyValue(infoText, "Blocks", String.valueOf(program.getBlocksOnTape()));
		}
		return infoText.getComponent();
	}

	private void appendKeyValue(InfoText infoText, String key, String value) {
		CarouselProgramBrowserTheme theme = getTheme();
		if (!infoText.isEmpty())
			infoText.appendTextSeparator(new LinesTextSpacer(0.5f));
		infoText.appendText(key, theme.getProgramAuthoringKeyColor(), theme.getProgramAuthoringKeyFont(), 1.2f);
		infoText.appendText(value, theme.getProgramAuthoringValueColor(), theme.getProgramAuthoringValueFont());
	}

}