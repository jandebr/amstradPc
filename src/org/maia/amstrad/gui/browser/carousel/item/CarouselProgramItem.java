package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImage;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.image.ImageComponent;
import org.maia.swing.layout.FillMode;
import org.maia.util.ColorUtils;

public class CarouselProgramItem extends CarouselRepositoryItem {

	private boolean previousRunFailed;

	private static Image errorOverlayImage = UIResources.loadImage("covers/error-overlay-160x160.png");

	public CarouselProgramItem(ProgramNode programNode, CarouselComponent carouselComponent,
			AmstradProgramCoverImageProducer coverImageProducer, Insets margin) {
		this(programNode, carouselComponent, new AmstradProgramCoverImage(programNode, coverImageProducer), margin);
	}

	public CarouselProgramItem(ProgramNode programNode, CarouselComponent carouselComponent,
			AmstradProgramCoverImage coverImage, Insets margin) {
		super(programNode, carouselComponent, coverImage, margin);
	}

	@Override
	public void render(Graphics2D g, SlidingItemListComponent component) {
		super.render(g, component);
		if (isPreviousRunFailed()) {
			renderErrorOverlay(g);
		}
	}

	protected void renderErrorOverlay(Graphics2D g) {
		Color bg = ColorUtils.setTransparency(getCoverImage().getBackgroundColor(), 0.1f);
		ImageComponent overlay = new ImageComponent(errorOverlayImage, false, bg);
		overlay.setSize(getSize());
		overlay.setFillMode(FillMode.FIT_DOWNSCALE);
		overlay.paint(g);
	}

	@Override
	public void execute(CarouselHost host) {
		AmstradProgram program = getProgramNode().getProgram();
		if (program != null && !program.isNoLaunch()) {
			host.runProgram(program);
		}
	}

	@Override
	public AmstradProgramCoverImage getCoverImage() {
		return (AmstradProgramCoverImage) super.getCoverImage();
	}

	public ProgramNode getProgramNode() {
		return getRepositoryNode().asProgram();
	}

	public boolean isPreviousRunFailed() {
		return previousRunFailed;
	}

	public void setPreviousRunFailed(boolean failed) {
		this.previousRunFailed = failed;
		getCarouselComponent().refreshUI();
	}

}