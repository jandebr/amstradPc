package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.Rectangle;

import org.maia.amstrad.gui.browser.carousel.animation.breadcrumb.CarouselBreadcrumbEnterFolderAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.item.CarouselEnterFolderAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.item.CarouselHighlightItemAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.item.CarouselRunProgramAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.startup.CarouselPortholeStartupAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.startup.CarouselStartupAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.startup.dragon.CarouselDragonFightAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.startup.ninja.CarouselNinjaFightAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.startup.waves.CarouselArcticWavesAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.startup.waves.CarouselTropicWavesAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.startup.waves.CarouselTropicWavesGamifiedAnimation;
import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselFolderItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.util.Randomizer;

public class CarouselAnimationFactory {

	private static CarouselAnimationFactory instance;

	private Randomizer randomizer;

	private CarouselAnimationFactory() {
		this.randomizer = new Randomizer();
	}

	public CarouselStartupAnimation createAnimationToStartup(CarouselHost host) {
		AmstradGraphicsContext graphicsContext = host.getGraphicsContext();
		CarouselPortholeStartupAnimation animation = null;
		int i = getRandomizer().drawIntegerNumber(0, 3);
		if (i == 0) {
			animation = new CarouselArcticWavesAnimation(graphicsContext);
		} else if (i == 1) {
			if (getRandomizer().drawBoolean()) {
				animation = new CarouselTropicWavesAnimation(graphicsContext);
			} else {
				animation = new CarouselTropicWavesGamifiedAnimation(graphicsContext);
			}
		} else if (i == 2) {
			animation = new CarouselNinjaFightAnimation(graphicsContext);
		} else {
			animation = new CarouselDragonFightAnimation(graphicsContext);
		}
		animation.setMinimumDelayMillis(1000L);
		animation.setMinimumDurationMillis(8000L);
		return animation;
	}

	public CarouselAnimation createAnimationToEnterFolder(FolderNode folderNode, CarouselHost host) {
		CarouselBaseAnimation animation = null;
		CarouselItem item = host.getCarouselItem(folderNode);
		Rectangle itemBounds = host.getCarouselItemBounds(folderNode);
		if (item != null && itemBounds != null) {
			animation = new CarouselEnterFolderAnimation((CarouselFolderItem) item, itemBounds);
		} else {
			CarouselBreadcrumbItem breadcrumbItem = host.getBreadcrumbItem(folderNode);
			itemBounds = host.getBreadcrumbItemBounds(folderNode);
			if (breadcrumbItem != null && itemBounds != null) {
				animation = new CarouselBreadcrumbEnterFolderAnimation(breadcrumbItem, itemBounds);
			}
		}
		if (animation != null) {
			animation.setMinimumDelayMillis(400L);
			animation.setMinimumDurationMillis(1000L);
		}
		return animation;
	}

	public CarouselAnimation createAnimationToRunProgram(ProgramNode programNode, CarouselHost host) {
		CarouselRunProgramAnimation animation = null;
		CarouselItem item = host.getCarouselItem(programNode);
		Rectangle itemBounds = host.getCarouselItemBounds(programNode);
		if (item != null && itemBounds != null) {
			animation = new CarouselRunProgramAnimation((CarouselProgramItem) item, itemBounds);
			animation.setMinimumDelayMillis(0L);
			animation.setMinimumDurationMillis(1000L);
		}
		return animation;
	}

	public CarouselAnimation createAnimationToHighlightNode(Node node, CarouselHost host) {
		CarouselHighlightItemAnimation animation = null;
		CarouselItem item = host.getCarouselItem(node);
		Rectangle itemBounds = host.getCarouselItemBounds(node);
		if (item != null && itemBounds != null) {
			animation = new CarouselHighlightItemAnimation(item, itemBounds);
			animation.setMinimumDelayMillis(0L); // instant, builtin random delay in animation
			animation.setMinimumDurationMillis(1000000000L); // a very long time since animation repeats
			animation.setHighlightMinimumDelayMillis(6000L); // 6s
			animation.setHighlightMaximumDelayMillis(12000L); // 12s
			animation.setHighlightDurationMillis(800L); // 0.8s
		}
		return animation;
	}

	private Randomizer getRandomizer() {
		return randomizer;
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