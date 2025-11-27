package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;
import org.maia.amstrad.gui.browser.carousel.item.CarouselFolderItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CarouselAnimationFactory {

	private static CarouselAnimationFactory instance;

	private CarouselAnimationFactory() {
	}

	public CarouselAnimation createAnimationToStartup(CarouselHost host) {
		CarouselAnimation animation = new CarouselStartupAnimation();
		animation.setMinimumDelayMillis(0L); // instant
		animation.setMinimumDurationMillis(300L); // TODO
		return animation;
	}

	public CarouselAnimation createAnimationToEnterFolder(FolderNode folderNode, CarouselHost host) {
		CarouselAnimation animation = new CarouselFolderAnimation((CarouselFolderItem) host.getCarouselItem(folderNode),
				host.getCarouselItemBounds(folderNode));
		animation.setMinimumDelayMillis(400L); // delayed
		animation.setMinimumDurationMillis(1000L);
		return animation;
	}

	public CarouselAnimation createAnimationToRunProgram(ProgramNode programNode, CarouselHost host) {
		CarouselAnimation animation = new CarouselProgramAnimation(
				(CarouselProgramItem) host.getCarouselItem(programNode), host.getCarouselItemBounds(programNode));
		animation.setMinimumDelayMillis(0L); // instant
		animation.setMinimumDurationMillis(1000L);
		return animation;
	}

	public CarouselAnimation createAnimationToHighlightNode(Node node, CarouselHost host) {
		CarouselItem item = host.getCarouselItem(node);
		Rectangle itemBounds = host.getCarouselItemBounds(node);
		CarouselItemHighlightAnimation animation = new CarouselItemHighlightAnimation(item, itemBounds);
		animation.setMinimumDelayMillis(0L); // instant, builtin random delay in animation
		animation.setMinimumDurationMillis(1000000000L); // a very long time, animation repeats
		animation.setHighlightMinimumDelayMillis(6000L); // 6s
		animation.setHighlightMaximumDelayMillis(12000L); // 12s
		animation.setHighlightDurationMillis(800L); // 0.8s
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