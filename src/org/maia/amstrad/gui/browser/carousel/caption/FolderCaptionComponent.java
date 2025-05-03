package org.maia.amstrad.gui.browser.carousel.caption;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;

import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public class FolderCaptionComponent extends CarouselCaptionComponent {

	private FolderNode folder;

	public FolderCaptionComponent(Dimension captionSize, CarouselProgramBrowserTheme theme, FolderNode folder) {
		super(captionSize, theme);
		this.folder = folder;
		buildUI();
	}

	protected void buildUI() {
		buildCaptionText();
		add(Box.createHorizontalGlue());
		fixSize();
	}

	protected void buildCaptionText() {
		Font font = getTheme().getCaptionFont();
		Color c1 = getTheme().getFolderInfoSubfolderColor();
		Color c2 = getTheme().getFolderInfoProgramColor();
		Color c3 = getTheme().getCaptionConjunctionColor();
		int subfs = getSubfolderCount();
		int progs = getProgramCount();
		if (!isFull()) {
			if (subfs == 0 && progs == 0) {
				addTextElement("empty", c3, font);
			} else {
				addTextElement("contains ", c3, font);
			}
		}
		if (!isFull() && subfs > 0) {
			addTextElement(String.valueOf(subfs) + " folder" + (subfs > 1 ? "s" : ""), c1, font);
			if (!isFull() && progs > 0) {
				addTextElement(" and ", c3, font);
			}
		}
		if (!isFull() && progs > 0) {
			addTextElement(String.valueOf(progs) + " program" + (progs > 1 ? "s" : ""), c2, font);
		}
	}

	protected int getSubfolderCount() {
		int count = 0;
		for (Node node : getFolder().getChildNodes()) {
			if (node.isFolder())
				count++;
		}
		return count;
	}

	protected int getProgramCount() {
		int count = 0;
		for (Node node : getFolder().getChildNodes()) {
			if (node.isProgram())
				count++;
		}
		return count;
	}

	public FolderNode getFolder() {
		return folder;
	}

}