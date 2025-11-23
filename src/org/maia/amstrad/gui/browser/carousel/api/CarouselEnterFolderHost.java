package org.maia.amstrad.gui.browser.carousel.api;

import org.maia.amstrad.gui.browser.carousel.action.CarouselEnterFolderAction;

public interface CarouselEnterFolderHost extends CarouselHost {

	void notifyEnterFolderCompleted(CarouselEnterFolderAction action);

	void notifyEnterFolderCancelled(CarouselEnterFolderAction action);

}