package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.ImageUtils;

public class CarouselProgramItem extends CarouselRepositoryItem {

	private boolean previousRunFailed;

	private static Image coverImage = ImageUtils
			.readFromResource("org/maia/amstrad/gui/browser/carousel/item/woody.png");

	public CarouselProgramItem(ProgramNode programNode, CarouselComponent carouselComponent, Dimension size,
			Insets margin, Font font) {
		super(programNode, carouselComponent, size, margin, font);
	}

	@Override
	public void execute() {
		AmstradProgram program = getProgramNode().getProgram();
		if (program != null) {
			getHost().runProgram(program);
		}
	}

	@Override
	protected Color getBackgroundColor() {
		if (isPreviousRunFailed()) {
			return Color.RED;
		} else {
			return new Color(7, 5, 38);
		}
	}

	@Override
	protected Image getCoverImage() {
		return coverImage;
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