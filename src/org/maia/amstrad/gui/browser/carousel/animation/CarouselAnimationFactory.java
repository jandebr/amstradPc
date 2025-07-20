package org.maia.amstrad.gui.browser.carousel.animation;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CarouselAnimationFactory {

	private static CarouselAnimationFactory instance;

	private CarouselAnimationFactory() {
	}

	public CarouselAnimation createAnimationToStartup(CarouselHost host) {
		CarouselAnimation animation = new CarouselStartupAnimation(host);
		animation.setMinimumDelayMillis(0L); // instant
		animation.setMinimumDurationMillis(3000L);
		return animation;
	}

	public CarouselAnimation createAnimationToEnterFolder(FolderNode folderNode, CarouselHost host) {
		CarouselAnimation animation = new CarouselFolderAnimation(host, host.getCarouselItemBounds(folderNode));
		animation.setMinimumDelayMillis(400L); // delayed
		animation.setMinimumDurationMillis(1000L);
		return animation;
	}

	public CarouselAnimation createAnimationToRunProgram(ProgramNode programNode, CarouselHost host) {
		CarouselAnimation animation = new CarouselProgramAnimation(host, host.getCarouselItemBounds(programNode));
		animation.setMinimumDelayMillis(0L); // instant
		animation.setMinimumDurationMillis(1000L);
		return animation;
	}

	public static CarouselAnimationFactory getInstance() {
		if (instance == null) {
			setInstance(new CarouselAnimationFactory());
		}
		return instance;
	}

	private static synchronized void setInstance(CarouselAnimationFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

}