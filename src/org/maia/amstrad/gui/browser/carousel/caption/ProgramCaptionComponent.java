package org.maia.amstrad.gui.browser.carousel.caption;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;

import org.maia.amstrad.gui.browser.carousel.info.InfoSection;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.util.StringUtils;

public class ProgramCaptionComponent extends CarouselCaptionComponent {

	private AmstradProgram program;

	public ProgramCaptionComponent(Dimension captionSize, CarouselProgramBrowserTheme theme, AmstradProgram program,
			List<InfoSection> infoSections) {
		super(captionSize, theme);
		this.program = program;
		buildUI(infoSections);
	}

	protected void buildUI(List<InfoSection> infoSections) {
		buildInfoIconsPanel(infoSections);
		add(Box.createHorizontalStrut(getIconSpacing() * 2));
		buildCaptionText();
		add(Box.createHorizontalGlue());
		fixSize();
	}

	protected void buildInfoIconsPanel(List<InfoSection> infoSections) {
		int iconSpacing = getIconSpacing();
		for (int i = 0; i < infoSections.size(); i++) {
			if (i > 0) {
				add(Box.createHorizontalStrut(iconSpacing));
			}
			add(infoSections.get(i).getIcon());
		}
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

	protected int getIconSpacing() {
		return Math.max(Math.min(getCaptionHeight() / 6, 8), 2);
	}

	public AmstradProgram getProgram() {
		return program;
	}

}