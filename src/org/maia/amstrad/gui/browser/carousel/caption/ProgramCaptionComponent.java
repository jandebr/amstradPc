package org.maia.amstrad.gui.browser.carousel.caption;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;

import org.maia.amstrad.gui.browser.carousel.CarouselComponentFactory;
import org.maia.amstrad.gui.browser.carousel.info.InfoSection;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.util.StringUtils;

public class ProgramCaptionComponent extends CarouselCaptionComponent {

	private AmstradProgram program;

	public ProgramCaptionComponent(CarouselComponentFactory factory, AmstradProgram program,
			List<InfoSection> infoSections) {
		super(factory);
		this.program = program;
		buildUI(infoSections);
	}

	protected void buildUI(List<InfoSection> infoSections) {
		JComponent iconsPanel = null;
		if (infoSections.size() > 1) {
			iconsPanel = buildInfoIconsPanel(infoSections);
			add(iconsPanel);
		}
		buildCaptionText();
		add(Box.createHorizontalGlue());
		if (iconsPanel != null) {
			remove(iconsPanel);
			add(iconsPanel);
		}
		fixSize();
	}

	protected void buildCaptionText() {
		Font font = getTheme().getCaptionFont();
		Color c1 = getTheme().getCaptionColor();
		Color c2 = getTheme().getCaptionConjunctionColor();
		String author = getProgram().getAuthor();
		int year = getProgram().getProductionYear();
		if (!isFull() && !StringUtils.isEmpty(author)) {
			addTextElement("by ", c2, font);
			if (!isFull()) {
				addTextElement(author, c1, font);
				if (!isFull() && year > 0) {
					addTextElement(" , ", c2, font);
				}
			}
		}
		if (!isFull() && year > 0) {
			addTextElement(String.valueOf(year), c1, font);
		}
	}

	public AmstradProgram getProgram() {
		return program;
	}

}