package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImage;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.swing.image.ImageComponent;
import org.maia.swing.layout.FillMode;
import org.maia.util.ColorUtils;

public class CarouselProgramItem extends CarouselRepositoryItem {

	private boolean previousRunFailed;

	private static Image errorOverlayImage;

	private static Image noLaunchOverlayImage;

	public CarouselProgramItem(ProgramNode programNode, CarouselComponent carouselComponent,
			AmstradProgramCoverImageProducer coverImageProducer, Insets margin) {
		this(programNode, carouselComponent, new AmstradProgramCoverImage(programNode, coverImageProducer), margin);
	}

	public CarouselProgramItem(ProgramNode programNode, CarouselComponent carouselComponent,
			AmstradProgramCoverImage coverImage, Insets margin) {
		super(programNode, carouselComponent, coverImage, margin);
	}

	@Override
	protected void doRenderItem(Graphics2D g, Image coverImage) {
		super.doRenderItem(g, coverImage);
		Image overlayImage = getOverlayImage();
		if (overlayImage != null) {
			renderOverlayImage(g, overlayImage);
		}
	}

	protected void renderOverlayImage(Graphics2D g, Image overlayImage) {
		Color bg = ColorUtils.setTransparency(getCoverImage().getBackgroundColor(), 0.1f);
		ImageComponent overlay = new ImageComponent(overlayImage, false, bg);
		overlay.setSize(getSize());
		overlay.setFillMode(FillMode.FIT);
		overlay.paint(g);
	}

	@Override
	public boolean isExecutable() {
		if (isNoLaunch()) {
			return false;
		} else if (getCarouselHost().getRunProgramActionInProgress() != null) {
			// only one run at a time
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void doExecute(CarouselHost host) {
		super.doExecute(host);
		host.runProgramAsync(getProgramNode());
	}

	protected Image getOverlayImage() {
		if (isPreviousRunFailed()) {
			return getErrorOverlayImage();
		} else if (isNoLaunch()) {
			return getNoLaunchOverlayImage();
		} else {
			return null;
		}
	}

	private static Image getErrorOverlayImage() {
		if (errorOverlayImage == null) {
			errorOverlayImage = UIResources.loadImage("covers/overlay-error-300x150.png");
		}
		return errorOverlayImage;
	}

	private static Image getNoLaunchOverlayImage() {
		if (noLaunchOverlayImage == null) {
			noLaunchOverlayImage = UIResources.loadImage("covers/overlay-lock-300x150.png");
		}
		return noLaunchOverlayImage;
	}

	@Override
	public AmstradProgramCoverImage getCoverImage() {
		return (AmstradProgramCoverImage) super.getCoverImage();
	}

	public ProgramNode getProgramNode() {
		return getRepositoryNode().asProgram();
	}

	public boolean isNoLaunch() {
		AmstradProgram program = getProgramNode().getProgram();
		return program == null || program.isNoLaunch();
	}

	public boolean isPreviousRunFailed() {
		return previousRunFailed;
	}

	public void setPreviousRunFailed(boolean failed) {
		this.previousRunFailed = failed;
	}

}