package org.maia.amstrad.gui.browser.carousel.theme;

import java.awt.Color;
import java.awt.Font;

import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.util.ColorUtils;

public class CarouselProgramBrowserDefaultTheme implements CarouselProgramBrowserTheme, CarouselDefaultColorPalette {

	private AmstradGraphicsContext graphicsContext;

	private static Font defaultFont = Font.decode("Arial-PLAIN-20");

	private static Font carouselFolderFont = Font.decode("Dialog-BOLD-24");

	private static Font breadcrumbFont = Font.decode("Century-PLAIN-24");

	public CarouselProgramBrowserDefaultTheme(AmstradGraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
	}

	@Override
	public Color getBackgroundColor() {
		return COLOR_2;
	}

	@Override
	public Font getDefaultFont() {
		return defaultFont;
	}

	@Override
	public Font getHeadingFont() {
		return getDefaultFont().deriveFont(Font.BOLD);
	}

	@Override
	public Color getHeadingColor() {
		return COLOR_1;
	}

	@Override
	public Font getCaptionFont() {
		return getDefaultFont().deriveFont(Font.BOLD);
	}

	@Override
	public Color getCaptionColor() {
		return COLOR_7;
	}

	@Override
	public Color getCaptionConjunctionColor() {
		return COLOR_8;
	}

	@Override
	public float getCaptionTextScale() {
		return 0.8f;
	}

	@Override
	public float getInfoIconUnselectedScale() {
		return 0.7f;
	}

	@Override
	public Color getInfoIconUnselectedBackgroundColor() {
		return getBackgroundColor();
	}

	@Override
	public Color getInfoIconSelectedBackgroundColor() {
		return getBackgroundColor();
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
		return getOutlineBorderColor();
	}

	@Override
	public Color getInfoOutlineCursorBorderColor() {
		return getOutlineCursorBorderColor();
	}

	@Override
	public Color getInfoOutlineCursorFillColor() {
		return getOutlineCursorFillColor();
	}

	@Override
	public Font getProgramDescriptionFont() {
		return getDefaultFont();
	}

	@Override
	public Color getProgramDescriptionColor() {
		return COLOR_12;
	}

	@Override
	public Font getProgramControlHeadingFont() {
		return getDefaultFont().deriveFont(Font.BOLD);
	}

	@Override
	public Color getProgramControlHeadingColor() {
		return COLOR_11;
	}

	@Override
	public Font getProgramControlKeyFont() {
		return getGraphicsContext().getSystemFont();
	}

	@Override
	public Color getProgramControlKeyColor() {
		return COLOR_10;
	}

	@Override
	public Font getProgramControlDescriptionFont() {
		return getDefaultFont();
	}

	@Override
	public Color getProgramControlDescriptionColor() {
		return COLOR_9;
	}

	@Override
	public Font getProgramAuthoringFont() {
		return getDefaultFont();
	}

	@Override
	public Color getProgramAuthoringColor() {
		return COLOR_12;
	}

	@Override
	public Font getProgramAuthoringKeyFont() {
		return getDefaultFont().deriveFont(Font.BOLD);
	}

	@Override
	public Color getProgramAuthoringKeyColor() {
		return COLOR_17;
	}

	@Override
	public Font getProgramAuthoringValueFont() {
		return getDefaultFont();
	}

	@Override
	public Color getProgramAuthoringValueColor() {
		return COLOR_16;
	}

	@Override
	public Font getFolderInfoSubfolderFont() {
		return getFolderInfoFont();
	}

	@Override
	public Color getFolderInfoSubfolderColor() {
		return COLOR_13;
	}

	@Override
	public Font getFolderInfoProgramFont() {
		return getFolderInfoFont();
	}

	@Override
	public Color getFolderInfoProgramColor() {
		return COLOR_12;
	}

	@Override
	public Font getCarouselEmptyFolderFont() {
		return getDefaultFont();
	}

	@Override
	public Color getCarouselEmptyFolderColor() {
		return COLOR_7;
	}

	@Override
	public Font getCarouselFolderTitleFont() {
		return carouselFolderFont;
	}

	@Override
	public Color getCarouselFolderTitleColor() {
		return getHeadingColor();
	}

	@Override
	public Color getCarouselProgramPosterBackgroundColorDark() {
		return COLOR_0;
	}

	@Override
	public Color getCarouselProgramPosterBackgroundColorBright() {
		return COLOR_18;
	}

	@Override
	public Font getCarouselProgramTitleFont() {
		return getHeadingFont();
	}

	@Override
	public Color getCarouselProgramTitleColor() {
		return getHeadingColor();
	}

	@Override
	public Color getCarouselProgramTitleBackgroundColor() {
		return ColorUtils.setTransparency(COLOR_0, 0.3f);
	}

	@Override
	public float getCarouselProgramTitleRelativeVerticalPosition() {
		return 0f;
	}

	@Override
	public Color getCarouselCursorColor() {
		return getCursorColor();
	}

	@Override
	public Color getCarouselOutlineBorderColor() {
		return getOutlineBorderColor();
	}

	@Override
	public Color getCarouselOutlineCursorBorderColor() {
		return getOutlineCursorBorderColor();
	}

	@Override
	public Color getCarouselOutlineCursorFillColor() {
		return getOutlineCursorFillColor();
	}

	@Override
	public Font getBreadcrumbFont() {
		return breadcrumbFont;
	}

	@Override
	public Color getBreadcrumbCursorColor() {
		return getCursorColor();
	}

	@Override
	public Color getBreadcrumbFolderColor() {
		return COLOR_5;
	}

	@Override
	public Color getBreadcrumbSelectedFolderColor() {
		return COLOR_15;
	}

	@Override
	public Color getBreadcrumbSeparatorColor() {
		return COLOR_3;
	}

	protected Color getCursorColor() {
		return COLOR_14;
	}

	protected Color getOutlineBorderColor() {
		return COLOR_3;
	}

	protected Color getOutlineCursorBorderColor() {
		return COLOR_4;
	}

	protected Color getOutlineCursorFillColor() {
		return COLOR_3;
	}

	protected Font getFolderInfoFont() {
		return getGraphicsContext().getSystemFont();
	}

	private AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

}