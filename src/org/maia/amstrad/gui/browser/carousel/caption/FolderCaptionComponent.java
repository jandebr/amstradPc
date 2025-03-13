package org.maia.amstrad.gui.browser.carousel.caption;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;

import org.maia.amstrad.gui.browser.carousel.CarouselComponentFactory;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public class FolderCaptionComponent extends CarouselCaptionComponent {

	private FolderNode folder;

	public FolderCaptionComponent(CarouselComponentFactory factory, FolderNode folder) {
		super(factory);
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
		Color c1 = getTheme().getCaptionColor();
		Color c2 = getTheme().getCaptionConjunctionColor();
		int subfs = getSubfolderCount();
		int progs = getProgramCount();
		if (!isFull()) {
			if (subfs == 0 && progs == 0) {
				addTextElement("empty", c2, font);
			} else {
				addTextElement("contains ", c2, font);
			}
		}
		if (!isFull() && subfs > 0) {
			addTextElement(String.valueOf(subfs) + " folder" + (subfs > 1 ? "s" : ""), c1, font);
			if (!isFull() && progs > 0) {
				addTextElement(" and ", c2, font);
			}
		}
		if (!isFull() && progs > 0) {
			addTextElement(String.valueOf(progs) + " program" + (progs > 1 ? "s" : ""), c1, font);
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