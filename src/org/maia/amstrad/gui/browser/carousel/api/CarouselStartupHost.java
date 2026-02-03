package org.maia.amstrad.gui.browser.carousel.api;

import java.awt.Color;

public interface CarouselStartupHost extends CarouselHost {

	void pauseBuildingUI();

	void resumeBuildingUI();

	Color getDisplayBackgroundColor();

	void setDisplayBackgroundColor(Color color);

}