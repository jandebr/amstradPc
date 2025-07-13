package org.maia.amstrad.gui.browser.carousel.animation;

import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CarouselAnimationFactory {

	private static CarouselAnimationFactory instance;

	private CarouselAnimationFactory() {
	}

	public CarouselAnimation createAnimationToEnterFolder(FolderNode folderNode, CarouselHost host) {
		return new CarouselAnimationDummy(host);
	}

	public CarouselAnimation createAnimationToRunProgram(ProgramNode programNode, CarouselHost host) {
		return new CarouselAnimationDummy(host);
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