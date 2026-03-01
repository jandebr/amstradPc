package org.maia.amstrad.gui.browser.carousel.action;

import java.awt.Color;

import org.maia.amstrad.gui.browser.carousel.animation.startup.CarouselStartupAnimation;
import org.maia.amstrad.gui.browser.carousel.api.CarouselStartupHost;

public class CarouselStartupAction extends CarouselAction {

	private Color originalDisplayBackgroundColor;

	public CarouselStartupAction(CarouselStartupHost host, CarouselStartupAnimation animation) {
		super(host, animation);
	}

	@Override
	protected void doPerform() {
		getHost().pauseBuildingUI();
		setOriginalDisplayBackgroundColor(getHost().getDisplayBackgroundColor());
		getHost().setDisplayBackgroundColor(getAnimation().getDisplayBackgroundColor());
	}

	@Override
	public synchronized void stopAnimation() {
		super.stopAnimation();
		getHost().resumeBuildingUI();
		getHost().setDisplayBackgroundColor(getOriginalDisplayBackgroundColor());
	}

	@Override
	protected CarouselStartupHost getHost() {
		return (CarouselStartupHost) super.getHost();
	}

	@Override
	public CarouselStartupAnimation getAnimation() {
		return (CarouselStartupAnimation) super.getAnimation();
	}

	private Color getOriginalDisplayBackgroundColor() {
		return originalDisplayBackgroundColor;
	}

	private void setOriginalDisplayBackgroundColor(Color color) {
		this.originalDisplayBackgroundColor = color;
	}

}