package org.maia.amstrad.gui.browser.carousel.info;

import java.util.List;

import org.maia.amstrad.gui.browser.carousel.CarouselLayoutManager;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.UserControl;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.animate.textslide.SlidingTextComponent.LinesTextSpacer;
import org.maia.util.StringUtils;

public class ProgramControlsInfoSection extends ProgramInfoSection {

	public static String defaultHeading = "User controls";

	public ProgramControlsInfoSection(InfoIcon icon, CarouselProgramBrowserTheme theme, CarouselLayoutManager layout,
			AmstradProgram program) {
		super(icon, theme, layout, program);
	}

	public static boolean hasInfo(AmstradProgram program) {
		return !program.getUserControls().isEmpty();
	}

	@Override
	protected SlidingItemListComponent createInfoComponent() {
		InfoText infoText = createInfoText();
		LinesTextSpacer keySpacer = new LinesTextSpacer(0.5f);
		LinesTextSpacer headingSpacer = new LinesTextSpacer(1f);
		CarouselProgramBrowserTheme theme = getTheme();
		List<UserControl> controls = getProgram().getUserControls();
		for (int i = 0; i < controls.size(); i++) {
			UserControl control = controls.get(i);
			String heading = control.getHeading();
			if (i == 0 && StringUtils.isEmpty(heading)) {
				heading = defaultHeading;
			}
			if (!StringUtils.isEmpty(heading)) {
				if (!infoText.isEmpty())
					infoText.appendTextSeparator(headingSpacer);
				infoText.appendText(heading, theme.getProgramControlHeadingColor(),
						theme.getProgramControlHeadingFont(), 1.2f);
			}
			infoText.appendTextSeparator(keySpacer);
			infoText.appendText(control.getKey(), theme.getProgramControlKeyColor(), theme.getProgramControlKeyFont());
			infoText.appendText(control.getDescription(), theme.getProgramControlDescriptionColor(),
					theme.getProgramControlDescriptionFont());
		}
		return infoText.getComponent();
	}

}