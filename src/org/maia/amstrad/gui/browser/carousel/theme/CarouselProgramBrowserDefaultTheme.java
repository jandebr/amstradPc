package org.maia.amstrad.gui.browser.carousel.theme;

import java.awt.Color;
import java.awt.Font;

import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class CarouselProgramBrowserDefaultTheme implements CarouselProgramBrowserTheme {

	private AmstradGraphicsContext graphicsContext;

	private static Font defaultFont = Font.decode("Arial-PLAIN-20");

	private static Font carouselFont = Font.decode("Century-PLAIN-24");

	private static Color COLOR_EARTH_1 = new Color(26, 22, 20);

	private static Color COLOR_EARTH_2 = new Color(65, 53, 46);

	private static Color COLOR_EARTH_3 = new Color(91, 67, 52);

	private static Color COLOR_EARTH_4 = new Color(186, 148, 120);

	public CarouselProgramBrowserDefaultTheme(AmstradGraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
	}

	@Override
	public Color getBackgroundColor() {
		return Color.BLACK;
	}

	@Override
	public Font getDefaultFont() {
		return defaultFont;
	}

	@Override
	public Font getHeadingFont() {
		return getDefaultFont();
	}

	@Override
	public Color getHeadingColor() {
		return Color.WHITE;
	}

	@Override
	public Font getCaptionFont() {
		return getDefaultFont().deriveFont(Font.BOLD);
	}

	@Override
	public Color getCaptionColor() {
		return COLOR_EARTH_4;
	}

	@Override
	public Color getCaptionConjunctionColor() {
		return COLOR_EARTH_3;
	}

	@Override
	public int getInfoPreferredLinesInView() {
		return 10;
	}

	@Override
	public int getInfoInterParagraphSpacing() {
		return 6;
	}

	@Override
	public int getInfoInterLineSpacing() {
		return 0;
	}

	@Override
	public float getInfoOverflowGradientLines() {
		return 1f;
	}

	@Override
	public float getInfoMinimumFontSize() {
		return 16f;
	}

	@Override
	public float getInfoMaximumFontSize() {
		return 30f;
	}

	@Override
	public Color getInfoOutlineBorderColor() {
		return COLOR_EARTH_2;
	}

	@Override
	public Color getInfoOutlineCursorBorderColor() {
		return COLOR_EARTH_2;
	}

	@Override
	public Color getInfoOutlineCursorFillColor() {
		return COLOR_EARTH_3;
	}

	@Override
	public Font getProgramDescriptionFont() {
		return getDefaultFont();
	}

	@Override
	public Color getProgramDescriptionColor() {
		return Color.WHITE;
	}

	@Override
	public Font getProgramControlHeadingFont() {
		return getDefaultFont().deriveFont(Font.BOLD);
	}

	@Override
	public Color getProgramControlHeadingColor() {
		return new Color(222, 193, 164);
	}

	@Override
	public Font getProgramControlKeyFont() {
		return getGraphicsContext().getSystemFont();
	}

	@Override
	public Color getProgramControlKeyColor() {
		return new Color(252, 127, 3);
	}

	@Override
	public Font getProgramControlDescriptionFont() {
		return getDefaultFont();
	}

	@Override
	public Color getProgramControlDescriptionColor() {
		return new Color(255, 177, 99);
	}

	@Override
	public Font getProgramAuthoringFont() {
		return getDefaultFont();
	}

	@Override
	public Color getProgramAuthoringColor() {
		return Color.WHITE;
	}

	@Override
	public Font getProgramAuthoringKeyFont() {
		return getDefaultFont().deriveFont(Font.BOLD);
	}

	@Override
	public Color getProgramAuthoringKeyColor() {
		return new Color(56, 99, 34);
	}

	@Override
	public Font getProgramAuthoringValueFont() {
		return getDefaultFont();
	}

	@Override
	public Color getProgramAuthoringValueColor() {
		return new Color(146, 242, 97);
	}

	@Override
	public Font getFolderInfoSubfolderFont() {
		return getGraphicsContext().getSystemFont();
	}

	@Override
	public Color getFolderInfoSubfolderColor() {
		return new Color(119, 113, 176);
	}

	@Override
	public Font getFolderInfoProgramFont() {
		return getGraphicsContext().getSystemFont();
	}

	@Override
	public Color getFolderInfoProgramColor() {
		return new Color(184, 178, 237);
	}

	@Override
	public Font getCarouselFont() {
		return carouselFont;
	}

	@Override
	public Color getCarouselCursorColor() {
		return Color.YELLOW;
	}

	@Override
	public Color getCarouselOutlineBorderColor() {
		return COLOR_EARTH_1;
	}

	@Override
	public Color getCarouselOutlineCursorBorderColor() {
		return COLOR_EARTH_1;
	}

	@Override
	public Color getCarouselOutlineCursorFillColor() {
		return COLOR_EARTH_1;
	}

	@Override
	public Font getBreadcrumbFont() {
		return getCarouselFont();
	}

	@Override
	public Color getBreadcrumbCursorColor() {
		return COLOR_EARTH_1;
	}

	@Override
	public Color getBreadcrumbFolderColor() {
		return COLOR_EARTH_2;
	}

	@Override
	public Color getBreadcrumbSelectedFolderColor() {
		return Color.WHITE;
	}

	@Override
	public Color getBreadcrumbSeparatorColor() {
		return COLOR_EARTH_1;
	}

	private AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

}