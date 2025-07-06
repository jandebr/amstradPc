package org.maia.amstrad.gui.browser.carousel.theme;

import java.awt.Color;
import java.awt.Font;

public interface CarouselProgramBrowserTheme {

	Color getBackgroundColor();

	Font getDefaultFont();

	Font getHeadingFont();

	Color getHeadingColor();

	Font getCaptionFont();

	Color getCaptionColor();

	Color getCaptionConjunctionColor();

	float getCaptionTextScale();

	float getInfoIconUnselectedScale();

	Color getInfoIconUnselectedBackgroundColor();

	Color getInfoIconSelectedBackgroundColor();

	int getInfoPreferredLinesInView();

	int getInfoInterParagraphSpacing();

	int getInfoInterLineSpacing();

	float getInfoOverflowGradientLines();

	float getInfoMinimumFontSize();

	float getInfoMaximumFontSize();

	Color getInfoOutlineBorderColor();

	Color getInfoOutlineCursorBorderColor();

	Color getInfoOutlineCursorFillColor();

	Font getProgramDescriptionFont();

	Color getProgramDescriptionColor();

	Font getProgramControlHeadingFont();

	Color getProgramControlHeadingColor();

	Font getProgramControlKeyFont();

	Color getProgramControlKeyColor();

	Font getProgramControlDescriptionFont();

	Color getProgramControlDescriptionColor();

	Font getProgramAuthoringFont();

	Color getProgramAuthoringColor();

	Font getProgramAuthoringKeyFont();

	Color getProgramAuthoringKeyColor();

	Font getProgramAuthoringValueFont();

	Color getProgramAuthoringValueColor();

	Font getFolderInfoSubfolderFont();

	Color getFolderInfoSubfolderColor();

	Font getFolderInfoProgramFont();

	Color getFolderInfoProgramColor();

	Font getCarouselEmptyFolderFont();

	Color getCarouselEmptyFolderColor();

	Font getCarouselFolderTitleFont();

	Color getCarouselFolderTitleColor();

	Font getCarouselProgramTitleFont();

	Color getCarouselProgramTitleColor();

	Color getCarouselProgramTitleBackgroundColor();

	float getCarouselProgramTitleRelativeVerticalPosition();

	Color getCarouselCursorColor();

	Color getCarouselOutlineBorderColor();

	Color getCarouselOutlineCursorBorderColor();

	Color getCarouselOutlineCursorFillColor();

	Font getBreadcrumbFont();

	Color getBreadcrumbCursorColor();

	Color getBreadcrumbFolderColor();

	Color getBreadcrumbSelectedFolderColor();

	Color getBreadcrumbSeparatorColor();

}