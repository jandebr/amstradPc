package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImage;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CarouselProgramItem extends CarouselRepositoryItem {

	private boolean previousRunFailed;

	public CarouselProgramItem(ProgramNode programNode, CarouselComponent carouselComponent,
			AmstradProgramCoverImageProducer coverImageProducer, Insets margin) {
		this(programNode, carouselComponent, new AmstradProgramCoverImage(programNode, coverImageProducer), margin);
	}

	public CarouselProgramItem(ProgramNode programNode, CarouselComponent carouselComponent,
			AmstradProgramCoverImage coverImage, Insets margin) {
		super(programNode, carouselComponent, coverImage, margin);
	}

	@Override
	public void execute(CarouselHost host) {
		AmstradProgram program = getProgramNode().getProgram();
		if (program != null) {
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